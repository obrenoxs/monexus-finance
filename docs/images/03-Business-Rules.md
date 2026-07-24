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

Não será permitido alterar o e-mail após o cadastro.

---

## Confirmação de E-mail

Todo novo usuário deverá confirmar seu e-mail antes de utilizar completamente o sistema.

---

## Recuperação de Senha

O usuário poderá redefinir sua senha através do e-mail cadastrado.

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