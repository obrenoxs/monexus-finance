# Domain Model

## Objetivo

Este documento define o modelo de domínio do Monexus Finance.

Todas as entidades, seus relacionamentos e responsabilidades são definidos aqui antes da implementação.

---

# Visão Geral

O Monexus Finance foi modelado seguindo princípios de alta coesão e baixo acoplamento.

Cada entidade possui apenas uma responsabilidade dentro do domínio.

---

# Modelo Geral

```
User
│
└── Wallet
      │
      ├── Transactions
      │
      ├── Categories
      │
      └── Goals
```

---

# Entidade User

## Responsabilidade

Representa o usuário da plataforma.

Não possui responsabilidade financeira.

É responsável apenas por informações da conta.

## Atributos

- id
- firstName
- lastName
- email
- password
- profileImage
- emailVerified
- createdAt
- updatedAt

## Relacionamentos

Um usuário possui:

- uma Wallet

---

# Entidade Wallet

## Responsabilidade

Representa a carteira financeira do usuário.

Toda movimentação financeira pertence à Wallet.

## Atributos

- id
- currentBalance
- currency
- createdAt
- updatedAt

## Relacionamentos

Pertence a:

- User

Possui:

- Transactions
- Categories
- Goals

---

# Entidade Transaction

## Responsabilidade

Representa qualquer movimentação financeira.

Receitas e despesas compartilham a mesma estrutura.

O tipo da movimentação será definido por um Enum.

## Atributos

- id
- description
- amount
- date
- observation
- type
- createdAt
- updatedAt

## Relacionamentos

Pertence a:

- Wallet

Possui:

- Category

---

# Enum TransactionType

Representa o tipo da movimentação.

Valores:

- INCOME
- EXPENSE

---

# Entidade Category

## Responsabilidade

Organizar movimentações financeiras.

Cada usuário possui suas próprias categorias.

## Atributos

- id
- name
- type
- createdAt

## Relacionamentos

Pertence a:

- Wallet

É utilizada por:

- Transactions

---

# Enum CategoryType

Valores:

- INCOME
- EXPENSE

---

# Entidade Goal

## Responsabilidade

Representa uma meta financeira.

## Atributos

- id
- title
- targetAmount
- targetDate
- createdAt
- updatedAt

## Relacionamentos

Pertence a:

- Wallet

---

# Relacionamentos

## User

```
1 ---- 1 Wallet
```

---

## Wallet

```
1 ---- N Transactions

1 ---- N Categories

1 ---- N Goals
```

---

## Category

```
1 ---- N Transactions
```

---

# Cardinalidades

| Entidade | Relacionamento | Cardinalidade |
|----------|----------------|---------------|
| User | Wallet | 1 : 1 |
| Wallet | Transaction | 1 : N |
| Wallet | Category | 1 : N |
| Wallet | Goal | 1 : N |
| Category | Transaction | 1 : N |

---

# Regras de Dependência

User NÃO conhece Transaction.

User NÃO conhece Goal.

User NÃO conhece Category.

Toda informação financeira deve passar obrigatoriamente pela Wallet.

---

# Responsabilidade das Entidades

## User

Responsável apenas pela conta do usuário.

---

## Wallet

Responsável pelo contexto financeiro.

---

## Transaction

Responsável por representar movimentações.

---

## Category

Responsável por organizar movimentações.

---

## Goal

Responsável por representar objetivos financeiros.

---

# Preparação para Evolução

O domínio foi projetado para permitir futuramente:

- múltiplas carteiras;
- investimentos;
- parcelamentos;
- despesas futuras;
- receitas recorrentes;
- integração bancária.

Sem necessidade de grandes alterações estruturais.

---

# Filosofia do Modelo

Cada entidade deve possuir apenas uma responsabilidade.

Toda lógica de negócio ficará concentrada na camada Service.

As entidades representam apenas o domínio da aplicação.