package br.ufv.sin142.aggregadornode.controller;

import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarResultados(OverallAggregatedResults resultados) {
        messagingTemplate.convertAndSend("/topic/aggregated", resultados);
    }
}