package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {
    @Id private UUID id;
    @Column(nullable = false, length = 30) private String tipo;
    @Column(nullable = false) private long quantidade;
    @Column(name = "data_hora", nullable = false) private Instant dataHora;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "origem_id") private Conta origem;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "destino_id") private Conta destino;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "solicitante_id") private Usuario solicitante;
    @Column(name = "requisicao_id", nullable = false) private UUID requisicaoId;
    @Column(length = 2000) private String motivo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vantagem_id") private Vantagem vantagem;
    @Column(name = "descricao_vantagem", length = 2000) private String descricaoVantagem;
    @Column(name = "cupom_codigo", unique = true, length = 40) private String cupomCodigo;
    private Integer ano;
    private Integer semestre;
    protected Movimentacao() {}
    private Movimentacao(String tipo, long quantidade, Usuario solicitante, UUID requisicaoId) {
        id = UUID.randomUUID(); dataHora = Instant.now(); this.tipo = tipo; this.quantidade = quantidade;
        this.solicitante = solicitante; this.requisicaoId = requisicaoId;
    }
    public static Movimentacao reconhecimento(Conta origem, Conta destino, long quantidade, String motivo, UUID requisicaoId) {
        var m = new Movimentacao("RECONHECIMENTO", quantidade, origem.getTitular(), requisicaoId);
        m.origem = origem; m.destino = destino; m.motivo = motivo; return m;
    }
    public static Movimentacao resgate(Conta origem, Vantagem vantagem, UUID requisicaoId) {
        var m = new Movimentacao("RESGATE", vantagem.getCusto(), origem.getTitular(), requisicaoId);
        m.origem = origem; m.vantagem = vantagem; m.descricaoVantagem = vantagem.getDescricao();
        m.cupomCodigo = UUID.randomUUID().toString(); return m;
    }
    public static Movimentacao credito(Conta destino, int ano, int semestre, UUID requisicaoId) {
        var m = new Movimentacao("CREDITO_SEMESTRAL", 1000, destino.getTitular(), requisicaoId);
        m.destino = destino; m.ano = ano; m.semestre = semestre; return m;
    }
    public UUID getId() { return id; }
    public String getTipo() { return tipo; }
    public long getQuantidade() { return quantidade; }
    public Instant getDataHora() { return dataHora; }
    public Conta getOrigem() { return origem; }
    public Conta getDestino() { return destino; }
    public String getMotivo() { return motivo; }
    public Vantagem getVantagem() { return vantagem; }
    public String getDescricaoVantagem() { return descricaoVantagem; }
    public String getCupomCodigo() { return cupomCodigo; }
    public Integer getAno() { return ano; }
    public Integer getSemestre() { return semestre; }
}
