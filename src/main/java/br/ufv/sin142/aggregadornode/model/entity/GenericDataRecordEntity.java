package br.ufv.sin142.aggregadornode.model.entity; // ou model.entity

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    private String dataType; // Ex: "votacao_api", "iot"
    private String objectIdentifier; // Ex: id_candidato_A, nome_da_cidade_X
    private double valor;
    private long eventDatetime; // Renomeado para clareza com datetime do Java
    private String batchId; // Para rastrear a origem do lote
}