package br.ufv.sin142.aggregadornode.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Este DTO será uma lista diretamente, conforme seu exemplo de agregado
// Se preferir um objeto wrapper, poderia ser:
// public class OverallAggregatedResults {
//     private List<TypeSpecificAggregatedResult> aggregatedData;
// }
// Mas, para corresponder ao seu exemplo `const agregado = [ ... ]`, a resposta da API pode ser List<TypeSpecificAggregatedResult>
// Por simplicidade no controller, vamos definir um DTO wrapper.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverallAggregatedResults {
     private List<TypeSpecificAggregatedResult> dadosAgregados;
     private long totalLotesProcessadosGlobal;     // Novo campo
     private long totalItensDeDadosProcessadosGlobal; // Novo campo
}