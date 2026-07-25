# Software Architecture

## Objetivo

Este documento define toda a arquitetura técnica do Monexus Finance.

Todas as decisões estruturais deverão ser registradas aqui antes de serem implementadas no código.

---

# Arquitetura

O Monexus Finance seguirá uma arquitetura em camadas (Layered Architecture), separando claramente cada responsabilidade do sistema.

```
Controller
↓

Service

↓

Repository

↓

Database
```

Cada camada possui apenas uma responsabilidade.

---

# Princípios Arquiteturais

O projeto seguirá os princípios:

- SOLID
- Clean Code
- Separation of Concerns
- Single Responsibility
- RESTful Architecture
- Baixo Acoplamento
- Alta Coesão

---

# Stack Tecnológica

## Backend

- Java 25
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Validation
- Maven

---

## Banco de Dados

- MySQL

---

## Frontend

- React
- Tailwind CSS

---

## DevOps

- Docker
- Docker Compose
- GitHub Actions

---

## Documentação

- OpenAPI
- Swagger

---

# Organização do Projeto

O projeto seguirá o padrão **Package by Feature** (organização por funcionalidade/domínio de negócio), em vez de Package by Layer.

Cada funcionalidade principal do sistema possui seu próprio pacote, contendo internamente todas as camadas necessárias (Controller, Service, Repository, Entity, DTO, Mapper).

Pacotes compartilhados entre funcionalidades (configuração, segurança, tratamento de exceções e utilitários) ficam centralizados em um pacote `shared`.

Estrutura geral:

com.monexus.finance
│
├── user
│ ├── controller
│ ├── service
│ ├── repository
│ ├── entity
│ ├── dto
│ │ ├── request
│ │ └── response
│ └── mapper
│
├── wallet
│ └── (mesma estrutura interna)
│
├── category
│ └── (mesma estrutura interna)
│
├── transaction
│ └── (mesma estrutura interna)
│
├── goal
│ └── (mesma estrutura interna)
│
└── shared
├── config
├── security
├── exception
└── utils

Cada funcionalidade é responsável apenas pelas suas próprias camadas internas, mantendo alta coesão.

O `shared` concentra apenas o que é, de fato, transversal a múltiplas funcionalidades — nunca regra de negócio de um domínio específico.

---

**Motivo da escolha (Package by Feature vs Package by Layer):**

O padrão Package by Layer, embora mais didático para introdução ao Spring Boot, tende a dificultar a navegação e manutenção conforme o número de funcionalidades cresce (roadmap do projeto prevê múltiplos módulos: Carteira, Categorias, Transações, Metas, Investimentos, Parcelamentos, Multi Wallet, IA, entre outros).

O Package by Feature favorece:

- alta coesão (tudo relacionado a uma funcionalidade fica junto);
- baixo acoplamento entre domínios distintos;
- navegação mais clara conforme o sistema cresce;
- maior alinhamento com o princípio de Separation of Concerns em nível de domínio, não apenas técnico.

---

# API

A API seguirá integralmente os princípios REST.

Cada recurso possuirá seu próprio endpoint.

Exemplo:

```
/users

/wallets

/transactions

/categories

/goals
```

Os verbos HTTP serão utilizados corretamente.

GET

POST

PUT

PATCH

DELETE

---

# Segurança

O sistema utilizará:

- Spring Security
- JWT
- BCrypt
- Confirmação de e-mail
- Recuperação de senha

A autenticação será realizada utilizando:

```
Email + Senha
```

O e-mail do usuário não poderá ser alterado.

---

# Banco de Dados

Inicialmente cada usuário possuirá:

- uma única carteira.

A arquitetura será preparada para suportar múltiplas carteiras futuramente sem necessidade de grandes refatorações.

---

# Escalabilidade

O sistema será desenvolvido pensando em futuras expansões.

Entre elas:

- múltiplas carteiras;
- investimentos;
- parcelamentos;
- despesas futuras;
- IA;
- aplicativo mobile;
- integração bancária.

---

# Convenções

## Identificadores

Todas as entidades utilizarão:

```
Long
```

com geração automática pelo banco.

---

## Datas

Será utilizado:

```
LocalDate

LocalDateTime
```

Nunca será utilizado Date.

---

## Valores Monetários

Todos os valores financeiros utilizarão:

```
BigDecimal
```

Nunca será utilizado:

- float
- double

---

## DTOs

Toda comunicação entre API e cliente será realizada através de DTOs.

Entidades nunca serão retornadas diretamente ao cliente.

---

## Mapper

A conversão entre Entity e DTO será realizada utilizando MapStruct.

---

## Tratamento de Erros

Todas as exceções serão centralizadas através de:

```
@RestControllerAdvice
```

---

# Filosofia de Desenvolvimento

Antes de implementar qualquer funcionalidade, devemos responder:

- Esta funcionalidade segue os princípios REST?
- Está respeitando o SOLID?
- Está preparada para evolução futura?
- Está simples?
- Está desacoplada?
- Está documentada?

Caso qualquer resposta seja "não", a implementação deverá ser revista antes de continuar.