const state = {
  resource: "alunos",
  records: [],
  institutions: [],
  editingId: null,
};

const configurations = {
  alunos: {
    title: "aluno",
    plural: "Alunos",
    listTitle: "Alunos cadastrados",
    endpoint: "/api/alunos",
    fields: [
      { key: "nome", label: "Nome completo", autocomplete: "name", maxLength: 160 },
      { key: "email", label: "Email", type: "email", autocomplete: "email", maxLength: 254 },
      { key: "cpf", label: "CPF", autocomplete: "off", maxLength: 14 },
      { key: "rg", label: "RG", autocomplete: "off", maxLength: 30 },
      { key: "endereco", label: "Endereço", autocomplete: "street-address", maxLength: 240 },
      { key: "curso", label: "Curso", maxLength: 160 },
      { key: "instituicao_id", label: "Instituição", type: "institution" },
      { key: "login", label: "Login", autocomplete: "username", maxLength: 80 },
      { key: "senha", label: "Senha", type: "password", autocomplete: "new-password", minLength: 8 },
    ],
    columns: ["Aluno", "Curso / instituição", "Login", "Ações"],
  },
  "empresas-parceiras": {
    title: "empresa parceira",
    plural: "Empresas parceiras",
    listTitle: "Empresas parceiras cadastradas",
    endpoint: "/api/empresas-parceiras",
    fields: [
      { key: "nome", label: "Nome da empresa", autocomplete: "organization", maxLength: 160 },
      { key: "email", label: "Email de contato", type: "email", autocomplete: "email", maxLength: 254 },
      { key: "login", label: "Login", autocomplete: "username", maxLength: 80 },
      { key: "senha", label: "Senha", type: "password", autocomplete: "new-password", minLength: 8 },
    ],
    columns: ["Empresa", "Email", "Login", "Ações"],
  },
};

const form = document.querySelector("#record-form");
const fieldsContainer = document.querySelector("#form-fields");
const submitButton = document.querySelector("#submit-form");
const cancelButton = document.querySelector("#cancel-edit");
const formMessage = document.querySelector("#form-message");
const listMessage = document.querySelector("#list-message");
const tableHead = document.querySelector("#table-head");
const tableBody = document.querySelector("#table-body");
const countLabel = document.querySelector("#record-count");
const connectionStatus = document.querySelector("#connection-status");

function element(tag, className, text) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text !== undefined) node.textContent = text;
  return node;
}

async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
  });
  if (response.status === 204) return null;
  const data = await response.json();
  if (!response.ok) throw new Error(data.erro || "Não foi possível concluir a solicitação.");
  return data;
}

function setConnection(online, message) {
  connectionStatus.classList.toggle("is-online", online);
  connectionStatus.classList.toggle("is-offline", !online);
  connectionStatus.lastElementChild.textContent = message;
}

function setFormMessage(message, success = false) {
  formMessage.textContent = message;
  formMessage.classList.toggle("is-success", success);
}

function appendField(config) {
  const wrapper = element("div", "field");
  const label = element("label", "", config.label);
  label.htmlFor = `field-${config.key}`;
  let input;

  if (config.type === "institution") {
    input = document.createElement("select");
    input.append(element("option", "", "Selecione uma instituição"));
    input.options[0].value = "";
    for (const institution of state.institutions) {
      const option = element("option", "", institution.nome);
      option.value = institution.id;
      input.append(option);
    }
  } else {
    input = document.createElement("input");
    input.type = config.type || "text";
    input.autocomplete = config.autocomplete || "off";
    if (config.maxLength) input.maxLength = config.maxLength;
    if (config.minLength) input.minLength = config.minLength;
    if (config.type === "email") input.inputMode = "email";
  }

  input.id = `field-${config.key}`;
  input.name = config.key;
  input.required = config.key !== "senha" || state.editingId === null;
  if (config.key === "senha" && state.editingId !== null) {
    input.placeholder = "Deixe em branco para manter a senha";
  }
  wrapper.append(label, input);

  if (config.key === "senha") {
    wrapper.append(element("span", "field-hint", "Mínimo de 8 caracteres. A senha é armazenada com hash."));
  }
  fieldsContainer.append(wrapper);
}

function renderForm(record = null) {
  const config = configurations[state.resource];
  fieldsContainer.replaceChildren();
  for (const field of config.fields) appendField(field);

  if (record) {
    for (const field of config.fields) {
      const input = form.elements.namedItem(field.key);
      if (input && field.key !== "senha") input.value = record[field.key] ?? "";
    }
  }

  document.querySelector("#form-kicker").textContent = record ? "Editando cadastro" : "Novo cadastro";
  document.querySelector("#form-title").textContent = record
    ? `Editar ${config.title}`
    : `Cadastrar ${config.title}`;
  submitButton.textContent = record ? "Salvar alterações" : `Salvar ${config.title}`;
  cancelButton.hidden = !record;
  setFormMessage("");
}

function renderTable() {
  const config = configurations[state.resource];
  document.querySelector("#list-title").textContent = config.listTitle;
  tableHead.replaceChildren();
  const headerRow = document.createElement("tr");
  for (const label of config.columns) headerRow.append(element("th", "", label));
  tableHead.append(headerRow);

  tableBody.replaceChildren();
  countLabel.textContent = `${state.records.length} ${state.records.length === 1 ? "registro" : "registros"}`;
  listMessage.textContent = state.records.length ? "" : "Nenhum cadastro encontrado. Use o formulário para começar.";

  for (const record of state.records) {
    const row = document.createElement("tr");
    const identity = document.createElement("td");
    identity.append(element("span", "primary-cell", record.nome));
    if (state.resource === "alunos") identity.append(element("span", "secondary-cell", record.email));
    row.append(identity);

    if (state.resource === "alunos") {
      const study = document.createElement("td");
      study.append(element("span", "primary-cell", record.curso));
      study.append(element("span", "secondary-cell", record.instituicao));
      row.append(study);
    } else {
      row.append(element("td", "", record.email));
    }

    row.append(element("td", "", record.login));
    const actions = element("td");
    const actionGroup = element("div", "row-actions");
    const editButton = element("button", "row-action", "Editar");
    editButton.type = "button";
    editButton.dataset.action = "edit";
    editButton.dataset.id = record.id;
    editButton.setAttribute("aria-label", `Editar ${record.nome}`);
    const deleteButton = element("button", "row-action delete", "Excluir");
    deleteButton.type = "button";
    deleteButton.dataset.action = "delete";
    deleteButton.dataset.id = record.id;
    deleteButton.setAttribute("aria-label", `Excluir ${record.nome}`);
    actionGroup.append(editButton, deleteButton);
    actions.append(actionGroup);
    row.append(actions);
    tableBody.append(row);
  }
}

async function loadRecords() {
  const config = configurations[state.resource];
  listMessage.textContent = "Carregando registros…";
  try {
    state.records = await request(config.endpoint);
    setConnection(true, "Servidor conectado");
    renderTable();
  } catch (error) {
    state.records = [];
    renderTable();
    listMessage.textContent = error.message;
    setConnection(false, "Servidor indisponível");
  }
}

async function changeResource(resource) {
  state.resource = resource;
  state.editingId = null;
  document.querySelectorAll(".tab").forEach((tab) => {
    const active = tab.dataset.resource === resource;
    tab.classList.toggle("is-active", active);
    tab.setAttribute("aria-selected", String(active));
  });
  renderForm();
  renderTable();
  await loadRecords();
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!form.reportValidity()) return;

  const config = configurations[state.resource];
  const payload = Object.fromEntries(new FormData(form).entries());
  if (payload.instituicao_id !== undefined) payload.instituicao_id = Number(payload.instituicao_id);
  if (!payload.senha) delete payload.senha;
  const editing = state.editingId !== null;
  const path = editing ? `${config.endpoint}/${state.editingId}` : config.endpoint;
  submitButton.disabled = true;
  try {
    await request(path, { method: editing ? "PUT" : "POST", body: JSON.stringify(payload) });
    setFormMessage(editing ? "Cadastro atualizado." : "Cadastro criado.", true);
    state.editingId = null;
    form.reset();
    renderForm();
    setFormMessage(editing ? "Cadastro atualizado." : "Cadastro criado.", true);
    await loadRecords();
  } catch (error) {
    setFormMessage(error.message);
  } finally {
    submitButton.disabled = false;
  }
});

cancelButton.addEventListener("click", () => {
  state.editingId = null;
  form.reset();
  renderForm();
});

document.querySelectorAll(".tab").forEach((tab) => {
  tab.addEventListener("click", () => changeResource(tab.dataset.resource));
});

tableBody.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;
  const record = state.records.find((item) => String(item.id) === button.dataset.id);
  if (!record) return;

  if (button.dataset.action === "edit") {
    state.editingId = record.id;
    renderForm(record);
    document.querySelector("#form-title").scrollIntoView({ behavior: "smooth", block: "center" });
    return;
  }

  if (!window.confirm(`Excluir o cadastro de ${record.nome}?`)) return;
  const endpoint = configurations[state.resource].endpoint;
  try {
    await request(`${endpoint}/${record.id}`, { method: "DELETE" });
    if (state.editingId === record.id) {
      state.editingId = null;
      form.reset();
      renderForm();
    }
    await loadRecords();
  } catch (error) {
    listMessage.textContent = error.message;
  }
});

async function initialize() {
  try {
    state.institutions = await request("/api/instituicoes");
  } catch (error) {
    setConnection(false, "Servidor indisponível");
    listMessage.textContent = error.message;
  }
  renderForm();
  await loadRecords();
}

initialize();
