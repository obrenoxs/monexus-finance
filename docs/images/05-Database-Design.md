# Database Design

## Objetivo

Este documento define toda a estrutura do banco de dados do Monexus Finance.

Todas as tabelas, colunas, tipos, relacionamentos e restrições deverão ser definidos aqui antes da implementação.

---

# Banco de Dados

Banco escolhido:

```
MySQL
```

---

# Convenções

## Nome das tabelas

Todas as tabelas utilizarão:

- letras minúsculas;
- snake_case;
- nomes no plural.

Exemplos:

```
users

wallets

transactions

categories

goals
```

---

## Nome das colunas

Todas utilizarão:

```
snake_case
```

Exemplo

```
first_name

created_at

updated_at
```

---

## Chaves Primárias

Todas as tabelas utilizarão

```
BIGINT AUTO_INCREMENT
```

Exemplo

```
id BIGINT
```

---

# Tabela users

| Campo | Tipo | Restrição |
|--------|------|-----------|
| id | BIGINT | PK |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(150) | UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| profile_image | VARCHAR(255) | NULL |
| email_verified | BOOLEAN | DEFAULT FALSE |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

---

# Tabela wallets

| Campo | Tipo | Restrição |
|--------|------|-----------|
| id | BIGINT | PK |
| currency | VARCHAR(5) | NOT NULL |
| user_id | BIGINT | UNIQUE |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

---

# Tabela categories

| Campo | Tipo | Restrição |
|--------|------|-----------|
| id | BIGINT | PK |
| name | VARCHAR(100) | NOT NULL |
| type | VARCHAR(20) | NOT NULL |
| wallet_id | BIGINT | FK |
| created_at | DATETIME | NOT NULL |

---

# Tabela transactions

| Campo | Tipo | Restrição |
|--------|------|-----------|
| id | BIGINT | PK |
| description | VARCHAR(150) | NOT NULL |
| amount | DECIMAL(19,2) | NOT NULL |
| date | DATE | NOT NULL |
| observation | TEXT | NULL |
| type | VARCHAR(20) | NOT NULL |
| wallet_id | BIGINT | FK |
| category_id | BIGINT | FK |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

---

# Tabela goals

| Campo | Tipo | Restrição |
|--------|------|-----------|
| id | BIGINT | PK |
| title | VARCHAR(150) | NOT NULL |
| target_amount | DECIMAL(19,2) | NOT NULL |
| target_date | DATE | NOT NULL |
| wallet_id | BIGINT | FK |
| created_at | DATETIME | NOT NULL |
| updated_at | DATETIME | NOT NULL |

---

# Relacionamentos

users

```
1 ---- 1 wallets
```

wallets

```
1 ---- N categories

1 ---- N transactions

1 ---- N goals
```

categories

```
1 ---- N transactions
```

---

# Chaves Estrangeiras

wallets

```
user_id
```

↓

users.id

---

categories

```
wallet_id
```

↓

wallets.id

---

transactions

```
wallet_id
```

↓

wallets.id

---

transactions

```
category_id
```

↓

categories.id

---

goals

```
wallet_id
```

↓

wallets.id

---

# Índices

Além das PKs e FKs, serão criados índices para:

users

```
email
```

---

transactions

```
date
```

---

transactions

```
type
```

---

categories

```
name
```

---

goals

```
target_date
```

---

# Integridade

Não será permitido:

- Wallet sem User.
- Transaction sem Wallet.
- Transaction sem Category.
- Goal sem Wallet.
- Category sem Wallet.

---

# Exclusões

## User

Ao excluir um usuário:

- Wallet será removida.

---

## Wallet

Ao excluir uma Wallet:

- Transactions serão removidas.
- Categories serão removidas.
- Goals serão removidas.

---

## Category

Uma categoria não poderá ser excluída caso existam Transactions vinculadas.

---

# Valores Monetários

Todos os valores financeiros utilizarão

```
DECIMAL(19,2)
```

Nunca será utilizado

- FLOAT
- DOUBLE

---

# Cálculo do Saldo

O saldo da carteira não será armazenado no banco de dados.

O valor será calculado dinamicamente através da soma das movimentações financeiras.

Fórmula:

Saldo = Soma das Receitas − Soma das Despesas

Esta decisão garante:

- consistência dos dados;
- ausência de divergências entre saldo e histórico;
- maior confiabilidade do sistema.

O banco de dados armazenará apenas as movimentações financeiras.

# Datas

Datas simples

```
DATE
```

Datas de auditoria

```
DATETIME
```

---

# Auditoria

Todas as tabelas possuirão:

```
created_at

updated_at
```

---

# Escalabilidade

O banco foi modelado para permitir futuras expansões sem alterações estruturais significativas.

Entre elas:

- múltiplas carteiras;
- investimentos;
- despesas futuras;
- receitas recorrentes;
- parcelamentos;
- integração bancária.