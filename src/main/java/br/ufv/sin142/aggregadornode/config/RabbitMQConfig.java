package br.ufv.sin142.aggregadornode.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    // 🧠 Esta parte é crucial: registra o conversor no container do listener
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter); // <- aqui o segredo
        return factory;
    }
}