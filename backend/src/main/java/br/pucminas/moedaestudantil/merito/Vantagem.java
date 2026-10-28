package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.EmpresaParceira;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vantagem")
public class Vantagem {
    @Id private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "empresa_id") private EmpresaParceira empresa;
    @Column(nullable = false, length = 2000) private String descricao;
    @Column(nullable = false) private long custo;
    @Column(nullable = false, columnDefinition = "bytea") private byte[] foto;
    @Column(name = "tipo_foto", nullable = false, length = 40) private String tipoFoto;
    protected Vantagem() {}
    public Vantagem(EmpresaParceira empresa, String descricao, long custo, byte[] foto, String tipoFoto) {
        id = UUID.randomUUID(); this.empresa = empresa; this.descricao = descricao; this.custo = custo;
        this.foto = foto; this.tipoFoto = tipoFoto;
    }
    public UUID getId() { return id; }
    public EmpresaParceira getEmpresa() { return empresa; }
    public String getDescricao() { return descricao; }
    public long getCusto() { return custo; }
    public byte[] getFoto() { return foto; }
    public String getTipoFoto() { return tipoFoto; }
}
