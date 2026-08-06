# Monexus Finance

> Plataforma de gestão financeira pessoal, construída com foco em arquitetura de software profissional — não apenas em "fazer funcionar".

![Status](https://img.shields.io/badge/status-V1%20conclu%C3%ADda-brightgreen)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen)
![React](https://img.shields.io/badge/React-19-blue)

## Sobre o projeto

O Monexus Finance é uma plataforma completa de gestão financeira pessoal — carteira, categorias, transações, metas e um dashboard com indicadores em tempo real. É, ao mesmo tempo, um produto funcional e um estudo aprofundado de arquitetura de software: cada decisão técnica (camadas, eventos, segurança, testes) foi documentada antes de ser implementada, e o histórico de commits reflete esse processo de construção incremental, não um código entregue de uma vez só.

Toda a documentação de domínio, arquitetura e regras de negócio está disponível na pasta [`docs/`](./docs).

## Funcionalidades

- **Autenticação completa**: cadastro, login com JWT, confirmação de e-mail, recuperação de senha, upload de foto de perfil
- **Carteira, Categorias e Transações**: CRUD completo, com validação de consistência entre tipos
- **Metas financeiras**: acompanhamento de progresso com barra visual
- **Dashboard**: indicadores em tempo real e gráfico de evolução mensal
- **Exclusão de conta em cascata**: remoção segura e atômica de todos os dados vinculados ao usuário

## Arquitetura e decisões técnicas

- **Package by Feature**, com regras explícitas de dependência entre módulos
- **Dependency Inversion Principle** aplicado para resolver acoplamento entre módulos sem dependência circular
- **Eventos síncronos vs. assíncronos**, usados conforme a criticidade da operação (ex: criação de carteira vs. envio de e-mail)
- **Saldo sempre calculado dinamicamente**, nunca persistido, com query agregada única (sem N+1)
- Documentação completa de domínio, regras de negócio e API em [`docs/`](./docs)

## Stack Tecnológica

**Backend**
- Java 25, Spring Boot 4.0, Spring Security, JWT
- Spring Data JPA, Hibernate, MySQL, Flyway
- MapStruct, Jakarta Validation
- JUnit 5, Mockito, Testcontainers
- Docker, Docker Compose, GitHub Actions (CI/CD)
- OpenAPI / Swagger

**Frontend**
- React, React Router
- Tailwind CSS
- Axios, Recharts

## Testes

O projeto conta com testes unitários (Mockito) cobrindo regras de negócio de todos os módulos, e testes de integração com **Testcontainers**, validando queries reais contra um banco MySQL.

```bash
mvn test      # testes unitários
mvn verify    # testes unitários + integração
```

A cada push na branch `main`, o GitHub Actions executa esse pipeline automaticamente.

## Como executar o projeto

### Pré-requisitos
- Java 25
- Node.js 18+
- MySQL 8 (ou Docker)

### Backend

```bash
# Com Docker (recomendado)
docker compose up -d

# Ou localmente, configurando as variáveis de ambiente (ver .env.example)
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`, com documentação interativa em `http://localhost:8080/swagger-ui/index.html`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`.

## Documentação completa

| Documento | Conteúdo |
|---|---|
| [01-Project-Vision.md](docs/01-Project-Vision.md) | Visão geral e objetivos do produto |
| [02-Software-Architecture.md](docs/02-Software-Architecture.md) | Arquitetura técnica e decisões estruturais |
| [03-Business-Rules.md](docs/03-Business-Rules.md) | Regras de negócio de cada módulo |
| [04-Domain-Model.md](docs/04-Domain-Model.md) | Modelo de domínio |
| [05-Database-Design.md](docs/05-Database-Design.md) | Estrutura do banco de dados |
| [06-API-Specification.md](docs/06-API-Specification.md) | Especificação da API REST |
| [07-Frontend-Design-System.md](docs/07-Frontend-Design-System.md) | Identidade visual e design system |
| [08-Development-Standards.md](docs/08-Development-Standards.md) | Padrões de desenvolvimento |
| [09-Roadmap.md](docs/09-Roadmap.md) | Evolução planejada do projeto |
| [10-Deployment.md](docs/10-Deployment.md) | Estratégia de implantação |
| [11-Change-Log.md](docs/11-Change-Log.md) | Histórico de mudanças |

## Roadmap

- [x] Autenticação e segurança
- [x] Carteira, Categorias, Transações, Metas
- [x] Dashboard com gráficos
- [x] Testes automatizados e CI/CD
- [x] Frontend funcional (React + Tailwind)
- [ ] Deploy em ambiente público
- [ ] Refinamento visual e animações
- [ ] Novas funcionalidades (v1.1+)

## Autor

**Breno Oliveira**
Estudante de Engenharia de Software e Análise e Desenvolvimento de Sistemas.

[LinkedIn](https://www.linkedin.com/in/breno-oliveira-souza/) · [GitHub](https://github.com/obrenoxs)
