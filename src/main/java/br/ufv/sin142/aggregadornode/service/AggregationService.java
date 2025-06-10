package br.ufv.sin142.aggregadornode.service;

import br.ufv.sin142.aggregadornode.model.*;
import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity;
import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity;
import br.ufv.sin142.aggregadornode.repository.GenericDataRecordRepository;
import br.ufv.sin142.aggregadornode.repository.ProcessedBatchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregationService {

    private final ProcessedBatchRepository processedBatchRepository;
    private final GenericDataRecordRepository genericDataRecordRepository;
    private final ObjectMapper objectMapper; // Para converter JSON para Objeto

    public AggregationService(ProcessedBatchRepository processedBatchRepository,
                              GenericDataRecordRepository genericDataRecordRepository) {
        this.processedBatchRepository = processedBatchRepository;
        this.genericDataRecordRepository = genericDataRecordRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * NOVO MÉTODO: Listener do RabbitMQ.
     * Este método "escuta" a fila definida em application.properties (rabbitmq.queue.name).
     * Ele é o novo ponto de entrada para os dados no agregador.
     * @param messageBody O corpo da mensagem recebida, que é uma string JSON.
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String messageBody) {
        System.out.println(">>> MENSAGEM RECEBIDA DA FILA RABBITMQ! <<<");
        try {
            // Converte a string JSON de volta para o nosso objeto IncomingDataBatch
            IncomingDataBatch batch = objectMapper.readValue(messageBody, IncomingDataBatch.class);
            
            // Chama o nosso método de processamento já existente.
            // Isso é ótimo, pois reutilizamos toda a lógica de persistência!
            processDataBatch(batch);

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem da fila: " + e.getMessage());
            // Em um sistema de produção, você poderia mover a mensagem para uma "dead-letter queue"
            // para análise posterior, em vez de simplesmente descartá-la.
        }
    }
    
    /**
     * Este método agora é chamado internamente pelo listener do RabbitMQ.
     * A lógica de persistência continua a mesma.
     */
    @Transactional
    public void processDataBatch(IncomingDataBatch batch) { // Mudamos para 'void' pois o retorno não é mais usado pela API REST
        if (batch == null || batch.getBatchId() == null || batch.getBatchId().trim().isEmpty() ||
            batch.getDataPoints() == null) {
            System.err.println("Lote inválido recebido da fila: " + batch);
            return;
        }

        if (processedBatchRepository.existsById(batch.getBatchId())) {
            System.out.println("Lote duplicado recebido da fila, ignorando: " + batch.getBatchId());
            return;
        }

        processedBatchRepository.save(new ProcessedBatchEntity(batch.getBatchId()));

        List<GenericDataRecordEntity> recordsToSave = new ArrayList<>();
        for (GenericDataItem item : batch.getDataPoints()) {
            if (item.getType() == null || item.getObjectIdentifier() == null) {
                System.err.println("Item inválido no lote da fila: " + item);
                continue;
            }
            GenericDataRecordEntity record = new GenericDataRecordEntity();
            record.setDataType(item.getType());
            record.setObjectIdentifier(item.getObjectIdentifier());
            record.setValor(item.getValor());
            record.setEventDatetime(item.getDatetime());
            record.setBatchId(batch.getBatchId());
            recordsToSave.add(record);
        }

        genericDataRecordRepository.saveAll(recordsToSave); // Salva todos os registros de uma vez (mais eficiente)
        System.out.println("Lote " + batch.getBatchId() + " da fila processado e salvo no banco com sucesso.");
    }

    /**
     * O método para obter resultados continua exatamente o mesmo, pois ele lê do banco de dados,
     * e não se importa como os dados chegaram lá.
     */
    @Transactional(readOnly = true)
    public OverallAggregatedResults getAggregatedResults() {
        // ... (seu código existente para ler do banco e calcular as estatísticas) ...
        // NENHUMA MUDANÇA NECESSÁRIA AQUI
        List<GenericDataRecordEntity> allRecords = genericDataRecordRepository.findAll();

        Map<String, List<GenericDataRecordEntity>> dataGroupedByType =
                allRecords.stream().collect(Collectors.groupingBy(GenericDataRecordEntity::getDataType));
        
        List<TypeSpecificAggregatedResult> finalAggregatedList = new ArrayList<>();
        dataGroupedByType.forEach((type, recordsForType) -> {
            double somatorioTotalDoTipo = recordsForType.stream().mapToDouble(GenericDataRecordEntity::getValor).sum();
            Map<String, List<GenericDataRecordEntity>> recordsByObjectIdentifier = recordsForType.stream().collect(Collectors.groupingBy(GenericDataRecordEntity::getObjectIdentifier));
            List<AggregatedStatisticItem> statisticsListForType = new ArrayList<>();
            recordsByObjectIdentifier.forEach((objectIdentifier, records) -> {
                List<Double> values = records.stream().map(GenericDataRecordEntity::getValor).sorted().collect(Collectors.toList());
                long contagem = values.size();
                if (contagem > 0) {
                    double somatorio = values.stream().mapToDouble(Double::doubleValue).sum();
                    double media = somatorio / contagem;
                    double mediana = (contagem % 2 == 0) ? (values.get((int) (contagem / 2) - 1) + values.get((int) (contagem / 2))) / 2.0 : values.get((int) (contagem / 2));
                    double porcentagem = (somatorioTotalDoTipo != 0) ? (somatorio / somatorioTotalDoTipo) * 100.0 : 0;
                    statisticsListForType.add(new AggregatedStatisticItem(objectIdentifier, media, mediana, somatorio, contagem, porcentagem));
                }
            });
            if (!statisticsListForType.isEmpty()) {
                finalAggregatedList.add(new TypeSpecificAggregatedResult(type, statisticsListForType));
            }
        });
        
        long totalGlobalLotes = processedBatchRepository.count();
        long totalGlobalItens = genericDataRecordRepository.count();

        return new OverallAggregatedResults(finalAggregatedList, totalGlobalLotes, totalGlobalItens);
    }
}