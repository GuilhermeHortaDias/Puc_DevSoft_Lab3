# Histórias do usuário e critérios de aceitação

[Sumário](../../SUMARIO.md) · [Casos de uso](casos-de-uso.md) · [Rastreabilidade](rastreabilidade.md)

As histórias descrevem o produto modelado, **não funcionalidades implementadas**. Prioridade “essencial” refere-se ao requisito do sistema, não ao compromisso de implementar tudo na sprint de modelagem. D01–D11 são decisões propostas na [análise](../analise-enunciado.md).

## HU01 — Ingressar como aluno

**Como aluno, quero me cadastrar na instituição participante, para receber reconhecimentos e trocar moedas por vantagens.**

**Essencial. Requisitos:** RF01, RF02, RF16. **Caso:** UC01. **Dependência:** lista de instituições pré-cadastrada.

- Dado formulário completo, quando informar nome, email, CPF, RG, endereço, curso, instituição existente, login e senha, então criar aluno, vínculo e conta com saldo zero.
- Dado um campo obrigatório ausente, quando enviar o formulário, então informar a pendência e não criar cadastro parcial.
- Dado instituição inexistente ou login em uso, quando cadastrar, então rejeitar sem substituir outro usuário.
- Dado usuário ainda sem sessão, quando acessar o cadastro inicial, então permitir o formulário público (D01); operações de moedas continuam protegidas.

## HU02 — Ingressar como empresa parceira

**Como representante de empresa, quero cadastrar a parceria, para oferecer vantagens aos alunos.**

**Essencial. Requisitos:** RF10, RF16. **Caso:** UC02. **Decisões:** D03, D08.

- Dado nome, email, login e senha válidos, quando confirmar cadastro, então criar empresa com credenciais próprias.
- Dado ausência de email/nome ou login já utilizado, quando tentar cadastrar, então não criar empresa e informar correção necessária.
- Dado cadastro concluído, quando consultar o resultado, então não exigir vantagem inicial nem criar conta de moedas para a empresa.

## HU03 — Acessar as funções do meu perfil

**Como aluno, professor ou representante de empresa, quero autenticar meu acesso, para utilizar as funções correspondentes ao meu perfil.**

**Essencial. Requisito:** RF16. **Caso:** UC03. **Dependência:** cadastro do perfil.

- Dado qualquer um dos três perfis com credenciais válidas, quando efetuar login, então estabelecer sessão e disponibilizar suas funções.
- Dado login inexistente ou senha incorreta, quando autenticar, então negar sessão sem expor credenciais.
- Dado ausência de sessão, quando tentar envio, extrato, catálogo protegido, cadastro de vantagem ou resgate, então exigir autenticação.
- Dado sessão de aluno/empresa, quando tentar enviar moedas como professor, então negar e manter contas inalteradas; aplicar a mesma separação às demais funções exclusivas.

## HU04 — Conhecer as vantagens disponíveis

**Como aluno, quero consultar o catálogo de vantagens, para decidir como usar minhas moedas.**

**Essencial. Requisito:** RF09. **Caso:** UC04. **Dependências:** HU03; existência de vantagens para catálogo não vazio.

- Dado aluno autenticado e vantagens cadastradas, quando abrir o catálogo, então mostrar descrição, foto, custo e empresa responsável.
- Dado catálogo vazio, quando consultá-lo, então informar ausência de vantagens sem criar saldo ou resgate.
- Dado seleção de vantagem, quando somente consultar seus detalhes, então não debitar moedas nem emitir cupom.

## HU05 — Oferecer uma vantagem

**Como representante de empresa, quero cadastrar uma vantagem com descrição, foto e custo em moedas, para atrair alunos reconhecidos pelo sistema.**

**Essencial. Requisito:** RF11. **Caso:** UC05. **Dependências:** HU02, HU03.

- Dado empresa autenticada e campos completos, quando cadastrar custo inteiro positivo, descrição e foto, então publicar vantagem vinculada à empresa da sessão.
- Dado custo zero, negativo ou fracionário (D02), descrição em branco ou foto ausente, quando cadastrar, então rejeitar a publicação.
- Dado tentativa de publicar em nome de outra empresa, quando processar a solicitação, então negar e não modificar seu catálogo.

## HU06 — Receber saldo semestral acumulável

**Como professor, quero receber 1.000 moedas por semestre sem perder as que restaram, para continuar reconhecendo meus alunos.**

**Essencial. Requisito:** RF04. **Caso:** UC09. **Dependência:** professor pré-cadastrado. **Decisão:** D04.

- Dado professor com saldo 250 e novo semestre elegível, quando executar o crédito, então o saldo passa a 1.250 e um crédito de 1.000 é registrado.
- Dado professor com saldo zero, quando receber o crédito do semestre, então seu saldo passa a 1.000.
- Dado crédito já registrado para professor/semestre, quando repetir o processamento, então não acrescentar outras 1.000 moedas.
- Dado aluno ou empresa, quando executar o crédito semestral, então não conceder o crédito desse fluxo a esses perfis.

## HU07 — Reconhecer um aluno

**Como professor, quero enviar moedas a um aluno com uma justificativa, para reconhecer sua contribuição.**

**Essencial. Requisito:** RF05. **Caso:** UC06. **Dependências:** HU03 e saldo disponível; aluno cadastrado. **Decisões:** D02, D05, D09.

- Dado professor com 1.000 e aluno com 20 moedas, quando enviar 100 com motivo válido, então os saldos passam a 900 e 120, e um reconhecimento relaciona as duas contas.
- Dado professor com saldo 80, quando tentar enviar 81, então rejeitar sem alterar as contas nem notificar sucesso.
- Dado saldo exatamente igual à quantidade enviada, quando confirmar com dados válidos, então permitir envio e deixar o professor com saldo zero.
- Dado motivo vazio/apenas espaços, aluno inexistente, vínculo incompatível com D09 ou quantidade não positiva, quando tentar enviar, então rejeitar sem movimentação.
- Dado dois envios concorrentes de 80 a partir de saldo 100, quando confirmar as operações, então no máximo um é efetivado e o saldo nunca fica negativo.

## HU08 — Saber que recebi reconhecimento

**Como aluno, quero receber um email ao ganhar moedas, para conhecer o reconhecimento do professor.**

**Essencial. Requisito:** RF06. **Caso:** UC10, incluído em UC06. **Dependência:** HU07 confirmado.

- Dado reconhecimento confirmado, quando processar a notificação, então enviar ao email cadastrado do aluno a quantidade, professor e motivo propostos.
- Dado envio recusado por regra de negócio, quando concluir a tentativa, então não enviar email de recebimento.
- Dado falha do serviço de email, quando reprocessar a mesma notificação, então manter o reconhecimento original e não creditar moedas novamente.

## HU09 — Acompanhar meus envios como professor

**Como professor, quero consultar meu saldo e extrato, para planejar novas premiações e revisar as realizadas.**

**Essencial. Requisito:** RF07. **Caso:** UC07. **Dependência:** HU03.

- Dado professor autenticado, quando consultar, então mostrar seu saldo e os envios, com data, quantidade, aluno e motivo.
- Dado créditos semestrais registrados, quando apresentar o extrato, então identificá-los como entradas para explicar o saldo acumulado, conforme decisão de representação.
- Dado conta sem envios, quando consultar, então apresentar saldo e ausência de envios; consultar não altera a conta.
- Dado tentativa de acessar conta de outro titular, quando consultar, então negar sem expor seu extrato.

## HU10 — Acompanhar recebimentos e trocas como aluno

**Como aluno, quero consultar meu saldo e extrato, para entender as moedas recebidas e utilizadas.**

**Essencial. Requisito:** RF08. **Caso:** UC07. **Dependência:** HU03.

- Dado saldo inicial zero, recebimento de 100 e resgate de 40, quando consultar, então mostrar saldo 60, entrada de 100 e saída de 40.
- Dado recebimento, quando consultar seu registro, então identificar professor, quantidade, motivo e data; dado resgate, identificar vantagem, custo histórico e código do cupom.
- Dado aluno sem movimentações, quando consultar, então mostrar saldo zero e extrato vazio.
- Dado pedido por conta alheia ou de empresa, quando solicitar extrato, então negar o acesso.

## HU11 — Trocar moedas por uma vantagem

**Como aluno, quero resgatar uma vantagem pagando com minhas moedas, para usufruir do produto ou desconto oferecido.**

**Essencial. Requisito:** RF12. **Caso:** UC08. **Dependências:** HU03, HU04 e saldo suficiente. **Decisões:** D02, D05, D06.

- Dado saldo 120 e vantagem de custo 50, quando confirmar resgate, então o saldo passa a 70 e são registrados um resgate de 50 e um cupom.
- Dado saldo exatamente igual ao custo, quando resgatar, então permitir e deixar saldo zero.
- Dado saldo insuficiente ou vantagem inexistente, quando tentar resgatar, então não debitar, emitir cupom ou enviar confirmação de sucesso.
- Dado falha antes da confirmação de saldo, registro ou cupom, quando encerrar a tentativa, então nenhuma dessas partes fica confirmada isoladamente.
- Dado dois resgates concorrentes de 80 com saldo 100, quando processar, então no máximo um é confirmado e nenhum saldo negativo ocorre.
- Dado alteração posterior do custo do catálogo, quando consultar o resgate já realizado, então preservar o custo original debitado.

## HU12 — Receber meu cupom para troca presencial

**Como aluno, quero receber por email um cupom com código, para apresentá-lo na troca presencial.**

**Essencial. Requisitos:** RF13, RF14. **Casos:** UC11, UC12. **Dependência:** HU11.

- Dado resgate confirmado, quando emitir cupom, então gerar um código único ligado a esse resgate.
- Dado cupom emitido, quando enviar a mensagem, então o email do aluno inclui o código e a vantagem correspondente.
- Dado dois resgates distintos, quando emitir seus cupons, então os códigos são diferentes.
- Dado falha de email após resgate, quando reenviar, então utilizar o cupom original sem novo débito ou nova emissão.

## HU13 — Conferir o cupom do aluno

**Como representante de empresa, quero receber por email o código do cupom resgatado, para conferir o comprovante na troca presencial.**

**Essencial. Requisito:** RF15. **Caso:** UC12. **Dependência:** HU11 e empresa com email cadastrado.

- Dado resgate de vantagem da empresa, quando enviar a mensagem, então usar o email dessa empresa e o mesmo código enviado ao aluno.
- Dado vantagem pertencente a outra empresa, quando notificar, então não encaminhar seu cupom à empresa errada.
- Dado email do aluno enviado e da empresa com falha, quando reprocessar, então tratar a mensagem pendente da empresa sem debitar aluno novamente.
- Dado conferência presencial, quando comparar os emails, então código e vantagem correspondem; o modelo não exige confirmação digital de utilização.

## HU14 — Usar meu cadastro institucional como professor

**Como professor de uma instituição participante, quero utilizar meu cadastro institucional já preparado, para acessar o sistema com meu vínculo identificado.**

**Essencial. Requisito:** RF03. **Caso:** UC03, com pré-cadastro como precondição. **Dependência:** processo de parceria externo ao autosserviço.

- Dado professor da lista institucional, quando conferir seu registro pré-cadastrado, então nome, CPF, departamento, instituição e credenciais estão presentes.
- Dado vínculo institucional e departamento, quando validar o registro, então o departamento pertence à mesma instituição do professor (D11).
- Dado professor pré-cadastrado com credenciais válidas, quando autenticar, então usar seu cadastro existente sem novo cadastro público.

Esta história documenta o valor e os critérios dos dados iniciais; não cria tela de importação, login institucional de terceiros ou papel administrador.
