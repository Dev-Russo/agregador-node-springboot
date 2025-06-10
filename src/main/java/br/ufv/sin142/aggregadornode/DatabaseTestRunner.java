package br.ufv.sin142.aggregadornode; // Use seu pacote base

import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity;
import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity;
import br.ufv.sin142.aggregadornode.repository.GenericDataRecordRepository;
import br.ufv.sin142.aggregadornode.repository.ProcessedBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

//@Component // Marca esta classe para ser gerenciada pelo Spring
public class DatabaseTestRunner implements CommandLineRunner {

    @Autowired // Injeta o repositório de lotes processados
    private ProcessedBatchRepository processedBatchRepository;

    @Autowired // Injeta o repositório de registros de dados genéricos
    private GenericDataRecordRepository genericDataRecordRepository;

    @Override
    @Transactional // Garante que as operações de banco de dados ocorram em uma transação
    public void run(String... args) throws Exception {
        System.out.println(">>> INICIANDO TESTE DE BANCO DE DADOS NA INICIALIZAÇÃO <<<");

        // Testa salvar um ProcessedBatchEntity
        try {
            ProcessedBatchEntity testBatch = new ProcessedBatchEntity("TEST_BATCH_001");
            processedBatchRepository.save(testBatch);
            System.out.println("ProcessedBatchEntity salvo com ID: " + testBatch.getBatchId());

            if (processedBatchRepository.existsById("TEST_BATCH_001")) {
                System.out.println("CONFIRMADO: ProcessedBatchEntity TEST_BATCH_001 existe no banco.");
            } else {
                System.err.println("ERRO: ProcessedBatchEntity TEST_BATCH_001 NÃO foi encontrado no banco após salvar.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao testar ProcessedBatchEntity: " + e.getMessage());
            e.printStackTrace();
        }

        // Testa salvar um GenericDataRecordEntity
        try {
            GenericDataRecordEntity testRecord = new GenericDataRecordEntity();
            // testRecord.setId(null); // O ID é auto-gerado, não precisa setar
            testRecord.setDataType("teste_tipo");
            testRecord.setObjectIdentifier("teste_objeto_id");
            testRecord.setValor(123.45);
            testRecord.setEventDatetime(System.currentTimeMillis());
            testRecord.setBatchId("TEST_BATCH_001"); // Pode relacionar com o lote de teste

            GenericDataRecordEntity savedRecord = genericDataRecordRepository.save(testRecord);
            System.out.println("GenericDataRecordEntity salvo com ID gerado: " + savedRecord.getId());

            if (savedRecord.getId() != null && genericDataRecordRepository.existsById(savedRecord.getId())) {
                System.out.println("CONFIRMADO: GenericDataRecordEntity com ID " + savedRecord.getId() + " existe no banco.");
            } else {
                System.err.println("ERRO: GenericDataRecordEntity NÃO foi encontrado no banco após salvar.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao testar GenericDataRecordEntity: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(">>> TESTE DE BANCO DE DADOS NA INICIALIZAÇÃO CONCLUÍDO <<<");
    }
}