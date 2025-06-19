package br.ufv.sin142.aggregadornode.repository;

import br.ufv.sin142.aggregadornode.model.entity.ProcessedBatchEntity; // Importe sua entidade
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedBatchRepository extends JpaRepository<ProcessedBatchEntity, String> {}