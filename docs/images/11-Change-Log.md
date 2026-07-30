# Change Log

Todas as alterações relevantes realizadas no Monexus Finance deverão ser registradas neste documento.

Este projeto segue o princípio de evolução contínua.

---

# [Unreleased]

## Added

### Autenticação e Segurança

- Cadastro de usuário com validação de dados e hash de senha via BCrypt.
- Login com autenticação stateless via JWT (Spring Security).
- Confirmação de e-mail obrigatória via token de uso único (UUID), com expiração de 24h.
- Recuperação de senha via token enviado por e-mail (expiração de 1h), com resposta genérica para prevenir enumeração de usuários.
- Perfil do usuário: consulta (`GET /users/me`) e atualização de dados (`PUT /users/me`).
- Upload de foto de perfil via integração com Cloudinary, com validação de tipo e tamanho de arquivo (máx. 8MB).
- Alteração de e-mail com confirmação obrigatória do novo endereço e reautenticação por senha.
- Exclusão de conta com reautenticação por senha.
- Envio de e-mails transacionais via Mailtrap (ambiente de desenvolvimento), com SimpleMailMessage.
- Tratamento centralizado de exceções via `@RestControllerAdvice`, incluindo validação de campos com retorno detalhado por campo.

## Changed

- Migração da organização de pacotes de Package by Layer para Package by Feature.
- Padrão de erro da API expandido para incluir lista detalhada de campos inválidos (`errors`) em respostas de validação (422).

## Security

- Senhas armazenadas exclusivamente como hash BCrypt, nunca em texto plano.
- Autenticação JWT stateless, sem guarda de sessão no servidor.
- Respostas de "esqueci minha senha" e login padronizadas para não revelar existência de e-mails cadastrados.
- Operações sensíveis (troca de e-mail, exclusão de conta) exigem reautenticação por senha, mesmo com sessão já autenticada.

## Known Limitations

- Tokens JWT emitidos antes de uma troca de senha não são invalidados automaticamente (permanecem válidos até expiração natural). Mecanismo de revogação (blocklist) planejado para versão futura.

---

# [1.0.0] - Em desenvolvimento

## Objetivo

Primeira versão funcional do Monexus Finance.

---

## Funcionalidades

### Usuário

- Cadastro
- Login
- JWT
- Recuperação de senha
- Confirmação de e-mail
- Perfil

---

### Carteira

- Uma carteira por usuário

---

### Categorias

- CRUD completo

---

### Transações

- Receitas
- Despesas

---

### Dashboard

- Saldo atual
- Receitas do mês
- Despesas do mês
- Comparação mensal
- Gráficos

---

### Metas

- CRUD
- Barra de progresso

---

### Backend

- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Flyway

---

### Frontend

- React
- Tailwind CSS
- Light Theme
- Dark Theme

---

### DevOps

- Docker
- Docker Compose
- GitHub Actions

---

### Documentação

- OpenAPI
- Swagger

---

# Convenção

Sempre que uma alteração importante for realizada, utilizar uma das categorias abaixo.

## Added

Novas funcionalidades.

Exemplo:

```
Added

- Goal CRUD.
```

---

## Changed

Mudanças de comportamento.

Exemplo:

```
Changed

- Dashboard redesign.
```

---

## Deprecated

Funcionalidades que serão removidas.

---

## Removed

Funcionalidades removidas.

---

## Fixed

Correções.

Exemplo:

```
Fixed

- JWT expiration bug.
```

---

## Security

Correções relacionadas à segurança.

Exemplo:

```
Security

- Password reset validation.
```

---

# Política

O Change Log deverá registrar apenas mudanças relevantes.

Pequenas alterações internas de código não precisam ser registradas.

---

# Versionamento

O projeto seguirá Semantic Versioning.

Formato:

```
MAJOR.MINOR.PATCH
```

Exemplo:

```
1.0.0

1.0.1

1.1.0

2.0.0
```

---

## MAJOR

Mudanças incompatíveis.

---

## MINOR

Novas funcionalidades compatíveis.

---

## PATCH

Correções.

---

# Objetivo

Permitir que qualquer pessoa acompanhe facilmente toda a evolução do Monexus Finance ao longo do tempo.