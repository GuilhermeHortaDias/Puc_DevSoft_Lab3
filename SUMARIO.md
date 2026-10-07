# Sumário

[Contexto e integrantes](README.md)

## Entregáveis do projeto — Lab03S01

Este é o material para revisar com o grupo, entregar na disciplina e usar como base para o desenvolvimento.

| Entregável | O que contém | Abrir |
| --- | --- | --- |
| Casos de uso | Diagrama, atores, objetivos e fluxos | [Casos de uso](docs/modelagem/casos-de-uso.md) |
| Histórias do usuário | Necessidades dos perfis e critérios de aceitação | [Histórias](docs/modelagem/historias-do-usuario.md) |
| Classes | Diagrama de domínio, relações e restrições | [Classes](docs/modelagem/classes.md) |
| Componentes | Diagrama da arquitetura MVC e responsabilidades | [Componentes](docs/modelagem/componentes.md) |

[Requisitos e decisões](docs/modelagem/requisitos.md): referência única para conferir o que veio do enunciado, quais escolhas foram propostas e quais definições ainda precisam ser confirmadas.

Cada documento de diagrama contém sua imagem e os links para a fonte `.puml` e as exportações SVG/PNG.

## Entregáveis e aplicação — Lab03S02

| Entregável | O que contém | Abrir |
| --- | --- | --- |
| Modelo ER e persistência | Relações, esquema implementado, ORM e transações | [Modelo ER e estratégia](docs/modelagem/persistencia.md) |
| Backend | API dos CRUDs, autenticação, JPA, migrações e testes | [Código Java](backend/src/) |
| Frontend | Cadastro, login, consulta, edição e inativação dos dois perfis | [Código React](frontend/src/) |

As tabelas futuras do ER são identificadas como planejadas. O estado implementado e os comandos para executar e verificar a aplicação estão no [README](README.md).

## Insumos de elaboração — consulta opcional

[Referências utilizadas](docs/insumos/README.md): resumo do enunciado como fonte, aplicações semelhantes e documentação técnica consultada. Esse material explica a origem da modelagem; não faz parte dos quatro entregáveis nem acrescenta funcionalidades.

## Atualizar os diagramas

Edite o `.puml` correspondente e, com Java no PATH, execute na raiz do repositório:

```powershell
./scripts/renderizar-diagramas.ps1
```

O [script de renderização](scripts/renderizar-diagramas.ps1) usa PlantUML 1.2026.8, verifica sua distribuição e gera novamente as imagens SVG/PNG.
