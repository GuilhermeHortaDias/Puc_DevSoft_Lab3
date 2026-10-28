# Sumário

[Contexto e integrantes](README.md)

## Entregáveis do projeto — Lab03S01

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

## Persistência e aplicação — Lab03S02 e Lab03S03

| Entregável | O que contém | Abrir |
| --- | --- | --- |
| Modelo ER e persistência | Relações, esquema implementado, ORM e transações | [Modelo ER e estratégia](docs/modelagem/persistencia.md) |
| Backend | Cadastros, acesso dos três perfis, moedas, vantagens, cupons, SMTP e JPA | [Código Java](backend/src/) |
| Frontend | Cadastros, professor, saldo/extrato, reconhecimento, catálogo e resgate | [Código React](frontend/src/) |

O ER e os diagramas atuais acompanham as migrações e o código. A entrega consolidada da sprint 1 é preservada como registro histórico. Comandos de execução e compilação sem suítes de testes estão no [README](README.md). Os slides e o tutorial da apresentação final ainda dependem da preparação acadêmica do grupo.

## Insumos de elaboração — consulta opcional

[Referências utilizadas](docs/insumos/README.md): resumo do enunciado como fonte, aplicações semelhantes e documentação técnica consultada. Esse material explica a origem da modelagem; não faz parte dos quatro entregáveis nem acrescenta funcionalidades.

## Atualizar os diagramas

Edite o `.puml` correspondente e, com Java no PATH, execute na raiz do repositório:

```powershell
./scripts/renderizar-diagramas.ps1
```

O [script de renderização](scripts/renderizar-diagramas.ps1) usa PlantUML 1.2026.8, verifica sua distribuição e gera novamente as imagens SVG/PNG.
