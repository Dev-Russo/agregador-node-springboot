package br.ufv.sin142.aggregadornode.websocket;

import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults;
import br.ufv.sin142.aggregadornode.service.AggregationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

    private final AggregationService aggregationService;
    private final RabbitTemplate rabbitTemplate;

    public Publisher(@Lazy AggregationService aggregationService, RabbitTemplate rabbitTemplate) {
        this.aggregationService = aggregationService;
        this.rabbitTemplate = rabbitTemplate;

    }

    public void publicar() {
        OverallAggregatedResults overallAggregatedResults = aggregationService.getAggregatedResults();
        rabbitTemplate.convertAndSend("exchange-all", "", overallAggregatedResults);
    }
}
