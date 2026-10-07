package br.pucminas.moedaestudantil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CadastrosIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private String aluno(String login, String cpf) {
        return """
            {"login":"%s","senha":"Segredo123!","dados":{"nome":"Ana Silva","email":"ana@example.com",
            "cpf":"%s","rg":"MG123456","endereco":"Rua Um, 10","curso":"Engenharia de Software",
            "instituicaoId":1}}
            """.formatted(login, cpf);
    }

    private MockHttpSession login(String nome) throws Exception {
        return (MockHttpSession) mvc.perform(post("/api/auth/login").with(csrf())
                .param("username", nome).param("password", "Segredo123!"))
                .andExpect(status().isOk()).andReturn().getRequest().getSession(false);
    }

    @Test void alunoTemContaZeroSenhaProtegidaEConsegueAtualizarEInativar() throws Exception {
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("ana-crud", "12345678901")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.saldo").value(0))
                .andExpect(jsonPath("$.senha").doesNotExist());
        var session = login("ana-crud");
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ALUNO"));
        String id = jdbc.queryForObject("select id::text from usuario where login='ana-crud'", String.class);
        mvc.perform(put("/api/alunos/" + id).session(session).with(csrf()).contentType("application/json")
                .content("""
                   {"nome":"Ana Atualizada","email":"nova@example.com","cpf":"12345678901","rg":"MG123456",
                   "endereco":"Rua Dois, 20","curso":"Sistemas de Informação","instituicaoId":1}
                   """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Ana Atualizada"));
        mvc.perform(get("/api/alunos/" + id).session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nova@example.com"));
        assertThat(jdbc.queryForObject("select senha_hash from usuario where login='ana-crud'", String.class))
                .startsWith("$2").isNotEqualTo("Segredo123!");
        mvc.perform(delete("/api/alunos/" + id).session(session).with(csrf())).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("select ativo from usuario where login='ana-crud'", Boolean.class)).isFalse();
        assertThat(jdbc.queryForObject("select count(*) from conta where titular_id=?::uuid", Integer.class, id)).isEqualTo(1);
        mvc.perform(post("/api/auth/login").with(csrf()).param("username", "ana-crud")
                .param("password", "Segredo123!")).andExpect(status().isUnauthorized());
    }

    @Test void empresaConsegueConsultarAtualizarEInativarSemCriarCarteira() throws Exception {
        mvc.perform(post("/api/empresas").with(csrf()).contentType("application/json").content("""
            {"login":"loja-crud","senha":"Segredo123!","dados":{"nome":"Livraria PUC","email":"loja@example.com"}}
            """)).andExpect(status().isCreated());
        var session = login("loja-crud");
        String id = jdbc.queryForObject("select id::text from usuario where login='loja-crud'", String.class);
        mvc.perform(get("/api/empresas/" + id).session(session)).andExpect(status().isOk());
        mvc.perform(put("/api/empresas/" + id).session(session).with(csrf()).contentType("application/json")
                .content("{\"nome\":\"Livraria Atualizada\",\"email\":\"novo@example.com\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Livraria Atualizada"));
        assertThat(jdbc.queryForObject("select count(*) from conta where titular_id=?::uuid", Integer.class, id)).isZero();
        mvc.perform(delete("/api/empresas/" + id).session(session).with(csrf())).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("select ativo from usuario where login='loja-crud'", Boolean.class)).isFalse();
    }

    @Test void duplicidadeNaoDeixaAlunoOuCarteiraParcial() throws Exception {
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("aluno-unico", "23456789012"))).andExpect(status().isCreated());
        int antes = jdbc.queryForObject("select count(*) from usuario", Integer.class);
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("outro-login", "23456789012"))).andExpect(status().isConflict());
        assertThat(jdbc.queryForObject("select count(*) from usuario", Integer.class)).isEqualTo(antes);
        mvc.perform(post("/api/empresas").with(csrf()).contentType("application/json").content("""
            {"login":"ALUNO-UNICO","senha":"Segredo123!","dados":{"nome":"Loja","email":"loja@example.com"}}
            """)).andExpect(status().isConflict());
    }

    @Test void rejeitaDadosInvalidosEInstituicaoInexistente() throws Exception {
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("invalido", "123"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("sem-instituicao", "34567890123").replace("\"instituicaoId\":1", "\"instituicaoId\":999")))
                .andExpect(status().isBadRequest());
    }

    @Test void naoPermiteConsultarOutroAlunoNemModificarSemCsrf() throws Exception {
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("aluno-protegido", "45678901234"))).andExpect(status().isCreated());
        mvc.perform(post("/api/alunos").with(csrf()).contentType("application/json")
                .content(aluno("aluno-intruso", "56789012345"))).andExpect(status().isCreated());
        var session = login("aluno-intruso");
        String id = jdbc.queryForObject("select id::text from usuario where login='aluno-protegido'", String.class);
        mvc.perform(get("/api/alunos/" + id).session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/api/alunos/" + id)).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/alunos/" + id).session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/api/empresas").session(session)).andExpect(status().isForbidden());
    }
}
