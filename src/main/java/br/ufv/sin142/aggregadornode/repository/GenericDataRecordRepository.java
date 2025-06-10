package br.ufv.sin142.aggregadornode.repository;

import br.ufv.sin142.aggregadornode.model.entity.GenericDataRecordEntity; // Importe sua entidade
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Para consultas customizadas, se necessário
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository // Indica ao Spring que esta é uma interface de repositório
public interface GenericDataRecordRepository extends JpaRepository<GenericDataRecordEntity, Long> {
    // JpaRepository<GenericDataRecordEntity, Long>
    // 1. GenericDataRecordEntity: É a classe de entidade que este repositório gerenciará.
    // 2. Long: É o tipo do campo ID (chave primária) da entidade GenericDataRecordEntity (que é id, do tipo Long).

    // O Spring Data JPA já fornece os métodos CRUD básicos.

    // Você pode definir métodos de consulta customizados seguindo as convenções de nomenclatura do Spring Data JPA,
    // ou usando a anotação @Query.

    // Exemplo: Encontrar todos os registros por tipo de dado (dataType)
    List<GenericDataRecordEntity> findByDataType(String dataType);

    // Exemplo: Encontrar todos os registros por tipo de dado E identificador do objeto
    List<GenericDataRecordEntity> findByDataTypeAndObjectIdentifier(String dataType, String objectIdentifier);

    // Exemplo de como buscar todos os tipos de dados distintos usando uma @Query
    // Se você precisar apenas dos nomes dos tipos, esta query é mais eficiente
    // do que buscar todas as entidades e depois extrair os tipos em Java.
    @Query("SELECT DISTINCT gdr.dataType FROM GenericDataRecordEntity gdr")
    List<String> findDistinctDataTypes();

    // Exemplo: Encontrar todos os identificadores de objeto distintos para um determinado tipo de dado
    @Query("SELECT DISTINCT gdr.objectIdentifier FROM GenericDataRecordEntity gdr WHERE gdr.dataType = :dataType")
    List<String> findDistinctObjectIdentifiersByDataType(String dataType);
}