# Agregador de Feeds com Spring Boot 🚀

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=spring) ![Maven](https://img.shields.io/badge/Maven-3.8+-blue?style=for-the-badge&logo=apache-maven) ![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

Um backend robusto e eficiente construído com Java e Spring Boot, projetado para agregar conteúdo de múltiplos feeds RSS, centralizando-os e disponibilizando-os através de uma API REST.

## ✨ Funcionalidades Principais

-   **Agregação de Múltiplas Fontes**: Coleta e processa artigos de diversas fontes de RSS configuradas.
-   **Atualização Automática**: Utiliza o agendador de tarefas do Spring (`@Scheduled`) para buscar novos conteúdos periodicamente.
-   **Armazenamento em Memória**: Para simplicidade e rapidez, os dados agregados são mantidos em uma lista em memória.
-   **API RESTful**: Expõe um endpoint simples para consumir o conteúdo agregado em formato JSON.
-   **Configuração Flexível**: Adicione ou remova fontes de RSS facilmente através do arquivo `application.properties`.

## 🛠️ Tecnologias Utilizadas

-   **[Java 17](https://www.oracle.com/java/)**: Linguagem de programação principal.
-   **[Spring Boot 3](https://spring.io/projects/spring-boot)**: Framework para criação de aplicações stand-alone baseadas em Spring.
-   **[Spring Web](https://docs.spring.io/spring-framework/reference/web/webmvc.html)**: Para a construção da API REST.
-   **[Spring Scheduled Tasks](https://spring.io/guides/gs/scheduling-tasks/)**: Para o agendamento da busca de novos feeds.
-   **[Maven](https://maven.apache.org/)**: Ferramenta de automação de compilação e gerenciamento de dependências.
-   **[Rome](https://rometools.github.io/rome/)**: Biblioteca para manipulação e parsing de feeds RSS e Atom em Java.

## 📚 Pré-requisitos

Antes de começar, certifique-se de que você tem o seguinte instalado em sua máquina:
-   [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) - Versão 17 ou superior.
-   [Apache Maven](https://maven.apache.org/download.cgi) - Versão 3.8 ou superior.

## ⚙️ Instalação e Configuração

Siga os passos abaixo para rodar o projeto localmente:

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/Dev-Russo/agregador-node-springboot.git](https://github.com/Dev-Russo/agregador-node-springboot.git)
    cd agregador-node-springboot
    ```

2.  **Instale as dependências:**
    O Maven fará o download de todas as dependências automaticamente. Você pode forçar a instalação com o comando:
    ```bash
    mvn clean install
    ```

3.  **Configure as fontes de RSS:**
    Abra o arquivo `src/main/resources/application.properties`. Nele, você encontrará a propriedade `app.rss.feeds.urls`. Adicione as URLs dos feeds que você deseja agregar, separadas por vírgula:

    ```properties
    # Porta em que o servidor irá rodar
    server.port=8080

    # URLs dos feeds RSS para agregar, separadas por vírgula
    app.rss.feeds.urls=[https://g1.globo.com/rss/g1/tecnologia/,https://rss.tecmundo.com.br/feed,https://feeds.feedburner.com/canaltechbr](https://g1.globo.com/rss/g1/tecnologia/,https://rss.tecmundo.com.br/feed,https://feeds.feedburner.com/canaltechbr)
    ```

## ▶️ Executando o Projeto

Você pode iniciar a aplicação diretamente pela sua IDE (Eclipse, IntelliJ, VS Code) ou usando o Maven no terminal:
```bash
mvn spring-boot:run
```

Após iniciar, o servidor estará rodando em `http://localhost:8080`. O primeiro carregamento de dados ocorrerá assim que a aplicação iniciar, e depois será repetido conforme o intervalo definido no agendador de tarefas.

## 🔌 API Endpoints

O projeto expõe o seguinte endpoint para acessar os dados agregados:

### `GET /api/posts`

Retorna uma lista de todos os artigos agregados, ordenados do mais recente para o mais antigo.

-   **Método**: `GET`
-   **URL**: `/api/posts`
-   **Resposta de Sucesso (200 OK)**:
    ```json
    [
        {
            "title": "Título do Artigo Mais Recente",
            "link": "[https://examplo.com/artigo-recente](https://examplo.com/artigo-recente)",
            "publicationDate": "2025-08-19T18:30:00Z",
            "author": "Nome do Autor",
            "description": "Um resumo do conteúdo do artigo...",
            "source": "Nome da Fonte (ex: g1.globo.com)"
        },
        {
            "title": "Título de um Artigo Anterior",
            "link": "[https://examplo.com/artigo-anterior](https://examplo.com/artigo-anterior)",
            "publicationDate": "2025-08-19T17:45:00Z",
            "author": "Outro Autor",
            "description": "Este é o resumo de um outro artigo...",
            "source": "Nome da Fonte (ex: tecmundo.com.br)"
        }
    ]
    ```

## 🔧 Customização

### Alterando o Intervalo de Atualização

Por padrão, o agendador está configurado para buscar novos artigos a cada 15 minutos. Você pode alterar essa frequência editando a anotação `@Scheduled` no serviço responsável pela busca (ex: `FeedService.java`).

Exemplo no código:
```java
// Busca por novos posts a cada 15 minutos (900000 milissegundos).
@Scheduled(fixedRate = 900000)
public void fetchAllFeeds() {
    // ...lógica para buscar os feeds
}
```
Você também pode usar expressões cron para uma configuração mais detalhada. Para mais informações, consulte a [documentação do Spring sobre Scheduled Tasks](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling).

## 🤝 Contribuições

Contribuições são sempre bem-vindas! Se você tiver alguma ideia para melhorar este projeto, sinta-se à vontade para:

1.  Fazer um **Fork** do projeto.
2.  Criar uma nova **Branch** (`git checkout -b feature/sua-feature`).
3.  Fazer o **Commit** das suas alterações (`git commit -m 'Adiciona nova feature'`).
4.  Fazer o **Push** para a Branch (`git push origin feature/sua-feature`).
5.  Abrir um **Pull Request**.

