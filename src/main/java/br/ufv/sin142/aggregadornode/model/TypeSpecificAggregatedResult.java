package br.ufv.sin142.aggregadornode.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeSpecificAggregatedResult {
    private String type;
    private List<AggregatedStatisticItem> lista;
}