package br.ufv.sin142.aggregadornode.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericDataItem {
    private String type;
    private String objectIdentifier;
    private double valor;
    private LocalDateTime eventDatetime;
}