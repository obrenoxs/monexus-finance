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

O projeto seguirá a seguinte organização:

```
controller

service

repository

entity

dto

mapper

config

security

exception

utils

docs
```

Cada pacote possuirá apenas uma responsabilidade.

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