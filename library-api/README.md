# Library REST API

REST API para gerenciamento de uma biblioteca, desenvolvida em Java puro com banco de dados H2 embutido, sem uso de frameworks web.

---

## Sobre o projeto

O sistema permite gerenciar autores, livros e categorias através de uma API REST completa com operações de CRUD. As entidades possuem os seguintes relacionamentos:

- Author e Book: relacionamento 1:N — um autor possui vários livros
- Book e Category: relacionamento N:N — um livro pode pertencer a várias categorias e vice-versa

---

## Pré-requisitos

Antes de compilar e executar o projeto, certifique-se de ter instalado:

- Java JDK 17 ou superior
- Maven 3.6 ou superior

Para verificar se já estão instalados, execute no terminal:

```
java -version
mvn -version
```

---

## Como compilar e executar

**1. Clone o repositório**

```
git clone https://github.com/seu-usuario/library-api.git
cd library-api
```

**2. Compile e gere o JAR**

```
mvn clean package
```

Este comando compila o projeto e gera o arquivo `target/library-api-1.0.0-jar-with-dependencies.jar` com todas as dependências inclusas.

**3. Execute a aplicação**

```
java -jar target/library-api-1.0.0-jar-with-dependencies.jar
```

O servidor iniciará na porta 8080. O banco de dados H2 será criado automaticamente na pasta `./data/` na primeira execução.

**4. Encerrar a aplicação**

Pressione `Ctrl+C` no terminal. O servidor realiza o encerramento de forma segura, fechando a conexão com o banco de dados.

---

## Banco de dados

O projeto utiliza o H2, um banco de dados embutido que roda dentro do próprio processo Java, sem necessidade de instalação separada. Os dados são persistidos no arquivo `data/librarydb.mv.db`, criado automaticamente ao iniciar a aplicação.

---

## Endpoints disponíveis

A API escuta em `http://localhost:8080` e todas as respostas seguem o formato:

```json
{
  "success": true,
  "data": {},
  "message": null,
  "timestamp": "2025-01-01T10:00:00"
}
```

### Authors

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /authors | Lista todos os autores |
| GET | /authors/{id} | Busca autor por ID |
| GET | /authors/{id}/books | Lista os livros de um autor |
| POST | /authors | Cria um novo autor |
| PUT | /authors/{id} | Atualiza um autor |
| DELETE | /authors/{id} | Remove um autor |

Exemplo de corpo para POST e PUT:

```json
{
  "name": "Machado de Assis",
  "nationality": "Brasileiro",
  "email": "machado@literatura.br",
  "biography": "Considerado o maior nome da literatura brasileira."
}
```

### Books

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /books | Lista todos os livros |
| GET | /books/{id} | Busca livro por ID |
| POST | /books | Cria um novo livro |
| PUT | /books/{id} | Atualiza um livro |
| DELETE | /books/{id} | Remove um livro |
| GET | /books/{id}/categories | Lista categorias do livro |
| POST | /books/{id}/categories/{categoryId} | Associa uma categoria ao livro |
| DELETE | /books/{id}/categories/{categoryId} | Remove a associação com uma categoria |

Exemplo de corpo para POST e PUT:

```json
{
  "title": "Dom Casmurro",
  "isbn": "978-85-359-0277-5",
  "publicationYear": 1899,
  "price": 39.90,
  "pages": 256,
  "language": "Português",
  "authorId": 1
}
```

### Categories

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /categories | Lista todas as categorias |
| GET | /categories/{id} | Busca categoria por ID |
| POST | /categories | Cria uma nova categoria |
| PUT | /categories/{id} | Atualiza uma categoria |
| DELETE | /categories/{id} | Remove uma categoria |

Exemplo de corpo para POST e PUT:

```json
{
  "name": "Romance",
  "description": "Obras narrativas centradas em relações afetivas e desenvolvimento de personagens."
}
```

---

## Estrutura do projeto

```
library-api/
├── pom.xml
├── README.md
└── src/main/java/com/library/
    ├── Main.java
    ├── model/
    │   ├── BaseEntity.java
    │   ├── Author.java
    │   ├── Book.java
    │   └── Category.java
    ├── repository/
    │   ├── Repository.java
    │   ├── AuthorRepository.java
    │   ├── BookRepository.java
    │   └── CategoryRepository.java
    ├── service/
    │   ├── AuthorService.java
    │   ├── BookService.java
    │   └── CategoryService.java
    ├── controller/
    │   ├── BaseHandler.java
    │   ├── AuthorHandler.java
    │   ├── BookHandler.java
    │   └── CategoryHandler.java
    ├── database/
    │   ├── DatabaseConnection.java
    │   └── DatabaseInitializer.java
    ├── exception/
    │   ├── NotFoundException.java
    │   └── ValidationException.java
    └── util/
        ├── ApiResponse.java
        └── JsonUtil.java
```

---

## Dependências

| Biblioteca | Versão | Finalidade |
|------------|--------|------------|
| H2 Database | 2.2.224 | Banco de dados embutido |
| Jackson Databind | 2.16.1 | Serialização JSON |
| Jackson Datatype JSR310 | 2.16.1 | Suporte a tipos de data Java 8+ |
| Lombok | 1.18.30 | Redução de código repetitivo |

O servidor HTTP utilizado é o `com.sun.net.httpserver.HttpServer`, que faz parte do próprio JDK, sem necessidade de frameworks externos.

---

## Tecnologias utilizadas

- Java 17+
- Maven
- H2 Database (banco embutido)
- JDBC (acesso ao banco de dados)
- Jackson (serialização JSON)
- Lombok
- HttpServer do JDK (servidor HTTP)
