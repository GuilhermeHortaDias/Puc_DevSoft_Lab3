package br.pucminas.moedaestudantil.cadastro;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.UUID;

public final class CadastrosDtos {
    private CadastrosDtos() {}
    public record AlunoDados(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Pattern(regexp = "[0-9]{11}", message = "Informe os 11 dígitos do CPF.") String cpf,
        @NotBlank @Size(max = 30) String rg,
        @NotBlank @Size(max = 300) String endereco,
        @NotBlank @Size(max = 150) String curso,
        @NotNull @Positive Long instituicaoId) {}
    public record EmpresaDados(@NotBlank @Size(max = 150) String nome,
                               @NotBlank @Email @Size(max = 254) String email) {}
    public record CadastroAluno(@NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,80}", message = "Use de 3 a 80 letras, números, pontos, hífens ou sublinhados.") String login,
                                @NotBlank @Size(min = 8, max = 72) String senha,
                                @NotNull @Valid AlunoDados dados) {}
    public record CadastroEmpresa(@NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,80}", message = "Use de 3 a 80 letras, números, pontos, hífens ou sublinhados.") String login,
                                  @NotBlank @Size(min = 8, max = 72) String senha,
                                  @NotNull @Valid EmpresaDados dados) {}
    public record AlunoSaida(UUID id, String login, String nome, String email, String cpf, String rg,
                             String endereco, String curso, Long instituicaoId, String instituicao, long saldo) {}
    public record EmpresaSaida(UUID id, String login, String nome, String email) {}
    public record InstituicaoSaida(Long id, String nome) {}
    public record SessaoSaida(UUID id, String login, String perfil, String nome) {}
}
