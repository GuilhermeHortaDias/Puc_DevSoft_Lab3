# Requisitos e decisões da modelagem

[Sumário](../../SUMARIO.md)

Este documento reúne as regras que orientam os quatro entregáveis. **RF** identifica exigências extraídas do enunciado; **D** identifica escolhas propostas para completar detalhes que o enunciado não define. **UC** e **HU** apontam para os casos de uso e as histórias correspondentes.

## Requisitos do enunciado

| ID | Exigência | Caso de uso | História |
| --- | --- | --- | --- |
| RF01 | Cadastrar aluno com nome, email, CPF, RG, endereço, instituição e curso. | UC01 | HU01 |
| RF02 | Aluno seleciona uma instituição já pré-cadastrada. | Precondição de UC01 | HU01 |
| RF03 | Professor é pré-cadastrado com nome, CPF, departamento e vínculo explícito à instituição. | Precondição de UC03 e UC09 | HU14 |
| RF04 | Professor recebe 1.000 moedas por semestre, somadas ao saldo restante. | UC09 | HU06 |
| RF05 | Professor envia moedas a um aluno com saldo suficiente, montante e motivo aberto obrigatório. | UC06 | HU07 |
| RF06 | Aluno recebe email ao ganhar moedas. | UC10 | HU08 |
| RF07 | Professor consulta saldo e extrato dos envios. | UC07 | HU09 |
| RF08 | Aluno consulta saldo e extrato de recebimentos e trocas. | UC07 | HU10 |
| RF09 | Aluno consulta e seleciona vantagens cadastradas. | UC04 | HU04 |
| RF10 | Empresa interessada na parceria realiza cadastro. | UC02 | HU02 |
| RF11 | Empresa cadastra vantagem com custo em moedas, descrição e foto. | UC05 | HU05 |
| RF12 | Resgate desconta do aluno o custo da vantagem. | UC08 | HU11 |
| RF13 | Sistema gera código do cupom para troca presencial. | UC11 | HU12 |
| RF14 | Aluno recebe email do cupom com o código. | UC12 | HU12 |
| RF15 | Empresa recebe email para conferir a troca, com o mesmo código enviado ao aluno. | UC12 | HU13 |
| RF16 | Aluno, professor e empresa possuem login/senha e acesso autenticado às funções. | UC03 e funções protegidas | HU01, HU02, HU03, HU14 |

A arquitetura **MVC** é exigida pelo enunciado e está representada no [diagrama de componentes](componentes.md).

## Decisões adotadas nos modelos

Estas decisões fazem parte da proposta atual e podem ser revisadas pelo grupo. Elas não devem ser confundidas com exigências literais do PDF.

| ID | Escolha adotada |
| --- | --- |
| D01 | Cadastro inicial e login são públicos; demais ações humanas exigem sessão e perfil autorizado. |
| D02 | Quantidade/custo são inteiros positivos; aluno inicia com zero moedas; envio e resgate não podem deixar saldo negativo. |
| D03 | Empresa possui nome e email como dados mínimos; login é único e senha é armazenada como hash. |
| D04 | Agendador autenticado dispara o crédito semestral; cada professor recebe no máximo um crédito por semestre. |
| D05 | Envio confirma débito, crédito e registro juntos; resgate confirma débito, registro e cupom juntos. |
| D06 | Código de cupom é único; o resgate conserva o valor debitado, mesmo se o custo da vantagem mudar depois. |
| D07 | Emails são enviados após a confirmação da movimentação; repetir uma notificação não movimenta moedas novamente. |
| D08 | Empresa pode concluir cadastro sem vantagem inicial e cadastrar suas ofertas após autenticar-se. |
| D09 | Nesta versão, o vínculo aluno–professor é aproximado pela mesma instituição. O significado de “seus alunos” precisa ser confirmado. |
| D10 | Aluno e professor possuem uma conta cada; o resgate não cria crédito de moedas para a empresa. |
| D11 | Departamento e professor pertencem à mesma instituição. |

## Definições a confirmar antes da implementação

- Como serão preparados os cadastros de instituições, departamentos e professores, incluindo suas credenciais?
- Quais datas disparam o crédito semestral e como tratar professor que ingressa no meio do semestre?
- “Seus alunos” exige vínculo de turma/disciplina ou apenas de instituição?
- A empresa precisa de outros dados além de nome, email e credenciais?

A conferência do cupom permanece presencial, por email, conforme o enunciado. Linguagem, framework e estratégia de persistência serão definidos na etapa de implementação.
