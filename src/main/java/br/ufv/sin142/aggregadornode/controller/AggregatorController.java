package br.ufv.sin142.aggregadornode.controller;

import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults; // Novo DTO de saída
import br.ufv.sin142.aggregadornode.service.AggregationService;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/aggregator")
public class AggregatorController {

    @Autowired
    private  AggregationService aggregationService;

    @GetMapping("/results")
    public ResponseEntity<OverallAggregatedResults> getAggregatedResults() { // Alterado o tipo de retorno
        OverallAggregatedResults results = aggregationService.getAggregatedResults();
        return ResponseEntity.ok(results);
    }
}