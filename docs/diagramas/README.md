# Fontes e reprodução dos diagramas

[Sumário](../../SUMARIO.md) · [Pesquisa de notação e ferramentas](../pesquisa/diagramas-e-ferramentas.md)

| Diagrama | Fonte oficial | Vetor para leitura/ampliação | Raster |
| --- | --- | --- | --- |
| Casos de uso | [casos-de-uso.puml](casos-de-uso.puml) | [SVG](casos-de-uso.svg) | [PNG](casos-de-uso.png) |
| Classes | [classes.puml](classes.puml) | [SVG](classes.svg) | [PNG](classes.png) |
| Componentes | [componentes.puml](componentes.puml) | [SVG](componentes.svg) | [PNG](componentes.png) |

Os arquivos `.puml` são texto UTF-8 e podem ser abertos em qualquer editor. **Edite a fonte, não a imagem exportada.** README, sumário e especificações incorporam SVG; PNG é alternativa para apresentações e ferramentas que não suportem vetor.

## Runtime utilizado

- PlantUML **1.2026.8**, distribuição **Java 8**.
- [JAR da release oficial](https://github.com/plantuml/plantuml/releases/download/v1.2026.8/plantuml-java8-1.2026.8.jar).
- SHA-256: `b5ebc643668e36cb2cbe2c30b9aed8f88ef693840cd9f93eaf18d8660daaae63`.
- Ambiente conferido na geração inicial: Java 1.8.0_491; Graphviz 2.44.1 disponível pela distribuição do PlantUML no Windows.
- O JAR fica em `.tools/`, ignorado pelo Git; não é um plugin nem dependência da futura aplicação.

Referências: [Downloads PlantUML](https://plantuml.com/download) e [linha de comando](https://plantuml.com/command-line). O layout foi verificado no ambiente acima; outra plataforma pode exigir Graphviz configurado. Para um layout diferente, é possível avaliar [Smetana](https://plantuml.com/smetana02), mas não foi o motor usado nestas imagens.

## Gerar e verificar em PowerShell

Com Java no PATH, execute na raiz do repositório:

```powershell
./scripts/renderizar-diagramas.ps1
```

O script baixa somente a versão fixada se ainda não existir, confere SHA-256 **antes de executar**, verifica sintaxe e gera SVG/PNG dos três diagramas. Pode usar uma cópia já obtida da mesma distribuição:

```powershell
./scripts/renderizar-diagramas.ps1 -PlantUmlJar 'C:/ferramentas/plantuml-java8-1.2026.8.jar'
```

O script falha se a sintaxe estiver incorreta ou o JAR não corresponder ao checksum. Ele grava o [manifesto de hashes](manifesto.json) somente após a geração bem-sucedida. Política de execução do PowerShell é configurada pelo próprio usuário/ambiente; o projeto não altera essa política global.

Com Python 3 disponível, confira a documentação:

```powershell
python ./scripts/verificar-documentacao.py
```

O verificador usa apenas a biblioteca padrão: confere links locais, IDs, presença dos modelos, cobertura da matriz, estrutura SVG/PNG e hashes das fontes/exportações. Não executa a aplicação nem valida automaticamente o significado UML. Após a geração, abra todas as imagens e revise legibilidade, relações e regras.

## Alternativa de linha de comando

```powershell
java -jar .tools/plantuml-java8-1.2026.8.jar -charset UTF-8 -checkonly 'docs/diagramas/*.puml'
java -jar .tools/plantuml-java8-1.2026.8.jar -charset UTF-8 -tsvg 'docs/diagramas/*.puml'
java -jar .tools/plantuml-java8-1.2026.8.jar -charset UTF-8 -tpng 'docs/diagramas/*.puml'
```

A alternativa gera imagens, mas não atualiza o manifesto. Use o script principal antes da verificação documental para registrar os hashes correspondentes. Serviços web de renderização são opcionais; a geração desta entrega foi local.

## Histórico e manutenção

Versione juntos fonte, SVG, PNG, manifesto e documentos relacionados. Ao alterar um requisito, confira os casos de uso, histórias, multiplicidades, interfaces e matriz. O Git mantém as versões solicitadas pelo laboratório, sem necessidade de duplicar pastas “final”, “final2” etc.
