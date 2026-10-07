# Sistema de Moeda Estudantil

Projeto acadêmico da **PUC Minas**, curso de Engenharia de Software, disciplina **Projeto de Software**, professora **Milena Menezes Adão**. Laboratório 3 — segundo semestre de 2026.

O sistema reconhece o mérito estudantil com moedas virtuais. Professores distribuem moedas aos alunos, que podem trocá-las por produtos ou descontos de empresas parceiras. Cada professor recebe **1.000 moedas por semestre**, acumuladas com seu saldo restante. As trocas geram cupons enviados por email ao aluno e à empresa.

## Integrantes

- Guilherme Horta Dias
- Lucas Batista Duarte
- Rafael Abras

## O que está pronto

A **Lab03S01** contém os quatro modelos do sistema. A **versão inicial da Lab03S02** implementa os CRUDs de aluno e empresa parceira com front-end, API e persistência, além do modelo ER e da estratégia de acesso ao banco.

**[Abrir os entregáveis e a aplicação no SUMÁRIO](SUMARIO.md).**

Java 21 · Spring Boot 4.1.1 · Maven Wrapper · React 19/TypeScript · Vite 8 · PostgreSQL 17.11 · JPA/Hibernate · Spring Data JPA · Flyway · Spring Security.

Cada usuário consulta, edita e inativa **seu próprio cadastro**. Cadastro e login são públicos; outras ações exigem sessão e autorização. Inativação conserva os dados e encerra o acesso. Aluno começa com zero moedas; empresa não possui carteira. **Movimentações, vantagens, cupons e emails ainda não estão implementados.**

## Executar no Windows

Pré-requisitos: **JDK 21**, **Node 24 LTS** e **Docker Desktop** com mecanismo Linux iniciado. O Maven Wrapper baixa o Maven usado pelo projeto, sem instalação global. O script encontra o JDK pelo `javac`, evitando um Java 8 antigo no PATH; se necessário, passe `-JavaHome 'caminho/do/jdk-21'`.

Na raiz do repositório, prepare o PostgreSQL e as dependências do front-end:

```powershell
./scripts/dev.ps1 -Acao ambiente
```

Em dois terminais separados, também na raiz:

```powershell
./scripts/dev.ps1 -Acao backend
./scripts/dev.ps1 -Acao frontend
```

Abra **http://127.0.0.1:5173**. Vite encaminha `/api` ao backend em `127.0.0.1:8080`, preservando cookies e CSRF. PostgreSQL fica disponível apenas localmente na porta 5432 e conserva os dados em volume Docker. A API aplica as migrações ao iniciar; a instituição inicial é PUC Minas. Não há usuários ou senhas de demonstração: crie seu cadastro pela interface.

O Compose e o backend usam a credencial de desenvolvimento `lab3_local`. Para personalizar, copie `.env.example` para `.env` e altere `DB_PASSWORD` **antes da primeira criação do banco**. `.env` é ignorado pelo Git e carregado pelo script. Variáveis de conexão são `DB_URL`, `DB_USER` e `DB_PASSWORD`; em outros ambientes, configure credenciais próprias e HTTPS/cookie Secure. Não apague o volume para reiniciar a aplicação.

Para parar o banco, use `docker compose stop`. Para iniciá-lo novamente, use `docker compose up -d --wait`.

## Verificar

Com Docker funcionando, execute testes de integração, lint, formatação e build:

```powershell
./scripts/dev.ps1 -Acao verificar
```

Os testes do backend usam um PostgreSQL temporário via Testcontainers, separado do banco de desenvolvimento. Para os testes de navegador, mantenha o backend iniciado e execute:

```powershell
cd frontend
npx.cmd playwright install chromium
npm.cmd run test:e2e
```

Playwright inicia o front-end automaticamente se necessário e percorre os dois CRUDs contra a API real. Ele cria cadastros sintéticos com prefixo `e2e-` e termina inativando-os; o histórico permanece no banco local. Não execute esse conjunto contra dados de produção. A integração contínua em GitHub Actions repete a verificação em ambiente isolado.

## API inicial

| Recurso | Operações |
| --- | --- |
| `/api/alunos` e `/api/empresas` | `POST` cadastro público; `GET` lista somente o cadastro do titular |
| `/api/alunos/{id}` e `/api/empresas/{id}` | `GET` consulta, `PUT` atualização e `DELETE` inativação, somente pelo titular |
| `/api/instituicoes` | `GET` lista para o cadastro do aluno |
| `/api/auth/csrf` | `GET` token e nome do header para operações que alteram estado |
| `/api/auth/login` | `POST` formulário com `username` e `password` |
| `/api/auth/me` | `GET` sessão atual |
| `/api/auth/logout` | `POST` encerra a sessão |

Cadastro recebe `{ login, senha, dados }`; atualização recebe apenas `dados`. Campos e restrições estão em `CadastrosDtos.java`. Sucesso no cadastro retorna `201` e `Location`; inativação retorna `204`; validação `400`; ausência de sessão `401`; acesso indevido/CSRF `403`; duplicidade `409`. A [documentação de persistência](docs/modelagem/persistencia.md) registra as decisões e limitações.

## Organização

| Local | Conteúdo | Uso |
| --- | --- | --- |
| [docs/modelagem/](docs/modelagem/) | Entregáveis das fases 1 e 2, requisitos e decisões | Material do projeto para revisão, entrega e desenvolvimento |
| [docs/modelagem/diagramas/](docs/modelagem/diagramas/) | Fontes PlantUML e imagens dos quatro diagramas | Editar ou visualizar os modelos |
| [docs/insumos/](docs/insumos/README.md) | Resumo das referências usadas na elaboração | Consulta opcional sobre a origem das escolhas |
| `backend/` e `frontend/` | Código executável e testes | Desenvolvimento da fase 2 |
| `compose.yaml` e `scripts/dev.ps1` | Banco local e comandos de ambiente | Executar e verificar o projeto |
| [AGENTS.md](AGENTS.md) | Orientações curtas para assistentes de IA | Manter escopo, arquitetura e verificação nas próximas alterações |

Os arquivos `.puml` contêm o **código editável dos diagramas**. SVG e PNG são imagens desses mesmos diagramas. As histórias do usuário estão em Markdown.

A arquitetura modelada segue **MVC**, conforme o enunciado. As [decisões propostas e definições pendentes](docs/modelagem/requisitos.md) estão separadas das exigências do PDF.
