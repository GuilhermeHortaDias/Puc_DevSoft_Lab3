package br.pucminas.moedaestudantil.cadastro;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import static br.pucminas.moedaestudantil.cadastro.CadastrosDtos.*;

@Service
@Transactional(readOnly = true)
public class CadastrosService {
    private final UsuarioRepository usuarios;
    private final AlunoRepository alunos;
    private final EmpresaRepository empresas;
    private final InstituicaoRepository instituicoes;
    private final ContaRepository contas;
    private final PasswordEncoder senhas;

    public CadastrosService(UsuarioRepository usuarios, AlunoRepository alunos, EmpresaRepository empresas,
                           InstituicaoRepository instituicoes, ContaRepository contas, PasswordEncoder senhas) {
        this.usuarios = usuarios; this.alunos = alunos; this.empresas = empresas;
        this.instituicoes = instituicoes; this.contas = contas; this.senhas = senhas;
    }

    @Transactional
    public AlunoSaida cadastrarAluno(CadastroAluno entrada) {
        var instituicao = instituicao(entrada.dados().instituicaoId());
        var usuario = novoUsuario(entrada.login(), entrada.senha(), "ALUNO");
        validarCpf(entrada.dados().cpf(), usuario.getId());
        usuario = usuarios.save(usuario);
        var aluno = alunos.save(new Aluno(usuario, entrada.dados(), instituicao));
        contas.save(new Conta(usuario));
        alunos.flush();
        return saida(aluno);
    }

    @Transactional
    public EmpresaSaida cadastrarEmpresa(CadastroEmpresa entrada) {
        var usuario = usuarios.save(novoUsuario(entrada.login(), entrada.senha(), "EMPRESA"));
        var empresa = empresas.saveAndFlush(new EmpresaParceira(usuario, entrada.dados()));
        return saida(empresa);
    }

    public AlunoSaida consultarAluno(UUID id, String login) { return saida(alunoDoTitular(id, login)); }
    public EmpresaSaida consultarEmpresa(UUID id, String login) { return saida(empresaDoTitular(id, login)); }
    public List<AlunoSaida> listarAlunos(String login) { return List.of(consultarAluno(usuarioAtivo(login).getId(), login)); }
    public List<EmpresaSaida> listarEmpresas(String login) { return List.of(consultarEmpresa(usuarioAtivo(login).getId(), login)); }

    @Transactional
    public AlunoSaida atualizarAluno(UUID id, AlunoDados entrada, String login) {
        var aluno = alunoDoTitular(id, login);
        validarCpf(entrada.cpf(), id);
        aluno.atualizar(entrada, instituicao(entrada.instituicaoId()));
        alunos.flush();
        return saida(aluno);
    }

    @Transactional
    public EmpresaSaida atualizarEmpresa(UUID id, EmpresaDados entrada, String login) {
        var empresa = empresaDoTitular(id, login);
        empresa.atualizar(entrada);
        empresas.flush();
        return saida(empresa);
    }

    @Transactional
    public void inativarAluno(UUID id, String login) { alunoDoTitular(id, login).getUsuario().inativar(); }
    @Transactional
    public void inativarEmpresa(UUID id, String login) { empresaDoTitular(id, login).getUsuario().inativar(); }

    public List<InstituicaoSaida> listarInstituicoes() {
        return instituicoes.findAll(org.springframework.data.domain.Sort.by("nome")).stream()
                .map(i -> new InstituicaoSaida(i.getId(), i.getNome())).toList();
    }

    public SessaoSaida sessao(String login) {
        var usuario = usuarioAtivo(login);
        String nome = switch (usuario.getPerfil()) {
            case "ALUNO" -> alunos.findById(usuario.getId()).orElseThrow().getNome();
            case "EMPRESA" -> empresas.findById(usuario.getId()).orElseThrow().getNome();
            default -> usuario.getLogin();
        };
        return new SessaoSaida(usuario.getId(), usuario.getLogin(), usuario.getPerfil(), nome);
    }

    private Usuario novoUsuario(String login, String senha, String perfil) {
        String normalizado = login.strip().toLowerCase(Locale.ROOT);
        if (usuarios.existsByLogin(normalizado)) { throw erro(HttpStatus.CONFLICT, "Login já cadastrado."); }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw erro(HttpStatus.BAD_REQUEST, "A senha deve ocupar no máximo 72 bytes em UTF-8.");
        }
        return new Usuario(normalizado, senhas.encode(senha), perfil);
    }

    private Usuario usuarioAtivo(String login) {
        var usuario = usuarios.findByLogin(login).orElseThrow(() -> erro(HttpStatus.UNAUTHORIZED, "Entre na sua conta."));
        if (!usuario.isAtivo()) { throw erro(HttpStatus.UNAUTHORIZED, "Cadastro inativo."); }
        return usuario;
    }
    private void autorizar(UUID id, String login, String perfil) {
        var usuario = usuarioAtivo(login);
        if (!usuario.getId().equals(id) || !usuario.getPerfil().equals(perfil)) {
            throw erro(HttpStatus.FORBIDDEN, "Você pode acessar somente seu próprio cadastro.");
        }
    }
    private Aluno alunoDoTitular(UUID id, String login) {
        autorizar(id, login, "ALUNO");
        return alunos.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
    }
    private EmpresaParceira empresaDoTitular(UUID id, String login) {
        autorizar(id, login, "EMPRESA");
        return empresas.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Empresa não encontrada."));
    }
    private Instituicao instituicao(Long id) {
        return instituicoes.findById(id).orElseThrow(() -> erro(HttpStatus.BAD_REQUEST, "Selecione uma instituição cadastrada."));
    }
    private void validarCpf(String cpf, UUID id) {
        if (alunos.existsByCpfAndIdNot(cpf, id)) { throw erro(HttpStatus.CONFLICT, "CPF já cadastrado."); }
    }
    private AlunoSaida saida(Aluno aluno) {
        var instituicao = aluno.getInstituicao();
        long saldo = contas.findByTitularId(aluno.getId()).orElseThrow().getSaldo();
        return new AlunoSaida(aluno.getId(), aluno.getUsuario().getLogin(), aluno.getNome(), aluno.getEmail(),
                aluno.getCpf(), aluno.getRg(), aluno.getEndereco(), aluno.getCurso(), instituicao.getId(), instituicao.getNome(), saldo);
    }
    private EmpresaSaida saida(EmpresaParceira empresa) {
        return new EmpresaSaida(empresa.getId(), empresa.getUsuario().getLogin(), empresa.getNome(), empresa.getEmail());
    }
    private ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
