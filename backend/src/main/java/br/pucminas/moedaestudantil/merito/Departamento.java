package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.Instituicao;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "departamento")
public class Departamento {
    @Id private UUID id;
    @Column(nullable = false, length = 150) private String nome;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "instituicao_id") private Instituicao instituicao;
    protected Departamento() {}
    public Departamento(String nome, Instituicao instituicao) { id = UUID.randomUUID(); this.nome = nome; this.instituicao = instituicao; }
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public Instituicao getInstituicao() { return instituicao; }
}
