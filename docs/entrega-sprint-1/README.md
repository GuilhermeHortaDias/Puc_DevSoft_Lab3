# Entrega histórica da sprint 1 — Lab03S01

**Sistema de Moeda Estudantil · Projeto de Software · PUC Minas · 2º semestre de 2026**

Integrantes: Guilherme Horta Dias, Lucas Batista Duarte e Rafael Abras.  
Professora: Milena Menezes Adão.

## Arquivo da entrega

**[Relatório completo em PDF](Lab03S01-Sistema-de-Moeda-Estudantil.pdf)** · [Versão HTML autossuficiente](Lab03S01-Sistema-de-Moeda-Estudantil.html) · [Roteiro de apresentação histórico](ROTEIRO_APRESENTACAO.md)

O PDF e o HTML preservam os quatro entregáveis preparados para a seção **4.1 — Lab03S01** do [enunciado](../LABORATORIO_3_LAB_DESENVOLVIMENTO_DE_SOFTWARE.pdf). Diagramas de classes e componentes usam páginas A3 em paisagem.

Esses arquivos são um registro da modelagem da sprint 1: incluem herança de classes, subtipos de movimentação e um agendador como integração proposta. A implementação posterior adotou associações por chave compartilhada, registro único de movimentação e agendador interno. Os modelos atuais estão nos documentos abaixo; não se deve regenerar ou substituir o PDF histórico a partir deles.

| Material atual | Documento |
| --- | --- |
| Casos de uso e fluxos | [Casos de uso](../modelagem/casos-de-uso.md) |
| Histórias e critérios de aceitação | [Histórias](../modelagem/historias-do-usuario.md) |
| Classes persistentes e relacionamentos | [Classes](../modelagem/classes.md) |
| Componentes lógicos e MVC | [Componentes](../modelagem/componentes.md) |
| Esquema e transações | [Modelo ER e persistência](../modelagem/persistencia.md) |
| Exigências e escolhas do grupo | [Requisitos e decisões](../modelagem/requisitos.md) |

Os `.puml`, SVG e PNG de `docs/modelagem/diagramas/` acompanham a implementação atual. O [renderizador PlantUML](../../scripts/renderizar-diagramas.ps1) atualiza esses diagramas; os comandos operacionais da aplicação estão no [README principal](../../README.md). A antiga instrução de geração por `scripts/gerar-entrega-sprint1.py` foi removida porque esse arquivo não existe no projeto.

## Apresentação e envio

A submissão à disciplina depende do grupo. O roteiro e o relatório refletem a apresentação da sprint 1; para apresentar o sistema implementado, consulte o código e a documentação atual. Definições ainda sujeitas à confirmação acadêmica estão explícitas nos requisitos, especialmente o significado de “seus alunos”.
