package br.ufv.sin142.aggregadornode.component;

import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSender {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarResultados(OverallAggregatedResults resultados) {
        messagingTemplate.convertAndSend("/topic/aggregated", resultados);
    }
}