package br.ufv.sin142.aggregadornode.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedStatisticItem {
    private String objectIdentifier;
    private double media;
    private double mediana;
    private double somatorio;
    private long contagem; // Adicionado para clareza e cálculo de média/porcentagem
    private double porcentagem; // A ser calculado
}