package br.ufv.sin142.aggregadornode.service;

import br.ufv.sin142.aggregadornode.model.*;
import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity;
import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity;
import br.ufv.sin142.aggregadornode.repository.GenericDataRecordRepository;
import br.ufv.sin142.aggregadornode.repository.ProcessedBatchRepository;
import br.ufv.sin142.aggregadornode.websocket.Publisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregationService {

    private final ProcessedBatchRepository processedBatchRepository;
    private final GenericDataRecordRepository genericDataRecordRepository;

    @Autowired
    public AggregationService(ProcessedBatchRepository processedBatchRepository,
                              GenericDataRecordRepository genericDataRecordRepository,
                              ObjectMapper objectMapper
                              ) {
        this.processedBatchRepository = processedBatchRepository;
        this.genericDataRecordRepository = genericDataRecordRepository;
    }

    private static final Logger logger = LogManager.getLogger(AggregationService.class);

    @Transactional
    public void processDataBatch(IncomingDataBatch batch) {
        if (batch == null || batch.getBatchId() == null || batch.getBatchId().trim().isEmpty() ||
            batch.getDataPoints() == null) {
            logger.error("Lote inválido recebido da fila: {}", batch);
            return;
        }

        if (processedBatchRepository.existsById(batch.getBatchId())) {
            logger.warn("Lote duplicado recebido da fila, ignorando: {}", batch.getBatchId());
            return;
        }

        processedBatchRepository.save(new ProcessedBatchEntity(batch.getBatchId()));

        List<GenericDataRecordEntity> recordsToSave = new ArrayList<>();
        for (GenericDataItem item : batch.getDataPoints()) {
            if (item.getType() == null || item.getObjectIdentifier() == null) {
                logger.error("Item inválido no lote da fila: {}", item);
                continue;
            }
            GenericDataRecordEntity record = new GenericDataRecordEntity();
            record.setDataType(item.getType());
            record.setObjectIdentifier(item.getObjectIdentifier());
            record.setValor(item.getValor());
            record.setEventDatetime(item.getEventDatetime());
            record.setBatchId(batch.getBatchId());
            recordsToSave.add(record);

        }
        genericDataRecordRepository.saveAll(recordsToSave);
        logger.info("Lote {}", batch.getBatchId() + " da fila processado e salvo no banco com sucesso.");
    }

    @Transactional(readOnly = true)
    public OverallAggregatedResults getAggregatedResults() {
        List<GenericDataRecordEntity> allRecords = genericDataRecordRepository.findAll();

        Map<String, List<GenericDataRecordEntity>> dataGroupedByType =
                allRecords.stream().collect(Collectors.groupingBy(GenericDataRecordEntity::getDataType));
        
        List<TypeSpecificAggregatedResult> finalAggregatedList = new ArrayList<>();
        dataGroupedByType.forEach((type, recordsForType) -> {
            double somatorioTotalDoTipo = recordsForType.stream().mapToDouble(GenericDataRecordEntity::getValor).sum();
            Map<String, List<GenericDataRecordEntity>> recordsByObjectIdentifier = recordsForType.stream().collect(Collectors.groupingBy(GenericDataRecordEntity::getObjectIdentifier));
            List<AggregatedStatisticItem> statisticsListForType = new ArrayList<>();
            recordsByObjectIdentifier.forEach((objectIdentifier, records) -> {
                List<Double> values = records.stream().map(GenericDataRecordEntity::getValor).sorted().toList();
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