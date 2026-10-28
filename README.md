# Sistema de Moeda Estudantil

Projeto acadêmico da **PUC Minas**, curso de Engenharia de Software, disciplina **Projeto de Software**, professora **Milena Menezes Adão**. Laboratório 3 — segundo semestre de 2026.

O sistema reconhece o mérito estudantil com moedas virtuais. Professores distribuem moedas aos alunos, que podem trocá-las por produtos ou descontos de empresas parceiras. Cada professor recebe **1.000 moedas por semestre**, acumuladas com seu saldo restante. As trocas geram cupons enviados por email ao aluno e à empresa.

## Integrantes

- Guilherme Horta Dias
- Lucas Batista Duarte
- Rafael Abras

## Entregas do projeto

A **Sprint 1 (Lab03S01)** entregou a modelagem: casos de uso, histórias do usuário, classes e componentes. A **Sprint 2 (Lab03S02)** acrescenta o modelo ER, persistência SQLite/SQLAlchemy e CRUD inicial de aluno e empresa parceira com interface web conectada ao back-end.

**[Entrega da Sprint 2 e instruções para executar](docs/entrega-sprint-2/README.md)** · **[Modelo ER](docs/entrega-sprint-2/MODELO_ER.md)** · **[Roteiro de apresentação](docs/entrega-sprint-2/ROTEIRO_APRESENTACAO.md)**

**[PDF completo da Sprint 1](docs/entrega-sprint-1/Lab03S01-Sistema-de-Moeda-Estudantil.pdf)** · **[Roteiro de apresentação da Sprint 1](docs/entrega-sprint-1/ROTEIRO_APRESENTACAO.md)** · **[Sumário dos entregáveis](SUMARIO.md)**

## Organização

| Local | Conteúdo | Uso |
| --- | --- | --- |
| [docs/entrega-sprint-1/](docs/entrega-sprint-1/README.md) | Relatório consolidado em PDF/HTML e roteiro de apresentação | Entrega da Lab03S01 e apresentação em aula |
| [docs/entrega-sprint-2/](docs/entrega-sprint-2/README.md) | Modelo ER, decisões de persistência, escopo e roteiro | Entrega da Lab03S02 e instruções para executar |
| [docs/modelagem/](docs/modelagem/) | Quatro entregáveis, requisitos e decisões | Material do projeto para revisão, entrega e desenvolvimento |
| [docs/modelagem/diagramas/](docs/modelagem/diagramas/) | Fontes PlantUML e imagens dos três diagramas | Editar ou visualizar os modelos |
| [sprint2/](sprint2/) | Aplicação web, API, ORM/DAO e interface de cadastros | Implementação inicial da Lab03S02 |
| [docs/insumos/](docs/insumos/README.md) | Resumo das referências usadas na elaboração | Consulta opcional sobre a origem das escolhas |

Os arquivos `.puml` contêm o **código editável dos diagramas**. SVG e PNG são imagens desses mesmos diagramas. As histórias do usuário estão em Markdown.

A arquitetura modelada segue **MVC**, conforme o enunciado. As [decisões propostas e definições pendentes](docs/modelagem/requisitos.md) estão separadas das exigências do PDF.
