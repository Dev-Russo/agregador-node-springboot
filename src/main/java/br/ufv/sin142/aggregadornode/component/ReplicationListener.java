package br.ufv.sin142.aggregadornode.component;

import br.ufv.sin142.aggregadornode.model.IncomingDataBatch;
import br.ufv.sin142.aggregadornode.service.AggregationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Anotação @Profile("secondary"):
 * Garante que este listener SÓ será ativado quando a aplicação for iniciada
 * com o perfil "secondary". É o oposto do MessageListener.
 */
@Profile("secondary")
@Component
public class ReplicationListener {
    private static final Logger logger = LogManager.getLogger(ReplicationListener.class);

    private final AggregationService aggregationService;

    @Autowired
    public ReplicationListener(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    /**
     * Anotação @RabbitListener:
     * Escuta a fila de replicação que definimos no RabbitMQConfig.
     * O nome da fila é pego do application.properties, com um valor padrão.
     */
    @RabbitListener(queues = "${rabbitmq.queue.replication.name:replication-queue}")
    public void receiveReplicationMessage(IncomingDataBatch batch) {
        logger.info(">>> MENSAGEM DE REPLICAÇÃO RECEBIDA! (Nó Secundário) Processando lote {} <<<", batch.getBatchId());
        try {
            // A lógica é a mesma do primário: apenas processa e salva o lote.
            // Não precisa publicar no WebSocket, pois essa é a função do primário.
            aggregationService.processDataBatch(batch);
            logger.info("Lote {} replicado e salvo no banco de dados secundário com sucesso.", batch.getBatchId());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de replicação: {}", e.getMessage(), e);
        }
    }
}