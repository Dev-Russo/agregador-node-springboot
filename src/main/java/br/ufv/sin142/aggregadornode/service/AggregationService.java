package br.ufv.sin142.aggregadornode.service;

import br.ufv.sin142.aggregadornode.model.*; // Importando todos os modelos necessários
import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity; // Importando a entidade
import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity; // Importando a entidade
import br.ufv.sin142.aggregadornode.repository.GenericDataRecordRepository; // Importando o repositório
import br.ufv.sin142.aggregadornode.repository.ProcessedBatchRepository; // Importando o repositório
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe para transações

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregationService {

    // 1. Repositórios injetados para interagir com o banco de dados.
    // O 'final' garante que eles sejam inicializados no construtor.
    private final ProcessedBatchRepository processedBatchRepository;
    private final GenericDataRecordRepository genericDataRecordRepository;

    // 2. Injeção de dependência via construtor. É uma boa prática do Spring.
    //@Autowired
    public AggregationService(ProcessedBatchRepository processedBatchRepository,
                              GenericDataRecordRepository genericDataRecordRepository) {
        this.processedBatchRepository = processedBatchRepository;
        this.genericDataRecordRepository = genericDataRecordRepository;
    }

    // 3. O método para processar lotes agora é transacional.
    // Ou todas as operações de banco de dados funcionam, ou nenhuma é aplicada.
    @Transactional
    public boolean processDataBatch(IncomingDataBatch batch) {
        if (batch == null || batch.getBatchId() == null || batch.getBatchId().trim().isEmpty() ||
            batch.getDataPoints() == null) {
            System.err.println("Lote inválido: Dados do lote ausentes ou incompletos.");
            return false;
        }

        // 4. Verificação de duplicatas agora usa o banco de dados.
        if (processedBatchRepository.existsById(batch.getBatchId())) {
            System.out.println("Lote duplicado recebido, ignorando: " + batch.getBatchId());
            return false;
        }

        // 5. Salva o ID do lote no banco de dados para evitar futuros processamentos duplicados.
        processedBatchRepository.save(new ProcessedBatchEntity(batch.getBatchId()));

        int itemsProcessedInThisBatch = 0;
        for (GenericDataItem item : batch.getDataPoints()) {
            if (item.getType() == null || item.getType().trim().isEmpty() ||
                item.getObjectIdentifier() == null || item.getObjectIdentifier().trim().isEmpty()) {
                System.err.println("Item de dado genérico inválido no lote " + batch.getBatchId() + ": Tipo ou Identificador do Objeto ausente.");
                continue;
            }

            // 6. Para cada item, cria uma entidade e a salva no banco de dados.
            GenericDataRecordEntity record = new GenericDataRecordEntity();
            record.setDataType(item.getType());
            record.setObjectIdentifier(item.getObjectIdentifier());
            record.setValor(item.getValor());
            record.setEventDatetime(item.getDatetime());
            record.setBatchId(batch.getBatchId());

            genericDataRecordRepository.save(record);
            itemsProcessedInThisBatch++;
        }

        System.out.println("Lote " + batch.getBatchId() + " processado e salvo no banco com sucesso. Itens processados: " + itemsProcessedInThisBatch);
        return true;
    }

    // 7. O método para obter resultados agora também é transacional e marcado como 'readOnly' para otimização.
    @Transactional(readOnly = true)
    public OverallAggregatedResults getAggregatedResults() {
        List<TypeSpecificAggregatedResult> finalAggregatedList = new ArrayList<>();

        // 8. Busca todos os registros do banco de uma vez. Para volumes de dados gigantes,
        // esta estratégia pode ser otimizada, mas é um ótimo ponto de partida.
        List<GenericDataRecordEntity> allRecords = genericDataRecordRepository.findAll();

        // 9. Agrupa os registros em Java, similar ao que era feito com o mapa em memória.
        Map<String, List<GenericDataRecordEntity>> dataGroupedByType =
                allRecords.stream().collect(Collectors.groupingBy(GenericDataRecordEntity::getDataType));

        dataGroupedByType.forEach((type, recordsForType) -> {
            double somatorioTotalDoTipo = recordsForType.stream()
                    .mapToDouble(GenericDataRecordEntity::getValor)
                    .sum();

            Map<String, List<GenericDataRecordEntity>> recordsByObjectIdentifier = recordsForType.stream()
                    .collect(Collectors.groupingBy(GenericDataRecordEntity::getObjectIdentifier));

            List<AggregatedStatisticItem> statisticsListForType = new ArrayList<>();
            recordsByObjectIdentifier.forEach((objectIdentifier, records) -> {
                List<Double> values = records.stream()
                        .map(GenericDataRecordEntity::getValor)
                        .sorted() // Ordena os valores para o cálculo da mediana
                        .collect(Collectors.toList());

                long contagem = values.size();
                if (contagem == 0) return;

                double somatorio = values.stream().mapToDouble(Double::doubleValue).sum();
                double media = somatorio / contagem;
                double mediana;
                if (contagem % 2 == 0) {
                    mediana = (values.get((int) (contagem / 2) - 1) + values.get((int) (contagem / 2))) / 2.0;
                } else {
                    mediana = values.get((int) (contagem / 2));
                }
                double porcentagem = (somatorioTotalDoTipo != 0) ? (somatorio / somatorioTotalDoTipo) * 100.0 : 0;

                statisticsListForType.add(new AggregatedStatisticItem(
                        objectIdentifier, media, mediana, somatorio, contagem, porcentagem
                ));
            });

            if (!statisticsListForType.isEmpty()) {
                finalAggregatedList.add(new TypeSpecificAggregatedResult(type, statisticsListForType));
            }
        });

        // 10. Obtém as contagens globais diretamente do banco de dados.
        long totalGlobalLotes = processedBatchRepository.count();
        long totalGlobalItens = genericDataRecordRepository.count();

        return new OverallAggregatedResults(finalAggregatedList, totalGlobalLotes, totalGlobalItens);
    }
}