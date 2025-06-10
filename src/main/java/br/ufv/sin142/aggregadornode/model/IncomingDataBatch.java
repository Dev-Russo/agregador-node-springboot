package br.ufv.sin142.aggregadornode.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingDataBatch {
    private String batchId;      // ID único para este lote de dados
    private String sourceNodeId; // ID do nó coletor que enviou este lote
    private List<GenericDataItem> dataPoints; // Lista dos itens de dados no lote
}
