# Diagramas, notação e escolha da ferramenta

**Fontes consultadas em 07/10/2026.** UML é a linguagem de modelagem; PlantUML, Mermaid, Miro e FigJam são ferramentas com recursos diferentes para representar os modelos.

## 1. Casos de uso

Representam objetivos observáveis dos atores na fronteira do sistema. Monte o modelo identificando os perfis externos, os objetivos de cada um e só depois os comportamentos compartilhados.

| Forma/relação | Leitura | Uso neste projeto |
| --- | --- | --- |
| Ator, figura humana ou elemento com estereótipo | Papel externo, humano ou sistema | Aluno, professor, empresa, email e agendador |
| Elipse | Objetivo ou comportamento do sistema | Enviar moedas, resgatar vantagem |
| Retângulo de fronteira | Define o que pertence ao sistema | Sistema de Moeda Estudantil |
| Linha de associação | Participação do ator | Professor participa do envio |
| Seta tracejada `<<include>>` | Do caso base ao comportamento obrigatório incluído | Resgate inclui emissão do cupom |
| Seta tracejada `<<extend>>` | Da extensão para o caso base, sob condição | Disponível, sem necessidade nesta versão |
| Triângulo vazio de generalização | Do especializado para o geral | Disponível, sem necessidade entre os perfis |

Não usar setas para ordenar o tempo e não transformar validações internas em objetivos artificiais. Login é um caso de uso; sessão autenticada é precondição das operações protegidas, sem obrigar novo login em cada ação.

Referências: [OMG UML 2.5.1](https://www.omg.org/spec/UML/2.5.1/PDF), capítulo 18, e [PlantUML: casos de uso](https://plantuml.com/use-case-diagram).

## 2. Histórias do usuário

Histórias são descrições textuais de valor, não um diagrama UML. Neste projeto usam **Como [perfil], quero [objetivo], para [benefício]**, identificador, requisito relacionado e critérios no formato **Dado / Quando / Então**.

Revise cada história com INVEST: independente quando possível, negociável, valiosa, estimável, pequena e testável. As dependências de autenticação e de saldo ficam explícitas. Critérios incluem sucesso, limites e recusas; por exemplo, tentar enviar mais moedas que o saldo mantém as contas inalteradas. Não escrever histórias como “criar controller” ou “criar tabela”, pois são tarefas técnicas.

Referência: [Agile Alliance: User Stories](https://agilealliance.org/glossary/user-stories/).

## 3. Classes

O diagrama de classes descreve estrutura do domínio e responsabilidades. Cada classe aparece em um retângulo com nome, atributos e operações. Identifique entidades, atribua dados e comportamentos, conecte relações e revise as multiplicidades nos dois extremos.

| Recurso | Notação/leitura |
| --- | --- |
| Atributo e operação | `nome: Tipo`; `operacao(parametro: Tipo): Retorno` |
| Visibilidade | `+` pública, `-` privada, `#` protegida, `~` de pacote |
| Associação | Linha contínua; papéis e multiplicidades contextualizam o vínculo |
| Multiplicidade | `1`, `0..1`, `0..*`, `1..*` |
| Generalização | Linha contínua com triângulo vazio apontando à classe geral |
| Realização de interface | Linha tracejada com triângulo vazio apontando à interface |
| Composição | Losango preenchido no todo, com dependência de existência da parte |
| Agregação compartilhada | Losango vazio no todo; usar apenas quando houver semântica clara |
| Dependência | Seta tracejada: um elemento usa outro |
| Classe abstrata, enumeração e restrição | Especialização comum, conjunto de valores e regras anexas |

Foi adotado um modelo de domínio, não um ER nem um desenho completo das classes de implementação. Tipos `Identificador`, `Inteiro` e `DataHora` são conceituais. As regras de transação são descritas junto do modelo e coordenadas pelos serviços do componente Model.

Referências: [OMG UML 2.5.1](https://www.omg.org/spec/UML/2.5.1/PDF), capítulos 7, 9, 10 e 11, e [PlantUML: classes](https://plantuml.com/class-diagram).

## 4. Componentes

Representam unidades lógicas, responsabilidades e contratos. Monte o diagrama agrupando as responsabilidades MVC, identificando interfaces fornecidas e dependências, e separando integrações externas.

| Forma/recurso | Leitura |
| --- | --- |
| Retângulo com ícone de componente | Unidade modular com responsabilidade própria |
| Pacote | Agrupamento, como View, Controller e Model |
| Interface em círculo | Contrato fornecido; implementação ligada ao círculo |
| Interface requerida em semicírculo | Alternativa para representar consumo e montagem |
| Dependência tracejada | Consumidor aponta ao contrato ou componente que usa |
| Realização | Implementação aponta à interface com triângulo vazio tracejado |
| Porta | Pequeno quadrado na fronteira, quando pontos de interação precisam de destaque |

Nesta versão, círculos representam interfaces fornecidas, com dependências dos consumidores. Não há necessidade de portas ou conectores de montagem detalhados. Os componentes são lógicos e podem coexistir na mesma aplicação; não representam servidores ou microserviços obrigatórios.

Referências: [OMG UML 2.5.1](https://www.omg.org/spec/UML/2.5.1/PDF), capítulo 11, e [PlantUML: componentes](https://plantuml.com/component-diagram).

## 5. Comparação orientada à entrega

| Ferramenta | Pontos favoráveis | Limites para este trabalho | Avaliação |
| --- | --- | --- | --- |
| **PlantUML** | Sintaxes próprias para casos de uso, classes e componentes; texto em Git; exportações SVG/PNG | Layout automático pode exigir ajuste; renderização precisa de runtime ou serviço | **Escolhida como fonte oficial** |
| Mermaid | Fonte textual; classes; integração de diagramas em Markdown do GitHub | A documentação atual oferece `usecase-beta` a partir da v12; compatibilidade depende da versão do renderizador. `architecture-beta` não equivale por si só ao diagrama UML de componentes | Alternativa, com verificação de versão e notação |
| Miro | Quadro colaborativo, formas e conectores; biblioteca UML | Biblioteca UML listada para planos Business, Enterprise e Education; edição visual não oferece o mesmo diff textual dos `.puml` | Útil para oficinas, sem necessidade nesta entrega |
| Figma/FigJam | Canvas colaborativo, templates UML, formas e conectores | Layout e coerência UML precisam de manutenção visual; código textual do diagrama não é o artefato principal | Útil para apresentação e discussão |

Fontes: [PlantUML: download e distribuições](https://plantuml.com/download), [Mermaid: classes](https://mermaid.js.org/syntax/classDiagram), [Mermaid: casos de uso](https://mermaid.js.org/syntax/usecase.html), [Mermaid: arquitetura](https://mermaid.js.org/syntax/architecture.html), [GitHub: diagramas em Markdown](https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/creating-diagrams), [Miro: bibliotecas e planos](https://help.miro.com/hc/en-us/articles/4403634496402-Miro-for-mapping-diagramming) e [FigJam: ferramenta UML](https://www.figma.com/templates/uml-diagram-tool/).

A escolha é específica para a entrega acadêmica: cobertura dos três tipos UML, fonte editável, histórico de mudanças e imagens independentes de plugin. Não é uma classificação universal de ferramentas.

## 6. Decisão de reprodução

Utilizar **PlantUML 1.2026.8, distribuição Java 8**, obtida da release oficial e conferida por SHA-256. Java já estava disponível. O layout utiliza Graphviz fornecido pela distribuição verificada. Não foi preciso instalar plugin ou criar quadro externo.

Versionar `.puml`, SVG e PNG. Guardar o JAR em `.tools/` local, ignorado pelo Git. O [guia de reprodução](../diagramas/README.md) contém a versão, o checksum e os comandos efetivamente usados. A [linha de comando oficial](https://plantuml.com/command-line) documenta geração e verificação.
