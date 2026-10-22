# Roteiro de apresentação — sprint 1

[Entrega e PDF](README.md) · [Sumário do projeto](../../SUMARIO.md)

Sugestão de duração: **5 a 7 minutos**, ajustável ao tempo disponibilizado em aula. A apresentação de 20 minutos e o tutorial de tecnologias citados no enunciado pertencem à sprint 3.

## 1. Problema e escopo — 40 segundos

“O Sistema de Moeda Estudantil permite que professores reconheçam alunos com moedas virtuais. Os alunos usam essas moedas para resgatar produtos ou descontos de empresas parceiras. Nesta sprint entregamos a modelagem: casos de uso, histórias do usuário, classes e componentes.”

## 2. Casos de uso — 1 minuto

Abrir o diagrama de casos de uso no relatório.

- Mostrar os três perfis humanos: aluno, professor e empresa parceira.
- Explicar que instituições e professores são pré-cadastrados; alunos e empresas fazem seu cadastro e todos usam login e senha.
- Apontar os objetivos centrais: professor envia moedas; aluno consulta extrato, escolhe e resgata vantagem; empresa cadastra ofertas.
- Mostrar as inclusões: envio exige notificação; resgate exige geração do cupom e envio dos emails às duas partes.
- Distinguir o serviço de email externo e o agendador semestral proposto dos usuários humanos.

## 3. Histórias e exemplo — 1 minuto

Abrir HU06, HU07 e HU11. Mostrar que os critérios permitem conferir o comportamento esperado.

“Um professor começa o semestre com 250 moedas restantes. Recebe mais 1.000 e passa a ter 1.250. Ao reconhecer um aluno com 100 moedas e um motivo, fica com 1.150; um aluno que tinha zero passa a ter 100 e recebe email. Se o aluno resgatar uma vantagem de 40 moedas, fica com 60. Aluno e empresa recebem o mesmo código de cupom para a troca presencial.”

Mencionar uma recusa: sem saldo suficiente ou sem motivo, o envio não altera as contas. Falha de email permite reenvio sem repetir o débito ou o crédito.

## 4. Classes — 1 minuto e 20 segundos

Abrir e ampliar o diagrama de classes.

- `Usuario` reúne as credenciais; aluno e professor herdam nome e CPF de `TitularMoedas` e possuem uma conta cada.
- Instituição e departamento representam o vínculo acadêmico do professor; o aluno informa instituição e curso.
- `Movimentacao` tem três especializações: crédito semestral, reconhecimento e resgate.
- Um reconhecimento relaciona conta de origem do professor e conta de destino do aluno. O saldo resulta das entradas menos as saídas.
- A empresa oferece vantagens; cada resgate gera exatamente um cupom. A empresa não recebe as moedas gastas pelo aluno em uma carteira.

## 5. Componentes e MVC — 1 minuto

Abrir e ampliar o diagrama de componentes.

“A View apresenta formulários, catálogo e extratos. Os Controllers recebem as ações e verificam acesso. O Model aplica as regras de cadastro, moedas, resgates e notificações. A persistência grava operações relacionadas em conjunto e o adaptador de email integra o provedor externo.”

Percorrer o envio de moedas: **View → Controller → serviço de moedas → persistência → notificações → email**. Explicar que débito, crédito e histórico são confirmados juntos; o email é enviado depois.

## 6. Decisões e próximos passos — 40 segundos

“Separamos as exigências do enunciado das escolhas propostas. Precisamos confirmar o significado de ‘seus alunos’, as datas dos créditos semestrais e a preparação das credenciais dos professores. Na sprint 2 faremos o modelo ER, a estratégia de acesso aos dados e a versão inicial dos CRUDs de aluno e empresa parceira.”

## Perguntas para preparar

| Pergunta | Resposta apoiada nos modelos |
| --- | --- |
| Por que não há cadastro público de professor? | O enunciado determina que a instituição fornece a lista no início da parceria. |
| As 1.000 moedas substituem o saldo anterior? | Não. São somadas ao saldo restante uma vez por professor e semestre. |
| O que acontece se faltar saldo? | A operação é recusada sem movimentação, cupom ou notificação de sucesso. |
| Por que aluno e empresa recebem o mesmo código? | Para conferir o cupom na troca presencial. |
| A empresa confirma o uso do cupom no sistema? | O enunciado prevê conferência presencial por email; não foi acrescentado um fluxo de baixa digital. |
| O agendador é uma exigência literal? | É a proposta D04 para executar o crédito periódico; a exigência é o crédito semestral acumulável. |
| A aplicação já funciona? | A sprint 1 entrega os modelos. A implementação começa nas etapas seguintes. |
