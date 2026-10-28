# Roteiro de apresentação — Sprint 2

Tempo sugerido: 5 a 7 minutos, ajustável ao andamento semanal do grupo.

## 1. Requisitos da etapa

O enunciado da Lab03S02 pede três resultados: modelo ER, estratégia de persistência definida e implementada e CRUD inicial de aluno e empresa com interface conectada ao back-end.

## 2. Apresentar o modelo ER

Mostrar [`MODELO_ER.md`](MODELO_ER.md). Explicar a entidade comum `Usuario`, as especializações de aluno/empresa, a instituição pré-cadastrada e as chaves únicas. Destacar que a tabela de aluno aponta para uma instituição existente.

## 3. Explicar a persistência e a arquitetura

Mostrar a separação MVC: páginas web como View; controlador HTTP; serviços de validação e transação; modelos ORM e DAOs; SQLite como banco local. A senha é guardada como hash com salt, e o login é único entre os perfis.

## 4. Demonstrar o CRUD

1. Executar `python -m sprint2` e abrir `http://127.0.0.1:8000`.
2. Criar um aluno e selecionar PUC Minas; mostrar a linha na lista.
3. Editar um campo e salvar; mostrar a atualização.
4. Mudar para empresas parceiras, criar e editar uma empresa.
5. Excluir um cadastro de demonstração e mostrar a lista atualizada.

## 5. Encerrar com o próximo passo

Explicar que esta é a versão inicial de CRUD da Sprint 2. A etapa seguinte conclui os cadastros e apresenta arquitetura e persistência; os fluxos restantes do sistema serão implementados de acordo com o plano e o modelo atualizado.
