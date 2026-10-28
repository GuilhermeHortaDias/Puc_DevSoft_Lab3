# Entrega da Sprint 2 — Lab03S02

**Sistema de Moeda Estudantil · Projeto de Software · PUC Minas · 2º semestre de 2026**

Integrantes: Guilherme Horta Dias, Lucas Batista Duarte e Rafael Abras.  
Professora: Milena Menezes Adão.

## Escopo entregue

A seção **4.2 — Lab03S02** do [enunciado](../LABORATORIO_3_LAB_DESENVOLVIMENTO_DE_SOFTWARE.pdf) pede o modelo ER, definição e implementação da persistência e uma versão inicial dos CRUDs de aluno e empresa parceira, com front-end comunicando-se com o back-end.

| Item | Entrega |
| --- | --- |
| Modelo ER | [Diagrama e descrição das tabelas e restrições](MODELO_ER.md) |
| Persistência | SQLite com SQLAlchemy ORM e DAOs; esquema criado na inicialização e chave estrangeira habilitada |
| CRUD de aluno | Criar, consultar, listar, editar e excluir; instituição escolhida de uma lista pré-cadastrada |
| CRUD de empresa parceira | Criar, consultar, listar, editar e excluir |
| Front-end e back-end | Interface web responsiva servida pela aplicação e conectada aos endpoints JSON locais |
| Apresentação semanal | [Roteiro sugerido](ROTEIRO_APRESENTACAO.md) |

## Executar localmente

Requer Python 3.9 ou superior. Na raiz do repositório:

```sh
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
python -m sprint2
```

Abra <http://127.0.0.1:8000>. No Windows PowerShell, ative o ambiente com `.venv\Scripts\Activate.ps1`.

Na primeira inicialização, a aplicação cria `data/sprint2.sqlite3`, cria as tabelas e cadastra **PUC Minas** como instituição de demonstração. O arquivo do banco fica fora do versionamento. A configuração pode ser alterada por `SPRINT2_DATABASE_URL`; `SPRINT2_HOST` e `SPRINT2_PORT` mudam endereço e porta do servidor.

## API disponível

| Método e caminho | Ação |
| --- | --- |
| `GET /api/instituicoes` | Lista as instituições para o formulário de aluno |
| `GET /api/alunos` | Lista alunos; aceita `?busca=termo` |
| `POST /api/alunos` | Cria aluno e identidade associada |
| `GET /api/alunos/{id}` | Consulta aluno |
| `PUT /api/alunos/{id}` | Atualiza aluno; senha em branco mantém a atual |
| `DELETE /api/alunos/{id}` | Exclui aluno e identidade associada |
| `GET /api/empresas-parceiras` | Lista empresas; aceita `?busca=termo` |
| `POST /api/empresas-parceiras` | Cria empresa e identidade associada |
| `GET /api/empresas-parceiras/{id}` | Consulta empresa |
| `PUT /api/empresas-parceiras/{id}` | Atualiza empresa; senha em branco mantém a atual |
| `DELETE /api/empresas-parceiras/{id}` | Exclui empresa e identidade associada |

As requisições de criação e atualização usam JSON. Erros de validação retornam `400`, duplicidades `409` e registros inexistentes `404`. Os dados de senha nunca aparecem nas respostas; o banco guarda um hash PBKDF2-SHA256 com salt aleatório.

## Arquitetura e limites desta versão

A interface HTML/CSS/JavaScript é a **View**; `controllers.py` recebe HTTP e chama `services.py` (regras e transações); `models.py` define o Model ORM; `dao.py` concentra consultas e gravações; `database.py` configura o SQLite. É uma separação MVC em módulos de uma aplicação única, alinhada ao componente de persistência da Sprint 1.

Esta entrega inicial cobre os CRUDs pedidos na Sprint 2. Login, controle de acesso por perfil, professores, vantagens, moedas e cupons permanecem fora desta versão; os campos de login já são persistidos e senhas são protegidas por hash. A instituição é pré-cadastrada pelo processo de inicialização, conforme a premissa do enunciado.

## Arquivos da implementação

- Aplicação: [`sprint2/`](../../sprint2/)
- Dependência ORM: [`requirements.txt`](../../requirements.txt)
- Entidade e relacionamento: [`models.py`](../../sprint2/models.py)
- DAOs e regras: [`dao.py`](../../sprint2/dao.py) e [`services.py`](../../sprint2/services.py)
- Interface: [`index.html`](../../sprint2/static/index.html)
