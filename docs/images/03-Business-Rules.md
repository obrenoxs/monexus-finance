# Business Rules

## Objetivo

Este documento centraliza todas as regras de negócio do Monexus Finance.

Nenhuma funcionalidade poderá ser implementada sem que sua regra esteja previamente documentada.

---

# Usuário

## Cadastro

O usuário deverá informar:

- Nome
- Sobrenome
- E-mail
- Senha

---

## Foto de Perfil

A foto de perfil é opcional.

Caso o usuário não envie uma foto, o sistema utilizará um avatar padrão.

---

## E-mail

O e-mail deve ser único.

O usuário poderá alterar seu e-mail cadastrado, mediante confirmação
do novo endereço através de link enviado por e-mail.

O e-mail atual permanece válido para login até que o novo seja
confirmado com sucesso — a troca só é efetivada após a confirmação.

---

## Confirmação de E-mail

Todo novo usuário deverá confirmar seu e-mail antes de utilizar completamente o sistema.

---

## Recuperação de Senha

O usuário poderá redefinir sua senha através de um token enviado por e-mail, com validade de 24 horas e uso único.

Por motivo de segurança, o endpoint de solicitação de recuperação sempre retornará a mesma resposta genérica, independentemente de o e-mail informado estar ou não cadastrado no sistema — evitando que a API seja usada para descobrir quais e-mails possuem conta na plataforma (enumeração de usuários).

### Limitação conhecida (V1)

Ao trocar a senha com sucesso através desse fluxo, tokens JWT emitidos anteriormente para o usuário **não são invalidados automaticamente** — eles continuam válidos até sua expiração natural (24h).

Motivo: a autenticação do sistema é stateless (JWT sem estado no servidor), e invalidar tokens específicos antes da expiração exigiria um mecanismo de revogação (ex: blocklist de tokens, tipicamente com Redis), que não está no escopo de infraestrutura da V1.

Essa limitação é aceita conscientemente por ora e está registrada como melhoria futura de segurança.

---

## Exclusão da Conta

O próprio usuário poderá excluir sua conta.

Ao excluir a conta:

- carteira será removida;
- categorias serão removidas;
- metas serão removidas;
- receitas serão removidas;
- despesas serão removidas.

Toda exclusão será definitiva.

---

# Carteira

Cada usuário poderá possuir apenas uma carteira na versão 1.

A arquitetura será preparada para suportar múltiplas carteiras futuramente.

Uma carteira nunca poderá existir sem um usuário.

---

# Receitas

Toda receita deverá possuir obrigatoriamente:

- valor;
- descrição;
- data;
- categoria.

Campos opcionais:

- observações.

Receitas sempre aumentam o saldo da carteira.

Receitas poderão ser:

- editadas;
- excluídas.

---

# Despesas

Toda despesa deverá possuir obrigatoriamente:

- valor;
- descrição;
- data;
- categoria.

Campos opcionais:

- observações.

Despesas sempre reduzem o saldo da carteira.

Despesas poderão ser:

- editadas;
- excluídas.

---

# Categorias

Cada usuário poderá criar suas próprias categorias.

Categorias de receitas e despesas são independentes.

Exemplo:

Receita

Supermercado

Despesa

Supermercado

São categorias diferentes.

---

## Exclusão

Uma categoria somente poderá ser excluída caso não possua nenhuma movimentação vinculada.

Caso existam receitas ou despesas utilizando essa categoria, sua exclusão deverá ser impedida.

---

# Saldo

O saldo da carteira será calculado automaticamente.

Nunca será informado manualmente.

Fórmula:

Saldo = Total de Receitas − Total de Despesas

---

# Dashboard

Ao acessar o Dashboard Inicial, o usuário deverá visualizar imediatamente:

- saldo atual;
- receitas do mês;
- despesas do mês;
- saldo do mês.

Essas informações deverão possuir destaque visual.

---

# Indicadores

O Dashboard deverá apresentar:

- total de receitas;
- total de despesas;
- saldo atual;
- comparação com o mês anterior;
- gráficos financeiros.

---

# Metas Financeiras

Cada meta pertence à carteira do usuário.

Uma meta deverá possuir:

- nome;
- valor alvo;
- data prevista.

O sistema calculará automaticamente:

- progresso;
- percentual concluído;
- valor restante.

---

## Barra de Progresso

A barra deverá crescer conforme o saldo evoluir.

Quando possível, o sistema poderá apresentar mensagens motivacionais.

Exemplo:

"Faltam apenas R$ 800 para atingir sua meta."

---

# Valores Monetários

Todo valor financeiro será armazenado utilizando BigDecimal.

Nunca será utilizado:

- float
- double

---

# Datas

Todas as datas utilizarão:

- LocalDate
- LocalDateTime

---

# Tema

O sistema possuirá:

- tema claro (padrão);
- tema escuro.

O usuário poderá alternar entre ambos.

---

# Futuras Funcionalidades

As funcionalidades abaixo NÃO pertencem à versão 1.

Serão implementadas posteriormente.

- múltiplas carteiras;
- despesas futuras;
- receitas recorrentes;
- parcelamentos;
- investimentos;
- integração bancária;
- inteligência artificial;
- notificações;
- aplicativo mobile.

---

# Regra Geral

Sempre que surgir uma nova funcionalidade, sua regra deverá ser documentada antes da implementação.