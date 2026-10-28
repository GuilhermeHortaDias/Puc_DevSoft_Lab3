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
| D04 | Bean interno dispara crédito na inicialização e em janeiro/julho; semestre calculado em America/Sao_Paulo. Professor ingressante recebe o semestre corrente; cada conta recebe no máximo um crédito por ano/semestre. Não há endpoint público ou administrador. |
| D05 | Envio confirma débito, crédito, movimentação e notificação juntos; resgate confirma débito, movimentação com cupom e duas notificações juntos. Contas usam bloqueio pessimista em ordem de UUID; comando é único por solicitante/requisicaoId. |
| D06 | Código de cupom é único e fica na movimentação de resgate, junto ao custo debitado e à descrição histórica. Não há entidade separada de cupom. |
| D07 | Intenção de email é persistida na mesma transação da movimentação. Worker usa JavaMailSender após commit e repete apenas o envio, preservando moedas e cupom; Mailpit local e SMTP externo por ambiente. |
| D08 | Empresa pode concluir cadastro sem vantagem inicial e cadastrar suas ofertas após autenticar-se. |
| D09 | Nesta versão, o vínculo aluno–professor é aproximado pela mesma instituição. O significado de “seus alunos” precisa ser confirmado. |
| D10 | Aluno e professor possuem uma conta cada; o resgate não cria crédito de moedas para a empresa. |
| D11 | Departamento e professor pertencem à mesma instituição, com vínculo explícito e FK composta. |
| D12 | Pré-cadastro institucional usa JSON externo configurado em INSTITUCIONAL_ARQUIVO, com instituições, departamentos, professores e credenciais; template local contém dados fictícios e preserva a base PUC Minas existente. |
| D13 | Usuario concreto se associa aos perfis por PK compartilhada (@MapsId); saldo long é armazenado na conta com versão; uma Movimentacao concreta reúne tipos e metadados, sem herança/subtipos ou entidade Semestre. |
| D14 | Foto da vantagem é binária PNG/JPEG válido, até 2 MiB e 8 megapixels, armazenada no PostgreSQL com seu tipo. |
| D15 | Cada usuário mantém seu cadastro; excluir inativa e conserva histórico. BCrypt, sessão e CSRF protegem acesso; APIs usam DTOs e validação. |

## Definições sujeitas à confirmação do grupo

- “Seus alunos” exige vínculo de turma/disciplina ou apenas de instituição? A implementação adota D09 enquanto essa expressão não for esclarecida.
- A empresa precisa de outros dados além de nome, email e credenciais? A implementação conserva o cadastro mínimo existente.
- O grupo precisa fornecer o arquivo institucional de operação e confirmar as datas de janeiro/julho adotadas em D04. O mecanismo e o tratamento do ingresso tardio já estão definidos como decisões, sem atribuí-los ao PDF.

A conferência do cupom permanece presencial, por email, conforme o enunciado. A aplicação usa Java 21/Spring Boot, React/TypeScript, PostgreSQL e JPA/Hibernate com Spring Data JPA. As escolhas de esquema, transações e segurança estão na [modelagem ER e estratégia de persistência](persistencia.md). O PDF/HTML da sprint 1 preserva a entrega histórica; estes documentos acompanham a implementação atual.
