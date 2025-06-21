package br.ufv.sin142.aggregadornode.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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

    // --- Filas e Exchanges Principais ---

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.queue.websocket.name}")
    private String queueWebsocketName;

    @Bean
    public Queue dataQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Queue filaWebSocketCandidatos() {
        return new Queue(queueWebsocketName, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("exchange-all");
    }

    @Bean
    public Binding webSocketBinding() {
        return BindingBuilder.bind(filaWebSocketCandidatos())
                .to(fanoutExchange());
    }

    // --- Beans de Configuração Geral ---

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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    // --- INÍCIO DA NOVA SEÇÃO: CONFIGURAÇÃO DE REPLICAÇÃO ---

    @Value("${rabbitmq.queue.replication.name:replication-queue}")
    private String replicationQueueName;

    @Value("${rabbitmq.exchange.replication.name:replication.exchange}")
    private String replicationExchangeName;

    /**
     * 1. O Exchange (Carteiro): Distribui as mensagens de replicação
     * do nó primário para os secundários.
     */
    @Bean
    public FanoutExchange replicationExchange() {
        return new FanoutExchange(replicationExchangeName);
    }

    /**
     * 2. A Fila (Caixa de Correio): Onde o nó secundário vai escutar
     * para receber as mensagens replicadas.
     */
    @Bean
    public Queue replicationQueue() {
        return QueueBuilder.durable(replicationQueueName).build();
    }

    /**
     * 3. O Binding (Endereçamento): Liga o exchange de replicação à fila de replicação,
     * garantindo que a mensagem chegue ao destino.
     */
    @Bean
    public Binding replicationBinding(Queue replicationQueue, FanoutExchange replicationExchange) {
        return BindingBuilder.bind(replicationQueue).to(replicationExchange);
    }
}