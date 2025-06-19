package br.ufv.sin142.aggregadornode.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingDataBatch {
    private String batchId;
    private String sourceNodeId;
    private List<GenericDataItem> dataPoints;
}
