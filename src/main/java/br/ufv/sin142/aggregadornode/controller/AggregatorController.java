package br.ufv.sin142.aggregadornode.controller;

// Ajuste os imports para os novos DTOs e o serviço
import br.ufv.sin142.aggregadornode.model.IncomingDataBatch; // Já usa GenericDataItem internamente
import br.ufv.sin142.aggregadornode.model.OverallAggregatedResults; // Novo DTO de saída
import br.ufv.sin142.aggregadornode.service.AggregationService;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/data")
    public ResponseEntity<String> receiveDataBatch(@RequestBody IncomingDataBatch batch) {
        // A validação básica do batch (null checks) pode permanecer.
        // O IncomingDataBatch agora espera uma lista de GenericDataItem.
        if (batch == null || batch.getBatchId() == null || batch.getDataPoints() == null) {
            return ResponseEntity.badRequest().body("Lote de dados inválido ou incompleto.");
        }

        boolean success = aggregationService.processDataBatch(batch);

        if (success) {
            return ResponseEntity.accepted().body("Lote " + batch.getBatchId() + " recebido e processamento iniciado.");
        } else {
            // O motivo pode ser duplicata ou falha interna no processamento (embora o processDataBatch atual sempre retorne true se não for duplicado e não tiver erro fatal)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Lote " + batch.getBatchId() + " já processado ou falha ao processar.");
        }
    }

    @GetMapping("/results")
    public ResponseEntity<OverallAggregatedResults> getAggregatedResults() { // Alterado o tipo de retorno
        OverallAggregatedResults results = aggregationService.getAggregatedResults();
        return ResponseEntity.ok(results);
    }
}