package br.ufv.sin142.aggregadornode.component;

import br.ufv.sin142.aggregadornode.model.IncomingDataBatch;
import br.ufv.sin142.aggregadornode.service.AggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    private static final Logger logger = LogManager.getLogger(MessageListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AggregationService aggregationService;

    @Autowired
    private Publisher publisher;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(IncomingDataBatch batch) {
        logger.info(">>> MENSAGEM RECEBIDA DA FILA RABBITMQ! <<<");
        try {
            aggregationService.processDataBatch(batch);
            publisher.publicar();
            logger.info("Dados atualizados publicados na fila");

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem da fila: {}", e.getMessage());
        }
    }
}
