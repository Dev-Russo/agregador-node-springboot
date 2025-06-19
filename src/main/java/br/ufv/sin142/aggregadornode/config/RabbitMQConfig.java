package br.ufv.sin142.aggregadornode.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.queue.websocket.name}")
    private String queueWebsocketName;

    @Bean
    public Queue dataQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Queue filaWebSocketCandidatos(){
        return new Queue(queueWebsocketName, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("exchange-all");
    }

    @Bean
    public Binding WebSocketBinding() {
        return BindingBuilder.bind(filaWebSocketCandidatos())
                .to(fanoutExchange());
    }
}