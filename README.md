# XP Guardian API V2

![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg?logo=spring)
![Security](https://img.shields.io/badge/Security-JWT%20&%20BCrypt-blueviolet.svg?logo=json-web-tokens)
![Maven](https://img.shields.io/badge/Build-Maven-critical.svg?logo=apache-maven)
![OpenAPI](https://img.shields.io/badge/API%20Docs-SpringDoc%20v2-skyblue.svg?logo=swagger)

Projeto acadêmico de uma API RESTful para o "Banco XP Guardian". O sistema utiliza Java 21, Spring Boot 3 e Spring Security 6 para criar um serviço de autenticação e autorização robusto, seguro e stateless, seguindo os princípios SOLID e de Clean Architecture.

## Tabela de Conteúdos

1.  [Descrição do Projeto](#1-descrição-do-projeto)
    * [Funcionalidades Principais](#funcionalidades-principais)
2.  [Arquitetura e Tecnologias](#2-arquitetura-e-tecnologias)
    * [Tecnologias Utilizadas](#tecnologias-utilizadas)
3.  [Instruções de Execução](#3-instruções-de-execução)
    * [Pré-requisitos](#pré-requisitos)
    * [Configuração](#configuração)
    * [Executando a Aplicação](#executando-a-aplicação)
4.  [Acessando os Serviços](#4-acessando-os-serviços)
    * [Documentação da API (Swagger)](#documentação-da-api-swagger)
    * [Console do Banco de Dados (H2)](#console-do-banco-de-dados-h2)
5.  [Guia de Testes Manuais (Fluxo Completo)](#5-guia-de-testes-manuais-fluxo-completo)
    * [Passo 1: Registrar um Novo Usuário](#passo-1-registrar-um-novo-usuário)
    * [Passo 2: Autorizar no Swagger](#passo-2-autorizar-no-swagger)
    * [Passo 3: Acessar Rota Protegida (Usuário)](#passo-3-acessar-rota-protegida-usuário)
    * [Passo 4: Testar Rota de Admin (Autorização)](#passo-4-testar-rota-de-admin-autorização)
    * [Passo 5: (Opcional) Verificar o Banco](#passo-5-opcional-verificar-o-banco)
6.  [Como Rodar os Testes Automatizados](#6-como-rodar-os-testes-automatizados)
7.  [Estrutura do Projeto](#7-estrutura-do-projeto)

---

## 1. Descrição do Projeto

**XP Guardian API** é um backend RESTful que implementa um sistema de autenticação e autorização (A&A) seguro. O projeto foi desenhado para ser *stateless* (sem estado), utilizando JSON Web Tokens (JWT) para toda a validação de sessão, o que o torna ideal para arquiteturas de microsserviços e aplicações web modernas.

A arquitetura do projeto segue uma rigorosa separação de responsabilidades (camadas de Controller, Service e Repository) e adere aos princípios SOLID, garantindo um código limpo, modular, testável e de fácil manutenção.

### Funcionalidades Principais

* **Autenticação Stateless:** Endpoints públicos para registro (`/register`) e autenticação (`/authenticate`) que geram um token JWT.
* **Segurança com JWT:** Um filtro de segurança customizado (`JwtAuthenticationFilter`) intercepta todas as requisições, validando o token JWT antes de permitir o acesso.
* **Autorização Baseada em Papéis (RBAC):** Uso de `Role` (`USER`, `ADMIN`) e anotações `@PreAuthorize` para controlar o acesso a endpoints específicos.
* **Criptografia de Senhas:** Senhas de usuários são armazenadas de forma segura no banco de dados usando o `BCryptPasswordEncoder`.
* **Documentação Automática:** A API é 100% documentada com SpringDoc (OpenAPI 3), gerando uma interface web (Swagger UI) interativa para testes.
* **Testes Automatizados:** Cobertura de testes unitários (para a camada de serviço) e testes de integração (para os endpoints da API).

---

## 2. Arquitetura e Tecnologias

A aplicação é construída sobre uma arquitetura em camadas (Layered Architecture) que separa a API web, a lógica de negócios e o acesso a dados. As interfaces (`JwtService`, `AuthService`) são usadas para promover baixo acoplamento e facilitar a testabilidade (Inversão de Dependência - 'D' do SOLID).

### Tecnologias Utilizadas

| Categoria | Tecnologia | Versão/Descrição |
| :--- | :--- | :--- |
| **Core** | Java | 21 |
| **Framework** | Spring Boot | 3.2.0 |
| **Segurança** | Spring Security | 6.2.0 (Autenticação, Autorização, BCrypt) |
| **Persistência** | Spring Data JPA | Gerenciamento de entidades e repositórios |
| **Banco de Dados** | H2 Database | Banco de dados em memória para desenvolvimento |
| **Autenticação** | JSON Web Token (JWT) | `io.jsonwebtoken:jjwt-api` (v0.11.5) |
| **API Docs** | SpringDoc OpenAPI | `springdoc-openapi-starter-webmvc-ui` (v2.2.0) |
| **Build** | Apache Maven | Gerenciamento de dependências e build |
| **Testes** | JUnit 5 | Testes unitários e de integração |
| **Testes (Mock)**| Mockito | Mocking de dependências em testes |
| **Utilities** | Lombok | Redução de código boilerplate (getters, setters, etc.) |
| **Runtime** | JAXB Runtime | `org.glassfish.jaxb:jaxb-runtime` (Necessário para Java 11+) |

---

## 3. Instruções de Execução

Siga os passos abaixo para configurar e executar a aplicação localmente.

### Pré-requisitos

* **JDK 21** (Java Development Kit)
* **Apache Maven 3.8+**
* **Git**

### Configuração

**1. Clonar o Repositório**
Abra seu terminal e clone o projeto:
```bash
git clone https://github.com/GuiBichaco/XPGuardianAPI_V2.git
cd XPGuardianAPI_V2
```

**2. Chave Secreta JWT**
Para facilitar a execução em ambiente de desenvolvimento, o projeto utiliza um **valor padrão** para a chave secreta do JWT, definido diretamente na camada de serviço.

**Nenhuma configuração de variável de ambiente é necessária para rodar o projeto.**

*(**Nota de Produção:** Em um ambiente de produção real, o `@Value` leria a variável de ambiente `JWT_SECRET_KEY`, e o valor padrão seria removido para evitar a exposição de segredos no código-fonte.)*

### Executando a Aplicação

Você pode executar a aplicação de duas maneiras:

**Opção A: Pela sua IDE (Recomendado)**

1.  Abra o projeto na sua IDE (ex: IntelliJ IDEA).
2.  Aguarde o Maven baixar todas as dependências (pode levar alguns segundos).
3.  Navegue até o arquivo `src/main/java/com/xpguardian/XpGuardianApplication.java`.
4.  Clique no ícone verde de "Play" (▶️) para iniciar a aplicação.

**Opção B: Pelo Terminal (Maven)**

1.  Abra um terminal na pasta raiz do projeto.
2.  Execute o seguinte comando:
    ```bash
    mvn spring-boot:run
    ```

A aplicação estará pronta quando o console exibir as mensagens:
`Tomcat started on port(s): 8080 (http)`
`Started XpGuardianApplication in X.XXX seconds`

---

## 4. Acessando os Serviços

Com a aplicação rodando, você pode acessar dois principais serviços pelo seu navegador:

### Documentação da API (Swagger)

Esta é a interface interativa para visualizar e testar todos os endpoints da API.

* **URL:** `http://localhost:8080/swagger-ui.html`

### Console do Banco de Dados (H2)

Esta é a interface para visualizar os dados no banco de dados em memória (ex: para verificar se usuários foram criados).

* **URL:** `http://localhost:8080/h2-console`
* **Credenciais de Login:**
    * **Driver Class:** `org.h2.Driver`
    * **JDBC URL:** `jdbc:h2:mem:xpguardiandb` (Copie e cole este valor, é o mais importante)
    * **User Name:** `sa`
    * **Password:** `password`

---

## 5. Guia de Testes Manuais (Fluxo Completo)

Use a interface do Swagger (`http://localhost:8080/swagger-ui.html`) para realizar o fluxo de teste completo.

### Passo 1: Registrar um Novo Usuário

1.  Expanda a seção **Authentication** e o endpoint `POST /api/v1/auth/register`.
2.  Clique no botão **"Try it out"**.
3.  No campo `Request body`, insira os dados de um novo usuário. A senha deve ter no mínimo 8 caracteres.

    ```json
    {
      "firstname": "Usuario",
      "lastname": "XP",
      "email": "teste@xp.com",
      "password": "senhaForte123"
    }
    ```
4.  Clique em **"Execute"**.

**Resultado Esperado:** Você receberá uma resposta `Code 200` (OK) contendo o token JWT.
**➡️ Ação:** **Copie o valor do token** (a string longa que começa com `eyJ...`).

### Passo 2: Autorizar no Swagger

1.  No topo da página, clique no botão verde **"Authorize"** (com o cadeado 🔒).
2.  Na janela que abrir, no campo `Value`, **cole o token** que você copiou.
3.  **IMPORTANTE:** Adicione o prefixo `Bearer ` (com um espaço) antes do token. O campo deve ficar assim:
    `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZUB4cC5jb20iLCJpYXQi...`
4.  Clique em **"Authorize"** e depois em **"Close"**. O ícone do cadeado 🔒 agora deve estar fechado.

### Passo 3: Acessar Rota Protegida (Usuário)

1.  Expanda a seção **Protected Resources** e o endpoint `GET /api/v1/protected/user-data`.
2.  Clique em **"Try it out"**.
3.  Clique em **"Execute"**.

**Resultado Esperado:** Você receberá uma resposta **`Code 200`** (OK) com a mensagem no corpo:
`"Hello User! This is your protected data."`
*Isso prova que sua autenticação (JWT) foi validada com sucesso.*

### Passo 4: Testar Rota de Admin (Autorização)

1.  Expanda o endpoint `GET /api/v1/protected/admin-data`.
2.  Clique em **"Try it out"**.
3.  Clique em **"Execute"**.

**Resultado Esperado:** Você receberá uma resposta **`Code 403`** (Forbidden). O corpo da resposta será um JSON de erro.
*Isso é um **SUCESSO**! Prova que a sua autorização por papel (`@PreAuthorize("hasAuthority('ADMIN')")`) está funcionando e bloqueando um usuário que é apenas `USER`.*

### Passo 5: (Opcional) Verificar o Banco

1.  Acesse o H2 Console (`http://localhost:8080/h2-console`) e faça login (veja as credenciais na Seção 4).
2.  Execute a query SQL: `SELECT * FROM _USER;`
3.  **Resultado:** Você verá o usuário `teste@xp.com` na tabela, com a senha na coluna `PASSWORD` totalmente criptografada (ex: `$2a$10$...`), provando que o `BCrypt` funcionou.

---

## 6. Como Rodar os Testes Automatizados

Para garantir a qualidade da lógica de negócio (Services) e dos endpoints (Controllers), o projeto inclui testes unitários e de integração.

Para executar todos os testes, abra um terminal na raiz do projeto e rode o seguinte comando Maven:

```bash
mvn test
```

O Maven compilará o código, executará todos os testes encontrados em `src/test/java`, e exibirá um relatório `BUILD SUCCESS` no console se tudo estiver correto.
## 7. Estrutura do Projeto

A estrutura de pacotes segue as melhores práticas de Clean Architecture e Separação de Responsabilidades:

```plaintext
com.xpguardian
├── XpGuardianApplication.java  // Classe principal (Main)
│
├── config/                     // Configurações (Spring Security, OpenAPI, JWT Filter)
│   ├── ApplicationConfig.java
│   ├── JwtAuthenticationFilter.java
│   ├── OpenAPIConfig.java
│   └── SecurityConfig.java
│
├── controller/                 // Camada de API (Endpoints REST)
│   ├── AuthController.java
│   └── ProtectedController.java
│
├── dto/                        // Data Transfer Objects (Request/Response)
│   ├── auth/
│   │   ├── AuthRequest.java
│   │   ├── AuthResponse.java
│   │   └── RegisterRequest.java
│   └── user/
│       └── UserDto.java
│
├── entity/                     // Entidades JPA (Mapeamento do Banco)
│   ├── Role.java
│   └── User.java
│
├── exception/                  // Handlers de exceções globais
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── repository/                 // Interfaces Spring Data JPA
│   └── UserRepository.java
│
└── service/                    // Camada de Lógica de Negócio
    ├── impl/                   // Implementações concretas
    │   ├── AuthServiceImpl.java
    │   └── JwtServiceImpl.java
    │
    ├── AuthService.java        // Interfaces (Contratos)
    └── JwtService.java
```