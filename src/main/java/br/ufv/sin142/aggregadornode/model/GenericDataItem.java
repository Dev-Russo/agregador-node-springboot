package br.ufv.sin142.aggregadornode.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericDataItem {
    private String type;             // Ex: "votacao_api", "iot"
    private String objectIdentifier; // Ex: id_candidato_A, nome_da_cidade_X
    private double valor;            // O valor numérico a ser agregado
    private long datetime;           // Timestamp da ocorrência
}