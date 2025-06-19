package br.ufv.sin142.aggregadornode.model.entity; // ou model.entity

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "generic_data_records", indexes = { // Adicionando índices para otimizar consultas
    @Index(name = "idx_datatype_objectidentifier", columnList = "dataType, objectIdentifier")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericDataRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chave primária auto-incrementada

    private String dataType;
    private String objectIdentifier;
    private double valor;
    @Column(name = "event_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime eventDatetime;
    private String batchId;
}