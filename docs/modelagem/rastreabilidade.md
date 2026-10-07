# Matriz de rastreabilidade e revisão

[Sumário](../../SUMARIO.md) · [Requisitos e decisões](../analise-enunciado.md)

## Requisitos → modelos

| Requisito | Caso de uso/precondição | História | Classes principais | Componentes |
| --- | --- | --- | --- | --- |
| RF01 | UC01 | HU01 | `Aluno`, `InstituicaoEnsino`, `Conta` | V01, C01, M02, P01 |
| RF02 | Precondição de UC01 | HU01 | `InstituicaoEnsino` | M02 consulta; P01 conserva dados preparados |
| RF03 | Precondição de UC03/UC09 | HU14 | `Professor`, `Departamento`, `InstituicaoEnsino`, `Usuario` | M01/M02 consultam; P01 conserva dados preparados |
| RF04 | UC09 | HU06 | `CreditoSemestral`, `Semestre`, `Conta` | T01, M01, M03, P01 |
| RF05 | UC06 | HU07 | `Reconhecimento`, `Conta`, `Professor`, `Aluno` | V01, C01, M01, M03, P01 |
| RF06 | UC10 incluído em UC06 | HU08 | `Reconhecimento`, `NotificacaoEmail`, `Aluno` | M03, M04, P01, E01, serviço externo |
| RF07 | UC07 | HU09 | `Professor`, `Conta`, `Reconhecimento`, `CreditoSemestral` | V01, C01, M01, M03, P01 |
| RF08 | UC07 | HU10 | `Aluno`, `Conta`, `Reconhecimento`, `Resgate` | V01, C01, M01, M03, P01 |
| RF09 | UC04 | HU04 | `Vantagem`, `EmpresaParceira` | V01, C01, M01, M02, P01 |
| RF10 | UC02 | HU02 | `EmpresaParceira`, `Usuario` | V01, C01, M02, P01 |
| RF11 | UC05 | HU05 | `Vantagem`, `EmpresaParceira` | V01, C01, M01, M02, P01 |
| RF12 | UC08 | HU11 | `Resgate`, `Conta`, `Vantagem` | V01, C01, M01, M03, P01 |
| RF13 | UC11 incluído em UC08 | HU12 | `Cupom`, `Resgate` | M03, P01 |
| RF14 | UC12 incluído em UC08 | HU12 | `Cupom`, `NotificacaoEmail`, `Aluno` | M04, P01, E01, serviço externo |
| RF15 | UC12 incluído em UC08 | HU13 | `Cupom`, `NotificacaoEmail`, `EmpresaParceira`, `Vantagem` | M04, P01, E01, serviço externo |
| RF16 | UC03 e precondições de UC04–UC09; credenciais em UC01/UC02 | HU01, HU02, HU03, HU14 | `Usuario` e especializações | V01, C01, M01, P01 |

RF02 e RF03 não receberam casos de uso de administração fictícios. Estão cobertos como dados/precondições, critérios das histórias e classes; o processo de preparação é explicitamente externo ao autosserviço.

## Regras → evidências de modelagem

| Regra | Conferência entre artefatos |
| --- | --- |
| RN01 | UC01/UC03 exigem dados preparados; HU01/HU14 verificam vínculo; classes mostram instituição explícita e departamento do professor. |
| RN02 | UC09 e HU06 somam 1.000 ao saldo; `CreditoSemestral` referencia `Semestre`; M03/P01 preservam saldo e unicidade. |
| RN03 | UC06 e HU07 exigem destinatário/motivo/saldo; `Reconhecimento.motivo`; M03 coordena validação e transação. |
| RN04 | UC07, HU09/HU10 e `Conta.consultarExtrato()` distinguem entradas/saídas pelos papéis de origem/destino. |
| RN05 | UC05, HU05 e `Vantagem` exigem custo/descrição/foto e empresa; M02 controla autoria pela sessão. |
| RN06 | UC08, HU11 e `Resgate` conservam débito e custo; relação 1:1 com cupom; M03 confirma em conjunto. |
| RN07 | UC12, HU12/HU13 e `Cupom.codigo` compartilhado; M04 prepara as duas mensagens do mesmo resgate. |
| RN08 | UC03/HU03 e precondições protegidas; `Usuario` inclui login/hash; M01 controla os três perfis. |

## Decisões e questões abertas

D01–D11 estão centralizadas na [análise do enunciado](../analise-enunciado.md). A modelagem adota saldo não negativo e operações atômicas, crédito único por semestre, códigos únicos, mensagens reprocessáveis, dados mínimos de empresa, uma conta por titular e vínculo acadêmico pela instituição. Esses detalhes são identificados como decisões, sem serem apresentados como texto explícito do PDF.

O grupo ainda deve confirmar calendário, pré-cadastro/credenciais, significado de “seus alunos”, campos empresariais e eventual baixa digital. A ausência dessas definições não impede a revisão documental, mas deve orientar a implementação posterior.

## Checklist manual de consistência

- [x] Os 16 requisitos funcionais e as 8 regras expressas aparecem na matriz e nos modelos indicados.
- [x] Nome, email, CPF, RG, endereço, curso e instituição de aluno estão representados.
- [x] Professor tem nome/CPF herdados e departamento/instituição explícitos.
- [x] Professor acumula saldo; não há expiração/reset semestral.
- [x] Reconhecimento possui origem professor, destino aluno e motivo obrigatório.
- [x] Extrato do professor explica envios; o do aluno explica recebimentos e resgates.
- [x] Resgate tem origem aluno, sem destino de moedas para empresa, custo histórico e um cupom.
- [x] Aluno e empresa recebem o mesmo código, preservado ao repetir emails.
- [x] Os três perfis usam credenciais e autorização coerentes com seus objetivos.
- [x] MVC está explícito; View não acessa persistência diretamente.
- [x] Histórias não foram substituídas por tarefas técnicas ou diagramas artificiais.
- [x] Fontes, exportações e links foram conferidos; não há implementação de sprints posteriores.

## Registro de verificação da versão inicial

Conferência em **07/10/2026**: script de renderização executado com PlantUML 1.2026.8/Java 8, verificação de sintaxe sem erros e geração dos três SVG/PNG. Todas as três imagens foram inspecionadas visualmente após o ajuste final do diagrama de componentes. Os campos do PDF, invariantes de moeda, associações e contratos foram revistos contra as histórias e casos de uso.

O verificador documental confirmou **12 documentos Markdown, 85 links locais, 16 requisitos, 8 regras, 14 histórias e 12 casos de uso**, além dos hashes dos três conjuntos fonte/SVG/PNG. Comandos reproduzíveis estão no [guia de diagramas](../diagramas/README.md). Este registro descreve essa versão; após qualquer alteração, executar novamente os checks e revisar o resultado.

## Limite da verificação

Compilar PlantUML demonstra validade sintática e geração de imagens. Conferir links e hashes demonstra integridade dos artefatos. A revisão manual de requisitos/multiplicidades é necessária para avaliar significado. Nenhum desses checks comprova comportamento de uma aplicação, pois a entrega é de modelagem.
