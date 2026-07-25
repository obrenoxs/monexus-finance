# Development Standards

## Objetivo

Este documento define todos os padrões de desenvolvimento do Monexus Finance.

Todo código implementado deverá seguir estas diretrizes.

---

# Filosofia

Antes de escrever qualquer código, devemos responder:

- É simples?
- É legível?
- É escalável?
- Está desacoplado?
- Está documentado?
- Segue o SOLID?

Caso qualquer resposta seja "não", a implementação deverá ser revisada.

---

# Princípios

O projeto seguirá:

- SOLID
- Clean Code
- KISS
- DRY
- Separation of Concerns
- RESTful Design

---

# Organização de Pacotes

O projeto segue o padrão **Package by Feature**.

Cada funcionalidade de negócio possui seu próprio pacote raiz, contendo as camadas necessárias internamente:

<feature> ├── controller ├── service ├── repository ├── entity ├── dto │ ├── request │ └── response └── mapper ```

shared
├── config
├── security
├── exception
└── utils

Regras:

Nenhuma funcionalidade deve depender diretamente de camadas internas de outra funcionalidade (ex: wallet não deve importar classes de dentro de user.service, apenas o que for exposto via shared ou contrato público, quando necessário).
Uma classe só entra em shared se for utilizada por duas ou mais funcionalidades. Caso contrário, permanece dentro do pacote da própria feature.

---

## Registrar no `11-Change-Log.md`

Na seção **`[Unreleased]`**, dentro de `## Changed` (crie a seção se ainda não existir):

```markdown
## Changed

- Migração da organização de pacotes de Package by Layer para Package by Feature, visando maior escalabilidade e coesão conforme o crescimento do roadmap do projeto.

---

# Convenções de Nome

## Classes

PascalCase

Exemplo

```
UserService

TransactionController

GoalMapper
```

---

## Métodos

camelCase

Exemplo

```
createTransaction()

findById()

updateGoal()
```

---

## Variáveis

camelCase

```
walletId

currentUser

goalRepository
```

---

## Constantes

UPPER_CASE

```
MAX_UPLOAD_SIZE

DEFAULT_PAGE_SIZE
```

---

## Enums

Sempre no singular.

Exemplo

```
TransactionType

CategoryType
```

---

# Controllers

Os Controllers possuem apenas uma responsabilidade:

Receber a requisição.

Enviar ao Service.

Retornar a resposta.

Nenhuma regra de negócio deverá existir no Controller.

---

# Services

Toda regra de negócio ficará na camada Service.

O Service nunca deverá acessar HTTP.

Nunca deverá conhecer DTO de entrada diretamente.

---

# Repository

Repository apenas acessa o banco.

Nenhuma regra de negócio deverá existir aqui.

---

# Entities

Entities representam apenas o domínio.

Não devem possuir:

- lógica de negócio;
- chamadas HTTP;
- acesso ao banco.

---

# DTOs

Toda comunicação externa utilizará DTO.

Nunca retornar Entity diretamente.

Teremos:

```
Request DTO

Response DTO
```

---

# Mapper

Todo mapeamento será realizado utilizando:

```
MapStruct
```

Nunca realizar conversões manualmente, salvo exceções muito específicas.

---

# Tratamento de Exceções

Todo erro será tratado através de:

```
@RestControllerAdvice
```

Nunca utilizar try/catch desnecessário.

---

# Validação

Toda entrada será validada utilizando:

```
Jakarta Validation
```

Exemplos

```
@NotBlank

@NotNull

@Positive

@Email

@Size
```

---

# Logs

Utilizar:

```
SLF4J

LoggerFactory
```

Nunca utilizar:

```
System.out.println()
```

---

# Comentários

Evitar comentários desnecessários.

Código deve ser autoexplicativo.

Comentários apenas quando agregarem contexto.

---

# Métodos

Métodos devem possuir apenas uma responsabilidade.

Preferencialmente:

20~30 linhas.

---

# Classes

Classes devem ser pequenas.

Alta coesão.

Baixo acoplamento.

---

# Dependências

Sempre utilizar:

```
Constructor Injection
```

Nunca utilizar:

```
@Autowired
```

em atributos.

---

# Versionamento

Commits seguirão o padrão:

```
feat:

fix:

refactor:

docs:

test:

style:

chore:
```

Exemplos

```
feat: create transaction endpoint

fix: validate duplicated category

docs: update architecture

refactor: improve goal service
```

---

# Branches

Padrão

```
main

develop

feature/*
```

Enquanto estivermos apenas nós desenvolvendo o projeto, poderemos trabalhar diretamente na main.

Caso o projeto cresça, adotaremos Git Flow completo.

---

# Pull Requests

Todo Pull Request deverá responder:

- O que foi feito?
- Por que foi feito?
- O que mudou?
- Existe impacto?

---

# Testes

Sempre que possível desenvolver:

- Unit Tests
- Integration Tests

---

# Cobertura

Objetivo futuro:

80%+

---

# Performance

Evitar:

- consultas N+1;
- carregamentos desnecessários;
- duplicação de código.

---

# Segurança

Nunca armazenar:

- senhas em texto;
- secrets no código;
- tokens hardcoded.

Utilizar sempre:

```
Environment Variables
```

---

# Documentação

Toda funcionalidade deverá possuir:

- documentação;
- endpoint documentado;
- regra de negócio registrada.

---

# Qualidade

Antes de finalizar qualquer funcionalidade, verificar:

✓ Código limpo

✓ SOLID

✓ DTO

✓ Mapper

✓ Exception

✓ Testes

✓ Documentação

✓ REST

---

# Regra de Ouro

Qualquer pessoa deve conseguir abrir este projeto daqui cinco anos e entender rapidamente como ele foi desenvolvido.