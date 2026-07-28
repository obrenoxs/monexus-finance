# API Specification

## Objetivo

Este documento define o contrato oficial da API REST do Monexus Finance.

Nenhum endpoint poderá ser implementado sem estar previamente documentado.

---

# Padrão REST

Todos os endpoints seguirão os princípios RESTful.

Métodos HTTP utilizados:

- GET
- POST
- PUT
- PATCH
- DELETE

---

# Base URL

```
/api/v1
```

---

# Autenticação

## Login

POST

```
/auth/login
```

Descrição

Realiza autenticação do usuário.

Resposta

- JWT Access Token

---

## Cadastro

POST

```
/auth/register
```

Descrição

Cria um novo usuário.

---

## Recuperação de Senha

POST

```
/auth/forgot-password
```

---

## Confirmação de E-mail

GET

Descrição

Confirma o e-mail do usuário a partir do token enviado por e-mail.

Nota: embora essa operação altere o estado do usuário (emailVerified),
o verbo GET foi escolhido deliberadamente, pois o cliente desta chamada
é um link clicado em um e-mail (sem possibilidade de disparar POST
nativamente). Essa é uma exceção reconhecida e documentada à convenção
REST padrão do restante da API.
---

## Redefinição

POST

```
/auth/reset-password
```

---

# Usuários

## Buscar usuário

GET

```
/users/me
```

---

## Atualizar perfil

PUT

```
/users/me
```

---

## Alterar foto

PATCH

```
/users/me/profile-image
```

---

## Excluir conta

DELETE

```
/users/me
```

---

# Carteira

## Buscar carteira

GET

```
/wallet
```

---

# Categorias

## Listar categorias

GET

```
/categories
```

---

## Buscar categoria

GET

```
/categories/{id}
```

---

## Criar categoria

POST

```
/categories
```

---

## Atualizar categoria

PUT

```
/categories/{id}
```

---

## Excluir categoria

DELETE

```
/categories/{id}
```

---

# Transações

## Listar transações

GET

```
/transactions
```

Filtros futuros

- período
- categoria
- tipo

---

## Buscar transação

GET

```
/transactions/{id}
```

---

## Criar transação

POST

```
/transactions
```

---

## Atualizar transação

PUT

```
/transactions/{id}
```

---

## Excluir transação

DELETE

```
/transactions/{id}
```

---

# Metas

## Listar metas

GET

```
/goals
```

---

## Buscar meta

GET

```
/goals/{id}
```

---

## Criar meta

POST

```
/goals
```

---

## Atualizar meta

PUT

```
/goals/{id}
```

---

## Excluir meta

DELETE

```
/goals/{id}
```

---

# Dashboard

## Dashboard principal

GET

```
/dashboard
```

Retornará

- saldo atual
- receitas do mês
- despesas do mês
- saldo do mês
- comparação mensal
- gráficos

---

# HTTP Status

## Sucesso

200 OK

201 Created

204 No Content

---

## Cliente

400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

409 Conflict

422 Unprocessable Entity

---

## Servidor

500 Internal Server Error

---

# Versionamento

Toda API utilizará:

```
/api/v1
```

Novas versões serão:

```
/api/v2
```

---

# Content Negotiation

A API será preparada para trabalhar com:

- JSON
- XML

---

# Paginação

Endpoints de listagem utilizarão paginação.

Exemplo

```
?page=0

&size=10

&sort=date,desc
```

---

# HATEOAS

Não será implementado na versão 1.

A arquitetura permitirá sua implementação futuramente.

---

# Segurança

Todos os endpoints, exceto autenticação, exigirão JWT válido.

---

# Padronização de Respostas

As respostas deverão possuir estrutura consistente.

Exemplo de sucesso

```json
{
  "id": 1,
  "description": "Supermercado",
  "amount": 150.00
}
```

Exemplo de erro

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "...",
  "path": "..."
}
```

Exemplo de erro de validação (múltiplos campos)

Quando a requisição falha em múltiplas regras de validação simultaneamente, a resposta incluirá o campo adicional `errors`, contendo a lista de cada campo inválido e sua respectiva mensagem.

```json
{
  "timestamp": "2026-07-24T21:15:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação nos dados enviados",
  "errors": [
    { "field": "firstName", "message": "Nome é obrigatório" },
    { "field": "email", "message": "E-mail inválido" }
  ],
  "path": "/api/v1/auth/register"
}
```

O campo `errors` é opcional e aparece apenas quando há um ou mais erros de validação de campo. Erros de regra de negócio (ex: e-mail duplicado) continuam seguindo o formato simples, sem esse campo.

---

# OpenAPI

Toda a documentação da API será gerada automaticamente através do SpringDoc OpenAPI.