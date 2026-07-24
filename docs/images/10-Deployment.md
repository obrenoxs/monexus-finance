# Deployment

## Objetivo

Este documento define toda a estratégia de implantação (Deployment) do Monexus Finance.

Seu objetivo é garantir que qualquer desenvolvedor consiga colocar a aplicação em funcionamento seguindo um processo padronizado.

---

# Ambientes

O Monexus Finance possuirá três ambientes principais.

## Development

Utilizado durante o desenvolvimento.

Características:

- banco local;
- Docker;
- logs detalhados;
- Hot Reload;
- Swagger habilitado.

---

## Staging

Ambiente para testes antes da produção.

Características:

- infraestrutura semelhante à produção;
- banco isolado;
- testes finais;
- validação de novas funcionalidades.

---

## Production

Ambiente utilizado pelos usuários.

Características:

- alta disponibilidade;
- segurança reforçada;
- logs controlados;
- monitoramento;
- backups.

---

# Tecnologias

Backend

- Java 25
- Spring Boot

Banco

- MySQL

Frontend

- React
- Tailwind CSS

Infraestrutura

- Docker
- Docker Compose

CI/CD

- GitHub Actions

---

# Configurações

Todas as configurações sensíveis deverão utilizar:

```
Environment Variables
```

Nunca deverão ser armazenadas no código.

Exemplos:

- senha do banco;
- JWT Secret;
- credenciais SMTP;
- API Keys.

---

# Profiles

O projeto utilizará Profiles do Spring.

```
application.yml

application-dev.yml

application-staging.yml

application-prod.yml
```

Cada ambiente possuirá suas próprias configurações.

---

# Docker

Cada serviço possuirá seu próprio container.

Inicialmente:

- Backend
- MySQL

Futuramente poderão existir:

- Redis
- Nginx
- Prometheus
- Grafana

---

# Docker Compose

O Docker Compose será utilizado para facilitar o ambiente de desenvolvimento.

Responsável por iniciar:

- banco;
- backend;
- frontend (futuramente).

---

# CI/CD

Toda alteração enviada para a branch principal deverá executar automaticamente:

- Build
- Testes
- Geração do JAR
- Build da imagem Docker
- Publicação da imagem

---

# Versionamento das Imagens

Formato:

```
latest

1.0.0

1.0.1

1.1.0
```

Nunca depender exclusivamente da tag `latest`.

---

# Banco de Dados

Todas as alterações estruturais serão realizadas através de migrations.

Será utilizado:

```
Flyway
```

Nenhuma alteração manual deverá ser realizada diretamente no banco de produção.

---

# Logs

O sistema utilizará logs estruturados.

Em produção:

- INFO
- WARN
- ERROR

Logs DEBUG permanecerão desabilitados.

---

# Monitoramento

Planejamento futuro:

- Prometheus
- Grafana
- Health Checks
- Métricas da aplicação

---

# Backups

O banco deverá possuir rotina automática de backup.

Planejamento:

- backup diário;
- retenção configurável;
- restauração validada periodicamente.

---

# Segurança

Todos os ambientes deverão utilizar:

- HTTPS;
- JWT;
- BCrypt;
- variáveis de ambiente;
- validação de entrada.

---

# Deploy Inicial

Durante a primeira versão, o sistema será executado localmente através do Docker Compose.

A publicação em nuvem ocorrerá em uma versão posterior.

---

# Planejamento de Hospedagem

A infraestrutura deverá ser preparada para permitir implantação em:

- AWS
- Google Cloud Platform
- Azure (futuro)

Sem necessidade de alterações significativas na arquitetura.

---

# Escalabilidade

A arquitetura deverá permitir futuramente:

- múltiplas instâncias da API;
- balanceamento de carga;
- cache distribuído;
- filas de processamento.

---

# Filosofia

A implantação deve ser:

- automatizada;
- reproduzível;
- segura;
- simples.

Qualquer desenvolvedor deve conseguir colocar o Monexus Finance em funcionamento seguindo este documento.