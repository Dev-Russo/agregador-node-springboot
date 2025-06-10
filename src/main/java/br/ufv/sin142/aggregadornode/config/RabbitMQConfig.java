package br.ufv.sin142.aggregadornode.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Injeta o nome da fila do seu application.properties
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    /**
     * Este método cria um "Bean" da fila.
     * Quando o Spring Boot iniciar, ele executará este método e garantirá que
     * uma fila com o nome 'lotes_de_dados' exista no RabbitMQ.
     * Se não existir, ela será criada.
     *
     * @return um objeto Queue que representa nossa fila de dados.
     */
    @Bean
    public Queue dataQueue() {
        // durable() garante que a fila e suas mensagens sobrevivam a reinicializações do RabbitMQ
        return QueueBuilder.durable(queueName).build();
    }
}