# Diagrama de componentes e arquitetura MVC

[Sumário](../../SUMARIO.md) · [Fonte PlantUML](../diagramas/componentes.puml) · [SVG](../diagramas/componentes.svg) · [PNG](../diagramas/componentes.png)

![Componentes MVC](../diagramas/componentes.svg)

## Arquitetura proposta

O PDF exige **MVC**. Este diagrama separa apresentação (View), entrada/coordenação de requisições (Controller) e regras/dados do domínio (Model), apoiados por adaptadores de persistência e email. Os componentes são **módulos lógicos**, que podem coexistir numa única aplicação; não há exigência de microserviços, containers ou implantação em nuvem.

Não foram escolhidos linguagem, framework, protocolos definitivos, banco, ORM/DAO ou provedor de email. Os contratos abaixo descrevem responsabilidades para orientar futuras implementações.

## Responsabilidades e rastreamento

| ID | Componente | Responsabilidades | Modelos relacionados |
| --- | --- | --- | --- |
| V01 | Interface web | Formulários públicos e telas por perfil; apresentar saldo, extrato, catálogo e confirmações | UC01–UC08 |
| C01 | Controladores | Receber ação, verificar sessão/perfil com M01, encaminhar para Model, apresentar resultado/erros à View | UC01–UC08 |
| M01 | Autenticação e autorização | Verificar credenciais, identificar usuário/perfil e autorizar acesso; autenticar integração proposta | UC03; precondições de UC04–UC09; `Usuario` |
| M02 | Cadastros e catálogo | Cadastrar aluno/empresa, criar conta inicial de aluno, consultar instituições/vínculos preparados, publicar/consultar vantagens | UC01, UC02, UC04, UC05; identidades e `Vantagem` |
| M03 | Contas, reconhecimento e resgate | Regras de saldo, crédito semestral, transferência justificada, extrato, resgate e emissão de cupom; coordenação atômica | UC06–UC09, UC11; `Conta`, `Movimentacao` e subtipos, `Cupom`, `Semestre` |
| M04 | Notificações | Preparar mensagens de operações confirmadas; acompanhar resultados e reprocessar falhas sem repetir moedas | UC10, UC12; `NotificacaoEmail`, `EstadoEmail` |
| P01 | Persistência | Implementar contrato de leitura/escrita e confirmação atômica de mudanças relacionadas | Todos os registros; estratégia concreta na sprint futura |
| E01 | Adaptador de email | Traduzir mensagem ao provedor, solicitar envio e devolver resultado | UC10, UC12 |
| T01 | Agendador semestral | Integração proposta que se autentica e solicita crédito para um semestre identificado | UC09, decisão D04 |
| Externo | Serviço de email | Receber solicitação de envio de E01 | Ator técnico de UC10, UC12 |

Não há componente de gestão institucional: instituições/departamentos/professores são dados de entrada do processo de parceria. M02 **consulta** esses dados; não implementa importação ou CRUD de professor nesta entrega.

## Interfaces e contratos conceituais

Nos contratos, `contexto` representa identidade/perfil previamente verificados; `dados` são os campos descritos nos casos de uso. Retornos incluem sucesso ou recusa de negócio; ainda não são assinaturas de uma linguagem escolhida.

| Interface | Fornecida por | Consumidores | Operações conceituais |
| --- | --- | --- | --- |
| `IRequisicoes` | C01 | V01 | Receber ação/dados/sessão; devolver resultado e informações para apresentação |
| `IAcesso` | M01 | C01, T01 | `autenticar(login, senha)`, `validarSessao(sessao)`, `autorizar(contexto, acao)`, `autenticarIntegracao(credencialTecnica)` |
| `ICadastrosCatalogo` | M02 | C01 | `cadastrarAluno(dados)`, `cadastrarEmpresa(dados)`, `listarInstituicoes()`, `listarVantagens(contexto)`, `cadastrarVantagem(contextoEmpresa, dados)` |
| `IMoedas` | M03 | C01, T01 | `enviarMoedas(contextoProfessor, alunoId, quantidade, motivo)`, `consultarExtrato(contextoTitular)`, `resgatar(contextoAluno, vantagemId)`, `creditarSemestre(contextoIntegracao, semestre)` |
| `INotificacoes` | M04 | M03 | `prepararMensagens(movimentacaoConfirmadaId)`, `processarPendencias()`; processamento interno segue D07 |
| `IPersistencia` | P01 | M01, M02, M03, M04 | Ler identidades/vínculos/contas/vantagens; gravar registros; executar unidade atômica; controlar unicidade de crédito/cupom/login; conservar estado de mensagem |
| `IEmail` | E01 | M04 | `enviar(notificacao): ResultadoEnvio`; resultado de aceite/falha não significa leitura do email |

Os círculos são interfaces fornecidas, ligados por linha contínua ao fornecedor. Setas tracejadas apontam dos consumidores para os contratos de que dependem. Uma seta não é um fluxo de sequência; a ordem de execução é explicada abaixo.

## Fluxos de responsabilidade

### Autenticação e cadastro

V01 envia ação a C01. Login chama M01; cadastros públicos chamam M02 sem exigir sessão prévia (D01). M02 salva aluno/empresa em P01; aluno e conta inicial devem ser criados juntos. As demais ações humanas passam pela identificação/autorização de M01 antes de chegar ao serviço adequado.

### Envio de moedas

C01 encaminha contexto do professor e dados a M03. M03 valida vínculo, quantidade, motivo e saldo no momento da confirmação, utiliza P01 para registrar reconhecimento e alterações de conta de forma atômica e, após sucesso, solicita mensagens via `INotificacoes`. M04 acompanha a notificação em P01 e envia via E01 ao serviço externo.

Se a transação falhar, não há reconhecimento confirmado nem email de sucesso. Se o email falhar depois da transação, moedas permanecem conforme o reconhecimento e a notificação é reprocessada, preservando sua referência.

### Resgate e cupom

C01 encaminha contexto do aluno e vantagemId a M03. M03 lê vantagem/empresa/custo via P01, verifica saldo e confirma débito, resgate e cupom único numa unidade atômica. M04 obtém a operação confirmada e prepara duas mensagens com o mesmo código, uma para o aluno e outra para a empresa correta.

E01 não gera cupons nem altera contas. A empresa não recebe saldo de moedas. Conferência presencial é atividade externa; não há componente de baixa online ou validação antifraude do cupom implementado.

### Crédito semestral

T01 autentica sua identidade técnica por `IAcesso`, então chama `IMoedas` com o semestre. M03 impede repetição por conta/semestre, preserva saldo restante e utiliza P01 para confirmar cada crédito de 1.000. Datas/calendário e política de ingresso tardio são decisões ainda abertas.

## Regras de dependência e limites

- View usa Controller e não acessa persistência nem provedor de email diretamente.
- Controller controla entrada e sessão; regras de moedas/cupom ficam no Model.
- Model depende de contratos de infraestrutura; implementação de ORM/DAO não foi escolhida.
- Integração semestral não equivale a acesso público sem autenticação.
- Estado de notificação permite tratar falha externa; fila, outbox e política de tentativas são escolhas futuras, sem introduzir novas telas.
- Os modelos de domínio devem acompanhar futuras decisões de código, conforme a avaliação do PDF.

O desenho satisfaz a exigência de modelagem da arquitetura MVC. Não demonstra sistema executável, banco configurado ou envio real de emails.
