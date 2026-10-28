package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.*;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@Service
public class ImportacaoInstitucionalService {
    public record Arquivo(@NotNull List<@NotBlank @Size(max = 150) String> instituicoes,
                          @NotNull List<@NotNull @Valid DadosProfessor> professores) {}
    public record DadosProfessor(@NotBlank @Size(max = 150) String nome,
            @NotBlank @Pattern(regexp = "[0-9]{11}") String cpf,
            @NotBlank @Size(max = 150) String departamento,
            @NotBlank @Size(max = 150) String instituicao,
            @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,80}") String login,
            @NotBlank @Size(min = 8, max = 72) String senha) {}

    private final InstituicaoRepository instituicoes;
    private final DepartamentoRepository departamentos;
    private final ProfessorRepository professores;
    private final UsuarioRepository usuarios;
    private final ContaRepository contas;
    private final PasswordEncoder senhas;
    private final Validator validator;
    public ImportacaoInstitucionalService(InstituicaoRepository instituicoes, DepartamentoRepository departamentos,
            ProfessorRepository professores, UsuarioRepository usuarios, ContaRepository contas,
            PasswordEncoder senhas, Validator validator) {
        this.instituicoes = instituicoes; this.departamentos = departamentos; this.professores = professores;
        this.usuarios = usuarios; this.contas = contas; this.senhas = senhas; this.validator = validator;
    }
    @Transactional
    public void importar(Path caminho) {
        final Arquivo arquivo;
        try { arquivo = JsonMapper.builder().build().readValue(caminho.toFile(), Arquivo.class); }
        catch (Exception exception) { throw new IllegalArgumentException("Não foi possível ler o arquivo institucional JSON. Confira caminho e formato."); }
        if (arquivo == null || !validator.validate(arquivo).isEmpty()) {
            throw new IllegalArgumentException("Arquivo institucional inválido. Confira os campos obrigatórios e seus tamanhos no exemplo.");
        }
        for (String nome : arquivo.instituicoes()) {
            String normalizado = nome.strip();
            if (instituicoes.findByNome(normalizado).isEmpty()) instituicoes.saveAndFlush(new Instituicao(normalizado));
        }
        for (var dados : arquivo.professores()) {
            String login = dados.login().toLowerCase(Locale.ROOT);
            if (dados.senha().equals("SENHA_PLACEHOLDER") || dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("Defina uma senha própria de 8 caracteres a 72 bytes em UTF-8 no arquivo institucional.");
            }
            var instituicao = instituicoes.findByNome(dados.instituicao().strip())
                .orElseThrow(() -> new IllegalArgumentException("Instituição do professor não está pré-cadastrada na lista institucional."));
            var departamento = departamentos.findByNomeAndInstituicaoId(dados.departamento().strip(), instituicao.getId())
                .orElseGet(() -> departamentos.saveAndFlush(new Departamento(dados.departamento().strip(), instituicao)));
            var existente = usuarios.findByLogin(login);
            if (existente.isPresent()) {
                var professor = professores.findById(existente.get().getId());
                if (professor.isEmpty() || !"PROFESSOR".equals(existente.get().getPerfil())
                        || !professor.get().getCpf().equals(dados.cpf())
                        || !professor.get().getNome().equals(dados.nome().strip())
                        || !professor.get().getDepartamento().getId().equals(departamento.getId())) {
                    throw new IllegalArgumentException("Login institucional já cadastrado com dados diferentes. Confira a lista antes de importar.");
                }
                continue; // Não redefine senha nem duplica a conta de quem já foi importado.
            }
            if (professores.existsByCpf(dados.cpf())) throw new IllegalArgumentException("CPF de professor repetido na lista institucional.");
            var usuario = usuarios.save(new Usuario(login, senhas.encode(dados.senha()), "PROFESSOR"));
            professores.saveAndFlush(new Professor(usuario, dados.nome().strip(), dados.cpf(), departamento));
            contas.saveAndFlush(new Conta(usuario));
        }
    }
}
