# Diagrama de classes de domínio

[Sumário](../../SUMARIO.md) · [Fonte PlantUML](../diagramas/classes.puml) · [SVG](../diagramas/classes.svg) · [PNG](../diagramas/classes.png)

![Classes de domínio](../diagramas/classes.svg)

## Nível do modelo

Este é um modelo de domínio com responsabilidades conceituais, **não um modelo ER**. As classes não escolhem tabelas, chaves físicas, ORM, linguagem ou framework. Controllers e serviços de aplicação pertencem à visão de componentes; não são entidades do negócio.

Tipos `Identificador`, `Texto`, `Inteiro`, `DataHora`, `ReferenciaImagem` e `Lista<T>` são conceituais. CPF, RG e login são textos. `ReferenciaImagem` representa uma foto acessível, sem escolher armazenamento. `/saldo` é derivado das movimentações; uma implementação poderá manter saldo materializado, desde que consistente com o histórico.

## Classes e responsabilidades

| Classe | Responsabilidade e dados |
| --- | --- |
| `Usuario` (abstrata) | Identidade de acesso: id, login único e senhaHash. A autenticação é feita pelo serviço M01; a entidade não guarda senha em texto claro. |
| `TitularMoedas` (abstrata) | Especialização de usuário com nome, CPF e uma conta. Generaliza aluno/professor, sem incluir empresa. |
| `Aluno` | Acrescenta email, RG, endereço e curso; possui vínculo com uma instituição. Nome e CPF são herdados. |
| `Professor` | Nome e CPF herdados; associações obrigatórias com departamento e instituição explícita. Pré-cadastrado. |
| `EmpresaParceira` | Especializa usuário com nome/email mínimos adotados e oferece vantagens. Não herda `TitularMoedas`. |
| `InstituicaoEnsino` | Referência pré-cadastrada para vínculo acadêmico, com id/nome propostos. Não possui login institucional no modelo. |
| `Departamento` | Organiza vínculo do professor dentro de uma instituição; id/nome são dados conceituais adotados. |
| `Conta` | Uma conta por titular; saldo disponível, consulta de histórico, crédito e débito. Operações devem respeitar os invariantes e a coordenação transacional de M03. |
| `Movimentacao` (abstrata) | Registro confirmado com id, data/hora, quantidade e referências opcionais à conta de origem e de destino. Cada subtipo restringe essas referências. |
| `Reconhecimento` | Transferência professor → aluno com motivo obrigatório. Um registro explica a saída e a entrada nos dois extratos. |
| `CreditoSemestral` | Entrada de 1.000 na conta do professor, vinculada a um semestre. |
| `Semestre` | Identifica período por ano/número; não define sozinho calendário ou elegibilidade de ingresso tardio. |
| `Vantagem` | Oferta de uma empresa, com descrição, foto e custo em moedas. |
| `Resgate` | Saída da conta do aluno, referenciando a vantagem e conservando em quantidade o valor realmente debitado. |
| `Cupom` | Comprovante de um resgate confirmado, com código único e data/hora de emissão. Não modela baixa online. |
| `NotificacaoEmail` | Mensagem vinculada à movimentação, destinatário, assunto, conteúdo, estado e número de tentativas. Estado/tentativas são decisões técnicas de suporte à regra de email. |
| `EstadoEmail` | Enumeração `PENDENTE`, `ENVIADA`, `FALHA`; `ENVIADA` indica envio aceito, não leitura pelo destinatário. |

## Associações e multiplicidades

| Relação | Interpretação |
| --- | --- |
| Instituição `1` — `0..*` Alunos | Todo aluno pertence a uma instituição; instituição pode não ter alunos cadastrados. |
| Instituição `1` — `0..*` Professores | Todo professor possui exatamente um vínculo institucional nesta versão. |
| Instituição `1` — `0..*` Departamentos | Departamento pertence a uma instituição; instituição pode não ter departamentos preparados ainda. |
| Departamento `1` — `0..*` Professores | Professor pertence a um departamento; ele deve pertencer à instituição do professor (D11). |
| Titular `1` ◼— `1` Conta | Conta pertence a um único aluno/professor, sem vida de negócio independente do titular. Não determina exclusão em cascata no banco. |
| Conta `0..1` — `0..*` Movimentações, papel origem | Movimento tem no máximo uma conta de origem; conta pode originar vários movimentos. |
| Conta `0..1` — `0..*` Movimentações, papel destino | Movimento tem no máximo uma conta de destino; conta pode receber vários movimentos. |
| Semestre `1` — `0..*` Créditos | Todo crédito semestral referencia um semestre. |
| Empresa `1` — `0..*` Vantagens | Toda vantagem é de uma empresa; empresa pode iniciar sem ofertas. |
| Vantagem `1` — `0..*` Resgates | Todo resgate seleciona uma vantagem; a vantagem pode nunca ser resgatada. |
| Resgate `1` ◼— `1` Cupom | Cada resgate confirmado gera exatamente um cupom pertencente a ele. |
| Movimentação `1` — `0..*` Notificações | Notificação referencia uma operação; o número efetivo depende do subtipo e do estágio de processamento. |

O losango de composição está no lado do todo. A composição não autoriza apagar movimentações nem substitui a discussão de exclusão nas próximas sprints.

## Invariantes de domínio

| Elemento | Restrições |
| --- | --- |
| Conta | Saldo nunca negativo; empresa não possui conta; conta de aluno inicia em zero (D02/D10). |
| Movimentação | Quantidade inteira positiva; ao menos origem ou destino existe; registros confirmados preservam o histórico. |
| Reconhecimento | Origem obrigatória de professor, destino obrigatório de aluno, motivo com conteúdo após remover espaços; quantidade igual debitada e creditada; saldo suficiente; vínculo D09. |
| Crédito semestral | Origem ausente, destino obrigatório de professor; quantidade = 1.000; um crédito por conta/semestre (D04). |
| Resgate | Origem obrigatória de aluno, destino ausente; quantidade = custo no momento da confirmação; saldo suficiente; valor histórico permanece mesmo se catálogo mudar (D06). |
| Vantagem | Empresa obrigatória, descrição e foto presentes, custo inteiro positivo. |
| Cupom | Código único; um cupom por resgate confirmado. Sem expiração/uso online presumidos. |
| Vínculo do professor | `professor.instituicao = professor.departamento.instituicao`. |
| Notificações | Reconhecimento demanda uma mensagem ao aluno; resgate demanda uma ao aluno e outra à empresa da vantagem com o mesmo código; crédito semestral não exige email no PDF. |

`0..*` em notificações permite representar processamento pendente, sem afirmar que zero emails é resultado final aceitável. O serviço M04 deve concluir as mensagens obrigatórias e repetir tentativas da mesma notificação sem criar nova movimentação.

## Como o saldo e o extrato se relacionam

```text
saldo(conta) = soma das quantidades cujo destino é a conta
             - soma das quantidades cuja origem é a conta
```

Exemplo: professor recebe crédito de 1.000 e envia 100 → saldo 900. Aluno recebe os mesmos 100 e resgata uma vantagem de 40 → saldo 60. O reconhecimento é um registro compartilhado pelos dois extratos, não dois envios diferentes. O resgate não possui destino empresarial de moedas.

O crédito semestral também pode aparecer no extrato do professor para explicar seu saldo; os envios exigidos pelo PDF continuam presentes. `consultarExtrato()` é leitura filtrada por titular, sob autorização.

## Coordenação com os componentes

M03 coordena débito, crédito, registro e cupom com P01 em transação; métodos da entidade `Conta` não autorizam alterações isoladas por qualquer ator. M02 gerencia identidades/catalogação; M01 verifica credenciais/perfil; M04 monta e envia notificações por E01. O modelo não define como P01 será implementado na Lab03S02.

As principais decisões revisáveis são D02–D11 na [análise](../analise-enunciado.md), especialmente vínculo aluno–professor, calendário e dados mínimos de empresa.
