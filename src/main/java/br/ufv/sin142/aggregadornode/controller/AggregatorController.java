package br.ufv.sin142.aggregadornode.controller;

// Ajuste os imports para os novos DTOs e o serviço
import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults; // Novo DTO de saída
import br.ufv.sin142.aggregadornode.service.AggregationService;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aggregator")
public class AggregatorController {

    private final AggregationService aggregationService;

    // @Autowired // Pode remover se tiver apenas um construtor
    public AggregatorController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/results")
    public ResponseEntity<OverallAggregatedResults> getAggregatedResults() { // Alterado o tipo de retorno
        OverallAggregatedResults results = aggregationService.getAggregatedResults();
        return ResponseEntity.ok(results);
    }
}