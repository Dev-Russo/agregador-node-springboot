# Agregador de Dados Distribuídos

Backend para receber lotes de dados de nós distribuídos, persistir os registros, calcular estatísticas agregadas e disponibilizar os resultados por HTTP, WebSocket e mensageria.

O projeto é construído com Java e Spring Boot e foi desenvolvido para trabalhar com PostgreSQL, RabbitMQ e múltiplas instâncias com papéis de nó primário e secundário.

## O que o sistema faz

- Recebe lotes de dados pela fila RabbitMQ `lotes_de_dados`;
- Ignora lotes duplicados usando o `batchId` como identificador idempotente;
- Persiste lotes processados e registros genéricos no PostgreSQL;
- Agrupa os dados por `type` e `objectIdentifier`;
- Calcula média, mediana, somatório, contagem e porcentagem por objeto;
- Mantém os resultados em cache por 20 segundos;
- Publica resultados atualizados no exchange RabbitMQ `exchange-all`;
- Envia atualizações em tempo real pelo tópico STOMP `/topic/aggregated`;
- Replica lotes do nó primário para nós secundários por meio de um exchange dedicado.

## Stack

- Java 21;
- Spring Boot 3.5;
- Gradle;
- Spring Web, WebSocket, Data JPA, Security, AMQP, Cache e Actuator;
- PostgreSQL 15;
- RabbitMQ 3 com painel de gerenciamento;
- Hibernate, Caffeine, Jackson, Log4j2 e Micrometer Prometheus;
- Docker e Docker Compose.

## Arquitetura de mensageria

O sistema pode operar com dois perfis:

- `primary`: escuta `lotes_de_dados`, processa o lote, persiste os dados, publica o resultado agregado e envia o lote para replicação;
- `secondary`: escuta `replication-queue` e persiste os lotes replicados sem publicar novamente no WebSocket.

As filas e exchanges principais são:

| Recurso | Finalidade |
| --- | --- |
| `lotes_de_dados` | Entrada de lotes no nó primário |
| `exchange-all` | Distribuição dos resultados agregados |
| `websocket_all` | Recebimento dos resultados que serão enviados ao WebSocket |
| `replication.exchange` | Distribuição dos lotes para replicação |
| `replication-queue` | Entrada padrão do nó secundário |

## Executando localmente

### Pré-requisitos

- JDK 21;
- Docker e Docker Compose;
- Uma rede Docker externa chamada `rede`, usada pelo arquivo `docker-compose.yml`.

Crie a rede uma única vez:

```bash
docker network create rede
```

Suba PostgreSQL, RabbitMQ e a aplicação:

```bash
docker compose up --build
```

Para executar diretamente com o Gradle Wrapper:

```bash
./gradlew bootRun
```

No Windows:

```powershell
.\gradlew.bat bootRun
```

O arquivo `src/main/resources/application.properties` lê as configurações de banco e RabbitMQ por variáveis de ambiente. Em uma implantação, configure também `SPRING_PROFILES_ACTIVE=primary` ou `SPRING_PROFILES_ACTIVE=secondary` para ativar o listener correspondente.

## API HTTP

### Consultar resultados agregados

`GET /api/aggregator/results`

Esse endpoint é público e retorna um objeto com os dados agregados e os totais globais:

```json
{
  "dadosAgregados": [
    {
      "type": "temperature",
      "lista": [
        {
          "objectIdentifier": "sensor-01",
          "media": 23.4,
          "mediana": 23.4,
          "somatorio": 46.8,
          "contagem": 2,
          "porcentagem": 100.0
        }
      ]
    }
  ],
  "totalLotesProcessadosGlobal": 1,
  "totalItensDeDadosProcessadosGlobal": 2
}
```

### Health check e métricas

- `GET /actuator/health`: health check público;
- `GET /actuator/prometheus`: métricas Prometheus públicas;
- demais endpoints `/actuator/**`: exigem autenticação HTTP Basic com papel `ADMIN`.

O endpoint `/ws` também é público e aceita conexões SockJS/STOMP. Clientes devem assinar `/topic/aggregated` para receber os resultados publicados.

## Payload de entrada

O nó primário espera mensagens JSON na fila `lotes_de_dados`:

```json
{
  "batchId": "batch-001",
  "sourceNodeId": "node-01",
  "dataPoints": [
    {
      "type": "temperature",
      "objectIdentifier": "sensor-01",
      "valor": 23.4,
      "eventDatetime": "2026-08-20T12:00:00"
    }
  ]
}
```

Um lote sem `batchId`, sem `dataPoints` ou já processado é rejeitado ou ignorado para preservar a consistência da agregação.

## Testes e build

```bash
./gradlew test
./gradlew build
```

No Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

## Estrutura principal

- `src/main/java/.../controller`: endpoints HTTP;
- `src/main/java/.../service`: processamento e agregação;
- `src/main/java/.../component`: listeners, publishers e integração com RabbitMQ/WebSocket;
- `src/main/java/.../model`: DTOs e entidades;
- `src/main/java/.../config`: RabbitMQ, WebSocket e configurações de infraestrutura;
- `src/main/resources`: propriedades e configuração do Log4j2;
- `docker-compose.yml`: ambiente local com PostgreSQL, RabbitMQ e aplicação.

## Autor

Projeto desenvolvido por [Murilo Russo](https://github.com/Dev-Russo), com foco em Engenharia de Software, sistemas distribuídos, APIs e IA aplicada.

- [LinkedIn](https://www.linkedin.com/in/murilo-russo)
- [GitHub](https://github.com/Dev-Russo)

