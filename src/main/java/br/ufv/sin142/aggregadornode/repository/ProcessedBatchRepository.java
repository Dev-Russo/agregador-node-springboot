package br.ufv.sin142.aggregadornode.repository;

import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity; // Importe sua entidade
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica ao Spring que esta é uma interface de repositório
public interface ProcessedBatchRepository extends JpaRepository<ProcessedBatchEntity, String> {
    // JpaRepository<ProcessedBatchEntity, String>
    // 1. ProcessedBatchEntity: É a classe de entidade que este repositório gerenciará.
    // 2. String: É o tipo do campo ID (chave primária) da entidade ProcessedBatchEntity (que é batchId, do tipo String).

    // O Spring Data JPA já fornece automaticamente métodos como:
    // - save(ProcessedBatchEntity entity)
    // - findById(String batchId)
    // - existsById(String batchId)
    // - findAll()
    // - deleteById(String batchId)
    // - count()
    // e muitos outros! Você não precisa implementá-los.
}