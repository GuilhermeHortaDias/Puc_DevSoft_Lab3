# Pesquisa de aplicações semelhantes

**Consulta:** 07/10/2026. **Método:** consulta às páginas e centrais de ajuda dos próprios produtos. A comparação trata dos comportamentos publicados; não houve teste de contas, contratação ou acesso aos dados internos dos produtos.

## Comparação com o enunciado

| Aplicação | Evidência pública | Relação com o laboratório | Limite da comparação |
| --- | --- | --- | --- |
| ClassDojo | Professores reconhecem esforços com pontos associados a habilidades e consultam registros com notas e horários. | Ajuda a pensar no motivo do reconhecimento e no histórico do aluno. | Não comprova crédito semestral fixo nem parceria empresarial como no PDF. |
| PBIS Rewards | Alunos consultam pontos e itens de lojas; recursos de resgate permitem adquirir recompensas para uso posterior. | Apoia o encadeamento saldo → catálogo → resgate → uso da recompensa. | Loja escolar não equivale automaticamente a empresas parceiras independentes. |
| LiveSchool | Loja permite compra com pontos, débito da conta e acompanhamento da entrega; cadastro de recompensa inclui preço, foto e descrição. | É uma referência próxima para apresentação da vantagem e separação entre compra e entrega presencial. | Estoque, carrinho, estados de entrega e administração de loja não são requisitos do laboratório. |

Fontes: [ClassDojo Points](https://www.classdojo.com/points/), [PBIS Rewards FAQ](https://www.pbisrewards.com/how-it-works/faqs/), [LiveSchool: Purchasing Rewards](https://help.whyliveschool.com/en/articles/9376573-purchasing-rewards) e [LiveSchool: Setting Up Your School Store](https://help.whyliveschool.com/en/articles/11698728-setting-up-your-school-store).

## Informações úteis para aprofundar os modelos

As conclusões abaixo são **inferências para este projeto**, derivadas da comparação e das regras do PDF, não funcionalidades que se presumem presentes nas três plataformas.

| Informação | Aplicação na entrega atual | Origem e classificação |
| --- | --- | --- |
| Motivo deve aparecer no registro de reconhecimento | `Reconhecimento.motivo` e critérios de envio e extrato | Motivo obrigatório no PDF; a referência ClassDojo reforça sua utilidade |
| Saldo e histórico precisam explicar a movimentação | Registrar data, quantidade e origem/destino; separar recebimento e resgate | Saldo/extrato já exigidos no PDF |
| Catálogo deve informar custo, descrição, foto e empresa | Critérios de consulta e cadastro de vantagem | PDF; cadastro de recompensas do LiveSchool ajuda a detalhar a leitura |
| Emitir comprovante e entregar produto são momentos distintos | Resgate gera cupom; conferência acontece presencialmente | PDF; diferença entre compra e entrega no LiveSchool ajuda a esclarecer o fluxo |
| Débito, histórico e cupom precisam permanecer coerentes | Operação atômica e valor histórico registrado | Decisões D05 e D06, não afirmações sobre implementação dos produtos |
| Repetir um envio de email não deve repetir movimentação | Notificação com referência à operação já concluída | Decisão D07, inferida da necessidade de lidar com falha externa |

## Ideias adicionais avaliadas, sem incorporação

| Ideia | Benefício possível | Motivo para ficar fora agora |
| --- | --- | --- |
| Estoque e indisponibilidade | Evitar resgatar produto esgotado | PDF não define estoque, reserva ou limite |
| Baixa online e cupom de uso único | Ajudar a controlar a entrega | Código para conferência é exigido; processo digital de baixa não é |
| Validade e cancelamento de cupom | Tratar vantagens temporárias | Demandaria novas regras de expiração e estorno |
| Categorias de reconhecimento | Facilitar lançamento recorrente | PDF exige mensagem aberta obrigatória; categorias não a substituem |
| Carrinho com várias vantagens | Permitir múltiplos resgates de uma vez | Objetivo descrito é selecionar uma vantagem por resgate |
| Relatórios agregados e ranking | Acompanhar adesão | Excede extrato individual e pode mudar o objetivo de reconhecimento |
| Integração de matrículas ou SSO | Conferir vínculo e simplificar acesso | Nenhuma integração acadêmica foi especificada |

Não adotar punição, retirada de moedas por comportamento ou reset de saldo por analogia com outros sistemas. O saldo acumulável do professor é uma regra específica deste enunciado e prevalece sobre práticas de produtos pesquisados.

## Resultado da pesquisa para o escopo

A pesquisa complementa a qualidade dos critérios e a clareza dos registros. Os quatro entregáveis continuam vinculados ao enunciado; qualquer ideia da tabela anterior exige discussão posterior e atualização explícita do escopo.
