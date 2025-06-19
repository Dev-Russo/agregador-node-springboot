package br.ufv.sin142.aggregadornode.websocket;

import br.ufv.sin142.aggregadornode.controller.WebSocketPublisher;
import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    private final WebSocketPublisher webSocketPublisher;
    private static final Logger logger = LogManager.getLogger(Consumer.class);


    public Consumer(WebSocketPublisher webSocketPublisher) {
        this.webSocketPublisher = webSocketPublisher;
    }

    @RabbitListener(queues = "${rabbitmq.queue.websocket.name}")
    public void consumir(@Payload OverallAggregatedResults resultados) {
        logger.info(">>>>> RECEBIDO DO RABBIT, ENVIANDO PARA WEBSOCKET");
        webSocketPublisher.enviarResultados(resultados);
    }
}
