package br.pucminas.moedaestudantil.merito;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class MeritoDtos {
    private MeritoDtos() {}
    public record ProfessorSaida(UUID id, String login, String nome, String cpf, String departamento,
                                  String instituicao, Long instituicaoId) {}
    public record AlunoResumo(UUID id, String nome, String curso) {}
    public record VantagemSaida(UUID id, UUID empresaId, String empresa, String descricao, long custo, String fotoUrl) {}
    public record FotoSaida(String tipo, byte[] bytes) {}
    public record EnvioEntrada(@NotNull UUID alunoId, @Positive long quantidade,
                              @NotBlank @Size(max = 2000) String motivo, @NotNull UUID requisicaoId) {}
    public record ResgateEntrada(@NotNull UUID vantagemId, @NotNull UUID requisicaoId) {}
    public record ExtratoSaida(UUID id, String tipo, Instant dataHora, long quantidade, String direcao,
                              String descricao, String contraparte, String motivo, String cupomCodigo) {}
    public record ContaSaida(long saldo, List<ExtratoSaida> extrato) {}
}
