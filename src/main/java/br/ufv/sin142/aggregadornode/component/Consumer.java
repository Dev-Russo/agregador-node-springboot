package br.ufv.sin142.aggregadornode.component;

import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private static final Logger logger = LogManager.getLogger(Consumer.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "${rabbitmq.queue.websocket.name}")
    public void consumir(@Payload OverallAggregatedResults resultados) {
        try{
            logger.info(">>>>> RECEBIDO DO RABBIT, ENVIANDO PARA WEBSOCKET <<<<<<");
            messagingTemplate.convertAndSend("/topic/aggregated", resultados);
            logger.info("Dados enviados via WebSocket com sucesso");
        }catch (Exception e){
            logger.error("Erro ao enviar dados via WebSocket", e);
        }
    }
}
