# Análise integral do enunciado

## Fonte, finalidade e interpretação

Fonte principal: `LABORATORIO_3_LAB_DESENVOLVIMENTO_DE_SOFTWARE.pdf`, fornecido pelo grupo, **duas páginas**, lidas integralmente e conferidas visualmente em 07/10/2026. O arquivo original foi preservado fora do repositório; não é necessário redistribuí-lo para consultar esta análise.

O documento identifica a PUC Minas, Engenharia de Software, disciplina Projeto de Software, professora Milena Menezes Adão, laboratório do segundo semestre de 2026, valendo 20 pontos. Seu objetivo é reconhecer mérito estudantil por moedas virtuais distribuídas por professores e trocadas por vantagens de empresas parceiras.

As exigências do PDF descrevem o laboratório completo. A solicitação atual do grupo limita a ação à **Lab03S01** e à documentação de apoio. Os modelos representam os comportamentos do sistema completo descrito, mas não implementam funcionalidades de nenhuma sprint.

## Leitura de cada seção do PDF

| Seção | Conteúdo e consequência |
| --- | --- |
| 1. Sistema de Moeda Estudantil, p. 1 | Reconhecimento de mérito; professor distribui, aluno resgata, empresa oferece vantagens. Não define moeda financeira ou criptomoeda. |
| 2. Descrição do Sistema, p. 1 | Cadastros, vínculos institucionais, crédito semestral acumulável, envio justificado, emails, extratos, catálogo, resgate e autenticação. Detalhados abaixo. |
| 3. Apresentação Final, pp. 1–2 | Protótipo e tutorial das tecnologias ao fim da Sprint 03; apresentação de 20 minutos; arquitetura MVC; GitHub atualizado com versões dos modelos e código final. Avaliação considera adequação aos requisitos, alinhamento entre modelos e código e atualização dos modelos. |
| 4.1. Lab03S01, p. 2 | Casos de uso, histórias do usuário, classes e componentes. É a entrega atual. |
| 4.2. Lab03S02, p. 2 | ER, estratégia de acesso ao banco (ORM, DAO etc.) e versão inicial dos CRUDs de aluno e empresa, com frontend e backend. Não executada aqui. |
| 4.3. Lab03S03, p. 2 | CRUDs finais, apresentação de arquitetura e persistência e preparação de slides. Não executada aqui. |
| Atenção, p. 2 | Apresentar o andamento semanalmente em aula; ausência do grupo implica perda automática de 50% dos pontos da sprint. Não há datas exatas de calendário no documento. |

## Perfis, dados e fronteira

| Elemento | Dados explicitamente pedidos | Papel |
| --- | --- | --- |
| Aluno | Nome, email, CPF, RG, endereço, instituição e curso; login e senha | Autocadastro, recebimento, consulta e resgate |
| Professor | Nome, CPF, departamento, vínculo explícito à instituição; login e senha | Pré-cadastrado, recebe crédito semestral e premia alunos |
| Empresa parceira | Cadastro e credenciais; campos identificadores não detalhados | Oferece vantagens e recebe email para conferir o cupom |
| Instituição de ensino | Existência pré-cadastrada; outros campos não detalhados | Referência do vínculo de alunos e professores |
| Vantagem | Custo em moedas, descrição e foto | Produto ou desconto selecionado pelo aluno |
| Cupom | Código gerado pelo sistema e envio por email às duas partes | Comprovante para conferência e troca presencial |

Instituição e departamento são entidades do domínio, **não perfis de acesso exigidos**. O serviço de email é um ator técnico externo. Um agendador semestral é a escolha proposta para disparar a regra temporal; não representa usuário humano nem novo painel.

## Requisitos funcionais expressos

Os identificadores são locais à documentação, não códigos presentes no PDF.

| ID | Requisito | Local do PDF |
| --- | --- | --- |
| RF01 | Cadastrar aluno com todos os dados solicitados, escolhendo instituição já cadastrada. | §2, parágrafo 1 |
| RF02 | Considerar instituições participantes já pré-cadastradas. | §2, parágrafo 1 |
| RF03 | Considerar professores pré-cadastrados pela lista enviada na parceria; manter nome, CPF, departamento e instituição explícita. | §2, parágrafo 1 |
| RF04 | Creditar 1.000 moedas ao professor por semestre, acumulando o saldo anterior. | §2, parágrafo 1 |
| RF05 | Permitir envio de moedas pelo professor a um aluno, informando montante e motivo obrigatório e exigindo saldo suficiente. | §2, parágrafo 1 |
| RF06 | Notificar o aluno por email após recebimento de moedas. | §2, parágrafo 2 |
| RF07 | Permitir ao professor consultar saldo e extrato dos envios realizados. | §2, parágrafo 2 |
| RF08 | Permitir ao aluno consultar saldo e extrato dos recebimentos e trocas. | §2, parágrafo 2 |
| RF09 | Disponibilizar vantagens cadastradas para seleção pelo aluno. | §2, parágrafo 2 |
| RF10 | Permitir cadastro de empresas interessadas na parceria. | §2, parágrafo 2 |
| RF11 | Permitir à empresa cadastrar vantagens, com custo em moedas, descrição e foto. | §2, parágrafo 2 |
| RF12 | Resgatar vantagem e descontar seu custo do saldo do aluno. | §2, parágrafo 3 |
| RF13 | Gerar código do cupom para a troca presencial. | §2, parágrafo 3 |
| RF14 | Enviar email de cupom ao aluno, incluindo o código. | §2, parágrafo 3 |
| RF15 | Enviar email à empresa, com o mesmo código, para conferir a troca. | §2, parágrafo 3 |
| RF16 | Manter login e senha de aluno, professor e empresa e autenticar o acesso às funcionalidades. | §2, parágrafo 4 |

## Regras expressas

| ID | Regra |
| --- | --- |
| RN01 | Instituições e professores existem antes dos respectivos fluxos operacionais. Professor pertence explicitamente a uma instituição e a um departamento. |
| RN02 | Crédito do professor = saldo restante + 1.000 a cada semestre; o saldo não é zerado pela virada. |
| RN03 | Professor não envia mais moedas que seu saldo; destinatário e motivo aberto são obrigatórios. |
| RN04 | Extrato de professor mostra envios; extrato de aluno mostra recebimentos e resgates, além do saldo. |
| RN05 | Vantagem pertence a uma empresa e exige custo, descrição e foto. |
| RN06 | Resgate desconta o custo da vantagem do aluno e gera cupom. |
| RN07 | Email do aluno e email da empresa contêm o mesmo código, usado para conferência presencial. |
| RN08 | Os três perfis acessam funções por autenticação com login e senha. |

## Decisões propostas para fechar lacunas

Estas decisões são **inferências de modelagem**, não novas exigências atribuídas à professora. Foram adotadas para produzir uma versão coerente e revisável; podem ser alteradas pelo grupo.

| ID | Decisão adotada | Motivo e limite |
| --- | --- | --- |
| D01 | Autocadastro e login são públicos; operações de negócio exigem sessão e perfil corretos. | Leitura literal de autenticação para absolutamente tudo inviabilizaria o primeiro cadastro. Dados pré-cadastrados vêm do processo de parceria, sem CRUD administrativo nesta entrega. |
| D02 | Quantidades e custos são inteiros positivos; saldo inicial do aluno é zero e resgate exige saldo suficiente. | Não há empréstimo nem moedas fracionadas definidos. São validações propostas para a coerência das trocas. |
| D03 | Nome e email da empresa são dados mínimos adotados; login é único, independente de ser email; senha é representada por hash. | A empresa precisa ser identificada e receber o cupom. CNPJ, endereço e telefone empresarial não são obrigatórios nesta versão. |
| D04 | Um agendador autenticado como integração dispara crédito; cada par professor/semestre recebe no máximo um crédito de 1.000. | Evita duplicação por reexecução. Datas, calendário e entrada de professor no meio do semestre continuam abertos. |
| D05 | Operações de saldo são atômicas; envio debita professor e credita aluno juntos. Resgate debita e cria registro/cupom juntos. | Evita saldo negativo e registros parciais em acessos simultâneos. Não escolhe banco, ORM ou mecanismo de lock. |
| D06 | Cupom tem código único; valor debitado é registrado na movimentação e não muda se o catálogo mudar. | Mantém correspondência entre saldo, histórico e comprovante. Não define expiração, estorno ou baixa online do cupom. |
| D07 | Email é preparado após confirmação da operação; falha de envio pode ser reprocessada sem movimentar moedas novamente. | A falha externa não deve duplicar débito/crédito. Representação de notificações é uma decisão de arquitetura. |
| D08 | Cadastro da empresa e cadastro da vantagem são objetivos distintos; uma empresa pode iniciar sem vantagem. | O PDF não determina cadastro conjunto obrigatório. Vantagens só são publicadas por empresa autenticada. |
| D09 | Relação aluno–professor é tratada pela instituição comum nesta versão; sem modelo de turmas/matrículas. | “Seus alunos” não define como conferir vínculo. Restrição à mesma instituição é uma aproximação adotada, insuficiente para comprovar matrícula em disciplina. |
| D10 | Professor e aluno possuem uma conta cada; empresa não recebe as moedas gastas. | O texto só define contas e extratos de alunos/professores e débito no resgate; não define crédito em carteira empresarial. |
| D11 | Professor.depto pertence à mesma instituição explicitamente associada ao professor. | Preserva o vínculo institucional sem criar gestão de departamentos como nova funcionalidade. |

## Pontos a confirmar antes da implementação

1. Quem prepara instituições, departamentos, professores e suas credenciais? A lista é importada ou inserida manualmente? Não há ator administrador especificado.
2. Quais são as datas do semestre e a regra para professor que entra no meio do período? Não presumir crédito retroativo ou proporcional.
3. “Seus alunos” significa mesma instituição, mesma turma ou matrícula na disciplina? D09 é uma hipótese revisável.
4. Quais campos e validações adicionais são necessários à empresa? CPF/RG e endereço de aluno já são pedidos; CNPJ da empresa não está no texto.
5. A conferência do código é apenas manual, por email, ou deverá existir consulta e confirmação dentro do sistema? A versão atual é manual.
6. Há limite de estoque, validade de vantagens/cupons, cancelamento ou estorno? Nada disso está determinado.
7. Quais regras aplicar a atualização e exclusão de aluno/empresa nas sprints futuras, preservando movimentações? CRUDs posteriores não autorizam excluir histórico agora.

## Fora do escopo da ação atual

Não produzir ER, banco, DAO/ORM implementado, frontend, backend, CRUD executável, slides, tutorial de tecnologias ou protótipo. Não introduzir ranking público, penalização de pontos, gamificação adicional, carteira de empresa, transferência aluno–aluno, dinheiro real, blockchain, pagamento online, aplicativo móvel ou administração institucional.

Nomear uma interface de persistência no diagrama de componentes representa uma responsabilidade arquitetural exigida pela modelagem; **não é implementar a estratégia de acesso ao banco da Lab03S02**.
