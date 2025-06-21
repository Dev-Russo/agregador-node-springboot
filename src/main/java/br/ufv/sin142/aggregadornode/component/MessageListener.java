package br.ufv.sin142.aggregadornode.component;

import br.ufv.sin142.aggregadornode.model.IncomingDataBatch;
import br.ufv.sin142.aggregadornode.service.AggregationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * MUDANÇA 1: Adicionada a anotação @Profile("primary").
 * Isso diz ao Spring para SÓ ativar esta classe se o perfil "primary" estiver ativo,
 * garantindo que apenas o nó primário escute a fila principal de dados.
 */
@Profile("primary")
@Component
public class MessageListener {
    private static final Logger logger = LogManager.getLogger(MessageListener.class);

    // MUDANÇA 2: As dependências agora são injetadas via construtor (melhor prática).
    private final AggregationService aggregationService;
    private final Publisher publisher;
    private final RabbitTemplate rabbitTemplate; // Ferramenta para enviar mensagens ao RabbitMQ.

    @Autowired
    public MessageListener(AggregationService aggregationService, Publisher publisher, RabbitTemplate rabbitTemplate) {
        this.aggregationService = aggregationService;
        this.publisher = publisher;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(IncomingDataBatch batch) {
        logger.info(">>> MENSAGEM RECEBIDA DA FILA RABBITMQ! (Nó Primário) <<<");
        try {
            // Lógica existente: processa o lote e publica para o WebSocket.
            aggregationService.processDataBatch(batch);
            publisher.publicar();
            logger.info("Dados atualizados publicados na fila do WebSocket");

            /*
             * MUDANÇA 3: A nova lógica de replicação.
             * Após processar com sucesso, envia o mesmo lote para o exchange de replicação.
             */
            rabbitTemplate.convertAndSend("replication.exchange", "", batch);
            logger.info("Lote {} encaminhado para o canal de replicação.", batch.getBatchId());

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem da fila: {}", e.getMessage());
        }
    }
}