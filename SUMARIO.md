# Sumário

[Contexto e integrantes](README.md)

## Entrega atual — Lab03S02 / Sprint 2

**[Entrega, escopo e instruções para executar](docs/entrega-sprint-2/README.md)** · [Modelo ER](docs/entrega-sprint-2/MODELO_ER.md) · [Roteiro de apresentação](docs/entrega-sprint-2/ROTEIRO_APRESENTACAO.md)

O código executável está em [sprint2/](sprint2/). A aplicação inclui CRUD inicial de aluno e empresa parceira, interface web, API JSON, SQLAlchemy ORM e DAOs sobre SQLite.

## Entregáveis de modelagem — Lab03S01 / Sprint 1

Este é o material para revisar com o grupo, entregar na disciplina e usar como base para o desenvolvimento.

**[Relatório completo da sprint 1 em PDF](docs/entrega-sprint-1/Lab03S01-Sistema-de-Moeda-Estudantil.pdf)** · [Entrega e geração do relatório](docs/entrega-sprint-1/README.md) · [Roteiro de apresentação](docs/entrega-sprint-1/ROTEIRO_APRESENTACAO.md)

| Entregável | O que contém | Abrir |
| --- | --- | --- |
| Casos de uso | Diagrama, atores, objetivos e fluxos | [Casos de uso](docs/modelagem/casos-de-uso.md) |
| Histórias do usuário | Necessidades dos perfis e critérios de aceitação | [Histórias](docs/modelagem/historias-do-usuario.md) |
| Classes | Diagrama de domínio, relações e restrições | [Classes](docs/modelagem/classes.md) |
| Componentes | Diagrama da arquitetura MVC e responsabilidades | [Componentes](docs/modelagem/componentes.md) |

[Requisitos e decisões](docs/modelagem/requisitos.md): referência única para conferir o que veio do enunciado, quais escolhas foram propostas e quais definições ainda precisam ser confirmadas.

Cada documento de diagrama contém sua imagem e os links para a fonte `.puml` e as exportações SVG/PNG.

## Insumos de elaboração — consulta opcional

[Referências utilizadas](docs/insumos/README.md): resumo do enunciado como fonte, aplicações semelhantes e documentação técnica consultada. Esse material explica a origem da modelagem; não faz parte dos quatro entregáveis nem acrescenta funcionalidades.

## Atualizar os diagramas

Edite o `.puml` correspondente e, com Java no PATH, execute na raiz do repositório:

```powershell
./scripts/renderizar-diagramas.ps1
```

O [script de renderização](scripts/renderizar-diagramas.ps1) usa PlantUML 1.2026.8, verifica sua distribuição e gera novamente as imagens SVG/PNG.
