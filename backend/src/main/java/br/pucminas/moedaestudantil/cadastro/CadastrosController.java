package br.pucminas.moedaestudantil.cadastro;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import static br.pucminas.moedaestudantil.cadastro.CadastrosDtos.*;

@RestController
@RequestMapping("/api")
public class CadastrosController {
    private final CadastrosService service;
    public CadastrosController(CadastrosService service) { this.service = service; }

    @GetMapping("/auth/csrf") public TokenSaida csrf(CsrfToken token) {
        return new TokenSaida(token.getToken(), token.getHeaderName());
    }
    public record TokenSaida(String token, String headerName) {}
    @GetMapping("/auth/me") public SessaoSaida sessao(Authentication auth) { return service.sessao(auth.getName()); }
    @GetMapping("/instituicoes") public List<InstituicaoSaida> instituicoes() { return service.listarInstituicoes(); }

    @PostMapping("/alunos") public ResponseEntity<AlunoSaida> cadastrarAluno(@Valid @RequestBody CadastroAluno entrada) {
        var saida = service.cadastrarAluno(entrada);
        return ResponseEntity.created(URI.create("/api/alunos/" + saida.id())).body(saida);
    }
    @GetMapping("/alunos") public List<AlunoSaida> alunos(Authentication auth) { return service.listarAlunos(auth.getName()); }
    @GetMapping("/alunos/{id}") public AlunoSaida aluno(@PathVariable UUID id, Authentication auth) {
        return service.consultarAluno(id, auth.getName());
    }
    @PutMapping("/alunos/{id}") public AlunoSaida atualizarAluno(@PathVariable UUID id, @Valid @RequestBody AlunoDados entrada, Authentication auth) {
        return service.atualizarAluno(id, entrada, auth.getName());
    }
    @DeleteMapping("/alunos/{id}") public ResponseEntity<Void> inativarAluno(@PathVariable UUID id, Authentication auth,
                                                       HttpServletRequest request, HttpServletResponse response) {
        service.inativarAluno(id, auth.getName()); encerrarSessao(auth, request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/empresas") public ResponseEntity<EmpresaSaida> cadastrarEmpresa(@Valid @RequestBody CadastroEmpresa entrada) {
        var saida = service.cadastrarEmpresa(entrada);
        return ResponseEntity.created(URI.create("/api/empresas/" + saida.id())).body(saida);
    }
    @GetMapping("/empresas") public List<EmpresaSaida> empresas(Authentication auth) { return service.listarEmpresas(auth.getName()); }
    @GetMapping("/empresas/{id}") public EmpresaSaida empresa(@PathVariable UUID id, Authentication auth) {
        return service.consultarEmpresa(id, auth.getName());
    }
    @PutMapping("/empresas/{id}") public EmpresaSaida atualizarEmpresa(@PathVariable UUID id, @Valid @RequestBody EmpresaDados entrada, Authentication auth) {
        return service.atualizarEmpresa(id, entrada, auth.getName());
    }
    @DeleteMapping("/empresas/{id}") public ResponseEntity<Void> inativarEmpresa(@PathVariable UUID id, Authentication auth,
                                                         HttpServletRequest request, HttpServletResponse response) {
        service.inativarEmpresa(id, auth.getName()); encerrarSessao(auth, request, response);
        return ResponseEntity.noContent().build();
    }
    private void encerrarSessao(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, auth);
        new CookieClearingLogoutHandler("JSESSIONID").logout(request, response, auth);
    }
}
