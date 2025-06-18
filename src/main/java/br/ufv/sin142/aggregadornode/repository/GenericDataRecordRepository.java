package br.ufv.sin142.aggregadornode.repository;

import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity; // Importe sua entidade
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Para consultas customizadas, se necessário
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository // Indica ao Spring que esta é uma interface de repositório
public interface GenericDataRecordRepository extends JpaRepository<GenericDataRecordEntity, Long> {

    List<GenericDataRecordEntity> findByDataType(String dataType);

   
    List<GenericDataRecordEntity> findByDataTypeAndObjectIdentifier(String dataType, String objectIdentifier);

    
    @Query("SELECT DISTINCT gdr.dataType FROM GenericDataRecordEntity gdr")
    List<String> findDistinctDataTypes();

   
    @Query("SELECT DISTINCT gdr.objectIdentifier FROM GenericDataRecordEntity gdr WHERE gdr.dataType = :dataType")
    List<String> findDistinctObjectIdentifiersByDataType(String dataType);
}