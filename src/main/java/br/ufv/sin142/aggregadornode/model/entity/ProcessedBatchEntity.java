package br.ufv.sin142.aggregadornode.model.entity; // ou model.entity

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedBatchEntity { // Renomeado para clareza
    @Id
    private String batchId;
}