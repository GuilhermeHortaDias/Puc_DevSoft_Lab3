# Entrega da sprint 1 — Lab03S01

**Sistema de Moeda Estudantil · Projeto de Software · PUC Minas · 2º semestre de 2026**

Integrantes: Guilherme Horta Dias, Lucas Batista Duarte e Rafael Abras.  
Professora: Milena Menezes Adão.

## Arquivo para entrega

**[Abrir o relatório completo em PDF](Lab03S01-Sistema-de-Moeda-Estudantil.pdf)**

O relatório reúne os quatro entregáveis solicitados na seção **4.1 — Lab03S01** do [enunciado](../LABORATORIO_3_LAB_DESENVOLVIMENTO_DE_SOFTWARE.pdf), com capa, sumário, diagramas vetoriais, especificações e requisitos rastreados. Os diagramas de classes e componentes usam páginas A3 em paisagem para permitir ampliação sem perda de qualidade.

[Versão HTML autossuficiente](Lab03S01-Sistema-de-Moeda-Estudantil.html) · [Roteiro para apresentação em aula](ROTEIRO_APRESENTACAO.md)

## Conteúdo conferido

| Entregável exigido | Conteúdo preparado | Fonte editável |
| --- | --- | --- |
| Diagrama de casos de uso | 5 atores, 12 casos de uso, relações de inclusão, fluxos e alternativas | [Documento](../modelagem/casos-de-uso.md) · [PlantUML](../modelagem/diagramas/casos-de-uso.puml) |
| Histórias do usuário | 14 histórias com critérios de aceitação, dependências e referências aos requisitos | [Histórias](../modelagem/historias-do-usuario.md) |
| Diagrama de classes | Usuários, instituições, contas, movimentações, vantagens, cupons e notificações; herança e multiplicidades | [Documento](../modelagem/classes.md) · [PlantUML](../modelagem/diagramas/classes.puml) |
| Diagrama de componentes | Arquitetura MVC, interfaces, persistência e integração com email | [Documento](../modelagem/componentes.md) · [PlantUML](../modelagem/diagramas/componentes.puml) |

Os [16 requisitos e as decisões de modelagem](../modelagem/requisitos.md) acompanham os quatro entregáveis. As questões abertas estão explícitas para discussão com a professora antes da implementação.

Esta sprint é de **modelagem**. Modelo ER, acesso ao banco e implementação dos CRUDs começam na Lab03S02, conforme seção 4.2 do enunciado.

## Apresentação e envio

1. Entregar o PDF no canal indicado pela disciplina e disponibilizar o repositório atualizado com os documentos e fontes UML.
2. Usar o roteiro para apresentar os quatro modelos e explicar as regras principais em aula. O enunciado exige apresentação semanal e prevê perda de 50% dos pontos da sprint em caso de não participação do grupo.
3. Confirmar com a professora as definições pendentes registradas nos requisitos, especialmente o vínculo entre professor e aluno e o calendário do crédito semestral.

O material está preparado localmente. A submissão à disciplina e a atualização do repositório remoto dependem do envio pelo grupo.

## Gerar novamente o relatório

O relatório é gerado diretamente dos documentos de `docs/modelagem/`, evitando manter outra cópia manual do conteúdo. Requer Python 3, os pacotes `Markdown` e `WeasyPrint` e as bibliotecas nativas do WeasyPrint disponíveis no sistema.

Na raiz do repositório:

```sh
python3 scripts/gerar-entrega-sprint1.py
```

Neste macOS, o Python das ferramentas da Apple precisa ser iniciado diretamente para receber o caminho das bibliotecas do Homebrew. Comando usado para gerar e conferir este PDF:

```sh
DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/lib \
  /Library/Developer/CommandLineTools/Library/Frameworks/Python3.framework/Versions/3.9/Resources/Python.app/Contents/MacOS/Python \
  scripts/gerar-entrega-sprint1.py
```

Para gerar somente o HTML, basta o pacote `Markdown`:

```sh
python3 scripts/gerar-entrega-sprint1.py --somente-html
```

Se os diagramas forem alterados, primeiro atualize SVG e PNG com o [renderizador PlantUML](../../scripts/renderizar-diagramas.ps1), depois gere o relatório novamente. O gerador verifica se as fontes incorporadas aos SVGs correspondem aos arquivos `.puml` antes de montar a entrega.
