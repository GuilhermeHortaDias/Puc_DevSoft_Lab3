# Desenvolvimento do projeto

- Consulte `docs/modelagem/requisitos.md` e `SUMARIO.md` antes de mudar regras de negócio. Diferencie exigências do laboratório de decisões do grupo.
- Escopo atual: concluir os requisitos do laboratório, incluindo professor, moedas, vantagens, cupons e emails, preservando os cadastros existentes.
- Preserve MVC: React apresenta; controllers tratam HTTP; services validam autorização e regras em transações; repositories acessam o banco via JPA.
- Use DTOs nas APIs, Bean Validation, BCrypt e sessão com CSRF. Nunca publique senhas ou exponha entidades JPA diretamente.
- Migrações Flyway são a fonte do esquema implementado; Hibernate apenas valida. Não altere migrações já aplicadas: acrescente outra.
- Trabalhe em incrementos pequenos. Nesta etapa, por solicitação do usuário, faça apenas build e verificações básicas necessárias; as suítes completas ficam para a etapa posterior de testes.
- Comandos de ambiente e verificação ficam no README. Use PostgreSQL real nos testes de integração; não substitua por H2.
- Mantenha somente arquivos necessários ao projeto. Atualize documentação existente em vez de criar relatórios, planos ou instruções redundantes.
- Não amplie funcionalidades nem invente atores para completar um CRUD. Cada usuário mantém seu próprio cadastro; exclusão significa inativação para conservar o histórico.
