# FluxivaMed API

![Java](https://img.shields.io/badge/Java-21-blue.svg) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg) ![Docker](https://img.shields.io/badge/Docker-20.10-blue.svg)

API RESTful para a plataforma FluxivaMed, um sistema de gerenciamento de plantões médicos projetado para otimizar a alocação de profissionais e a gestão de escalas em hospitais e clínicas.

## 📋 Sobre o Projeto

O FluxivaMed API é o backend que serve como a espinha dorsal para a plataforma, gerenciando todas as regras de negócio, autenticação de usuários e persistência de dados. O objetivo é fornecer uma base sólida, segura e escalável para as aplicações frontend (web e mobile) que consumirão estes serviços.

---

## ✨ Funcionalidades Implementadas

* **Autenticação e Autorização:** Sistema de registro e login com JWT (JSON Web Tokens).
* **Gestão de Perfis:** Suporte a diferentes perfis de usuários (`MEDICO`, `HOSPITAL_ADMIN`, etc.).
* **Fluxo de Plantões:**
    * Médicos podem visualizar plantões disponíveis com filtros.
    * Médicos podem se candidatar a um plantão.
    * Administradores de hospital podem aprovar a candidatura de um médico.
    * Médicos podem visualizar sua agenda de plantões ("Meus Plantões").
* **Documentação de API:** Geração automática de documentação com Springdoc (Swagger UI).
* **Migrações de Banco:** Versionamento de schema do banco de dados com Flyway.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** [Java 21](https://www.oracle.com/java/)
* **Framework:** [Spring Boot 3](https://spring.io/projects/spring-boot)
* **Segurança:** [Spring Security 6](https://spring.io/projects/spring-security) (Autenticação JWT com OAuth2 Resource Server)
* **Acesso a Dados:** [Spring Data JPA](https://spring.io/projects/spring-data-jpa) (Hibernate)
* **Banco de Dados:** [PostgreSQL](https://www.postgresql.org/)
* **Migrações:** [Flyway](https://flywaydb.org/)
* **Containerização:** [Docker](https://www.docker.com/) & Docker Compose
* **Build:** [Maven](https://maven.apache.org/)
* **Documentação:** [Springdoc OpenAPI](https://springdoc.org/)

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar o ambiente de desenvolvimento local.

### Pré-requisitos

Antes de começar, garanta que você tem as seguintes ferramentas instaladas:
* [JDK 21](https://www.oracle.com/java/technologies/downloads/#jdk21-windows)
* [Apache Maven](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/products/docker-desktop/) e Docker Compose

### Configuração

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/roberlancarvalho/fluxivamed-api.git](https://github.com/roberlancarvalho/fluxivamed-api.git)
    cd fluxivamed-api
    ```

2.  **Variável de Ambiente (JWT Secret):**
    A aplicação precisa de uma chave secreta para assinar os tokens JWT. No arquivo `application.properties`, ela está configurada para ler uma variável de ambiente `SECURITY_JWT_SECRET`.

    Você pode configurar essa variável no seu sistema operacional ou, mais fácil ainda, na sua configuração de execução do IntelliJ. Se a variável não for encontrada, um valor padrão será usado (o que é inseguro para produção, mas funcional para dev).

3.  **Inicie o Banco de Dados:**
    O projeto usa Docker Compose para subir o container do PostgreSQL. Execute o seguinte comando no terminal, na raiz do projeto:
    ```bash
    docker-compose up -d db
    ```
    Isso irá iniciar **apenas** o serviço do banco de dados em segundo plano.

### Executando a Aplicação

* **Via IntelliJ IDEA:** Abra o projeto como um projeto Maven e execute a classe principal `FluxivamedApplication.java`.
* **Via terminal Maven:**
    ```bash
    mvn spring-boot:run
    ```

A aplicação estará disponível em `http://localhost:8080`.

---

## 🗺️ Principais Endpoints da API

Após iniciar a aplicação, a documentação completa da API pode ser acessada via Swagger UI em:
**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Abaixo estão os principais endpoints já implementados:

| Método | Endpoint                                       | Proteção            | Descrição                                         |
|--------|------------------------------------------------|---------------------|---------------------------------------------------|
| `POST` | `/auth/register`                               | Pública             | Registra um novo usuário (Médico, Admin, etc.).   |
| `POST` | `/auth/login`                                  | Pública             | Autentica um usuário e retorna um token JWT.      |
| `GET`  | `/api/v1/plantoes/disponiveis`                 | `MEDICO`            | Lista os plantões com status `DISPONIVEL`.        |
| `POST` | `/api/v1/plantoes/{plantaoId}/candidatar-se`   | `MEDICO`            | Permite que o médico logado se candidate a um plantão. |
| `POST` | `/api/v1/plantoes/{plantaoId}/aprovar/{medicoId}` | `HOSPITAL_ADMIN`    | Aprova um médico para um plantão.               |
| `GET`  | `/api/v1/plantoes/meus-plantoes`               | `MEDICO`            | Lista os plantões do médico logado.               |
| `GET`  | `/users/me`                                    | Autenticado         | Retorna as informações do usuário logado.         |

---

## 👨‍💻 Autor

* **Roberlan Carvalho** - Fundador da [Tech North](https://github.com/roberlancarvalho)
