# Sistema de Moeda Estudantil

Projeto acadêmico da **PUC Minas**, curso de Engenharia de Software, disciplina **Projeto de Software**, professora **Milena Menezes Adão**. Laboratório 3 — segundo semestre de 2026.

O sistema reconhece o mérito estudantil com moedas virtuais. Professores distribuem moedas aos alunos, que podem trocá-las por produtos ou descontos de empresas parceiras. Cada professor recebe **1.000 moedas por semestre**, acumuladas com seu saldo restante. As trocas geram cupons enviados por email ao aluno e à empresa.

## Integrantes

- Guilherme Horta Dias
- Lucas Batista Duarte
- Rafael Abras

## O que está pronto

A aplicação implementa os cadastros de aluno/empresa, o acesso dos três perfis, o crédito semestral, o envio e o extrato de moedas, as vantagens com foto, o resgate e os cupons com emails para aluno e empresa. A arquitetura segue MVC, com persistência JPA e migrações Flyway.

**[Abrir os entregáveis e a aplicação no SUMÁRIO](SUMARIO.md).**

[Relatório da sprint 1](docs/entrega-sprint-1/Lab03S01-Sistema-de-Moeda-Estudantil.pdf) · [Roteiro da sprint 1](docs/entrega-sprint-1/ROTEIRO_APRESENTACAO.md). Esses arquivos preservam a entrega histórica da primeira etapa; os modelos atuais ficam em `docs/modelagem/`.

Java 21 · Spring Boot 4.1.1 · Maven Wrapper · React 19/TypeScript · Vite 8 · PostgreSQL 17.11 · JPA/Hibernate · Spring Data JPA · Flyway · Spring Security.

| Perfil | Funções |
| --- | --- |
| Aluno | Cadastro próprio, edição/inativação, saldo, extrato, catálogo e resgate com cupom |
| Professor | Dados institucionais, saldo/extrato, seleção de aluno e envio com motivo obrigatório |
| Empresa | Cadastro próprio, edição/inativação, publicação de vantagem com foto e lista das próprias ofertas |

Cadastro inicial e login são públicos; demais operações exigem sessão e perfil autorizado. Exclusão significa inativação, conservando o histórico. Professores são preparados pela instituição, sem cadastro público. O vínculo professor–aluno é aproximado pela mesma instituição, conforme [decisão D09](docs/modelagem/requisitos.md).

Aluno inicia com zero moedas; empresa não tem carteira. O professor recebe 1.000 por semestre, somadas ao saldo restante, com processamento interno na inicialização e em janeiro/julho. Envio e resgate confirmam saldos, histórico e intenção de email juntos. Identificação de comando e bloqueio de contas protegem contra repetição do débito.

As implementações foram compiladas e passaram por uma verificação básica de integração. **A campanha completa de testes fica para a próxima etapa**, conforme decisão do grupo. Slides e tutorial da apresentação final são entregas acadêmicas separadas do código.

## Executar no Windows

Pré-requisitos: **JDK 21**, **Node 24 LTS** e **Docker Desktop** com mecanismo Linux iniciado. O Maven Wrapper baixa o Maven usado pelo projeto, sem instalação global. O script encontra o JDK pelo `javac`, evitando um Java 8 antigo no PATH; se necessário, passe `-JavaHome 'caminho/do/jdk-21'`.

Na primeira execução, prepare a configuração local, o banco, o Mailpit e as dependências:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
./scripts/dev.ps1 -Acao ambiente
```

Em dois terminais separados, também na raiz:

```powershell
./scripts/dev.ps1 -Acao backend
./scripts/dev.ps1 -Acao frontend
```

Abra **http://127.0.0.1:5173**. Por padrão, a API usa 8080, PostgreSQL usa 5432 e o Mailpit recebe SMTP em 1025 e mostra os emails em **http://127.0.0.1:8025**. As portas ficam configuráveis em `.env`; o banco conserva dados em volume Docker e recebe as migrações na inicialização.

A ação `ambiente` gera `.local/institucional.json` quando esse arquivo não existe: instituição PUC Minas, departamento e **professor fictício** com login `professor.demo` e senha aleatória. Consulte a senha nesse arquivo local; ela não é impressa nem versionada. Alunos e empresas se cadastram pela interface.

Para usar a lista institucional real, preencha uma cópia privada do [exemplo JSON](backend/config/institucional.exemplo.json), substitua `SENHA_PLACEHOLDER` e informe `INSTITUCIONAL_ARQUIVO` em `.env` ou `-ArquivoInstitucional` ao script. A importação é transacional e idempotente: não duplica contas nem redefine senhas existentes; divergências de identidade interrompem a importação. Os dados oficiais e credenciais não devem ser colocados no Git.

Se as portas estiverem ocupadas, ajuste os pares `DB_PORT`/porta em `DB_URL`, `SERVER_PORT`/`VITE_API_TARGET` e `MAILPIT_SMTP_PORT`/`SMTP_PORT`; a interface do Mailpit usa `MAILPIT_HTTP_PORT`. O script carrega `.env` antes de iniciar cada serviço. O backend usa `DB_URL`, `DB_USER` e `DB_PASSWORD`; a credencial padrão do banco serve apenas ao desenvolvimento local. Altere-a antes de criar o volume; mudar `.env` depois não altera a senha já gravada no PostgreSQL.

Para parar o banco, use `docker compose stop`. Para iniciá-lo novamente, use `docker compose up -d --wait`.

## Compilar nesta etapa

Compile Java/React e confira lint e formatação, **sem executar suítes de testes**:

```powershell
./scripts/dev.ps1 -Acao compilar
```

A ação `verificar` e os testes existentes foram preservados para execução posterior. O backend usa PostgreSQL temporário via Testcontainers. Para executar os testes de navegador futuramente, com o backend iniciado:

```powershell
cd frontend
npx.cmd playwright install chromium
npm.cmd run test:e2e
```

Playwright inicia o frontend quando necessário e percorre os dois CRUDs contra a API real. Cria cadastros sintéticos `e2e-` e termina inativando-os; o histórico permanece no banco local. GitHub Actions executa build/lint/formatação em push/PR; as suítes existentes são opcionais em `workflow_dispatch`, com `executar_testes=true`.

## Emails

Spring Mail envia as notificações registradas após a confirmação da operação. O worker tenta o envio periodicamente e conserva falhas para repetição, sem movimentar moedas de novo. No desenvolvimento, Mailpit captura os emails; não os entrega a caixas externas.

Para entrega externa, configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_FROM`, `SMTP_AUTH` e `SMTP_STARTTLS` conforme o provedor. Os dois emails de resgate usam o mesmo código, disponível também no extrato. A conferência permanece presencial, sem baixa digital de cupom.

## API

| Recurso | Operações |
| --- | --- |
| `/api/alunos` e `/api/empresas` | `POST` cadastro público; `GET` lista somente o cadastro do titular |
| `/api/alunos/{id}` e `/api/empresas/{id}` | `GET` consulta, `PUT` atualização e `DELETE` inativação, somente pelo titular |
| `/api/instituicoes` | `GET` lista para o cadastro do aluno |
| `/api/auth/csrf` | `GET` token e nome do header para operações que alteram estado |
| `/api/auth/login` | `POST` formulário com `username` e `password` |
| `/api/auth/me` | `GET` sessão atual |
| `/api/auth/logout` | `POST` encerra a sessão |
| `/api/professores/me` | `GET` dados do professor autenticado |
| `/api/professores/alunos` | `GET` alunos ativos da instituição do professor |
| `/api/conta` | `GET` saldo e extrato do aluno/professor |
| `/api/moedas/envios` | `POST` `{ alunoId, quantidade, motivo, requisicaoId }`, somente professor |
| `/api/moedas/resgates` | `POST` `{ vantagemId, requisicaoId }`, somente aluno; retorna o código do cupom |
| `/api/vantagens` | `GET` catálogo do aluno ou ofertas próprias da empresa; `POST` multipart com descrição, custo e foto, somente empresa |
| `/api/vantagens/{id}/foto` | `GET` foto da vantagem, com sessão autorizada |

Cadastro recebe `{ login, senha, dados }`; atualização recebe apenas `dados`. Campos e restrições estão em `CadastrosDtos.java`. Sucesso no cadastro retorna `201` e `Location`; inativação retorna `204`; validação `400`; ausência de sessão `401`; acesso indevido/CSRF `403`; duplicidade `409`. A [documentação de persistência](docs/modelagem/persistencia.md) registra as decisões e limitações.

Envio/resgate usam `requisicaoId` UUID: repetir o mesmo comando retorna o registro original; reutilizar a identificação para outro comando gera conflito. A foto precisa ser PNG/JPEG válido de até 2 MB e 8 megapixels. O valor resgatado e a descrição da vantagem são preservados no histórico.

## Entrega local da Sprint 2

A implementação inicial em SQLite/SQLAlchemy está preservada em [sprint2/](sprint2/). Consulte a [documentação da Sprint 2](docs/entrega-sprint-2/README.md), o [modelo ER](docs/entrega-sprint-2/MODELO_ER.md) e o [roteiro de apresentação](docs/entrega-sprint-2/ROTEIRO_APRESENTACAO.md).

## Organização

| Local | Conteúdo | Uso |
| --- | --- | --- |
| [docs/modelagem/](docs/modelagem/) | Modelos atuais, requisitos e decisões | Material alinhado à implementação |
| [docs/modelagem/diagramas/](docs/modelagem/diagramas/) | Fontes PlantUML e imagens dos quatro diagramas | Editar ou visualizar os modelos |
| [docs/insumos/](docs/insumos/README.md) | Resumo das referências usadas na elaboração | Consulta opcional sobre a origem das escolhas |
| `backend/` e `frontend/` | Código executável e testes existentes | Aplicação |
| `compose.yaml` e `scripts/dev.ps1` | Banco local e comandos de ambiente | Executar e verificar o projeto |
| [AGENTS.md](AGENTS.md) | Orientações curtas para assistentes de IA | Manter escopo, arquitetura e verificação nas próximas alterações |

Os arquivos `.puml` contêm o **código editável dos diagramas**. SVG e PNG são imagens desses mesmos diagramas. As histórias do usuário estão em Markdown.

A arquitetura modelada segue **MVC**, conforme o enunciado. As [decisões propostas e definições pendentes](docs/modelagem/requisitos.md) estão separadas das exigências do PDF.
