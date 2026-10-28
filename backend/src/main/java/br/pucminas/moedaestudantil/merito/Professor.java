package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "professor")
public class Professor {
    @Id private UUID id;
    @MapsId @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "id") private Usuario usuario;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, unique = true, length = 11) private String cpf;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "departamento_id") private Departamento departamento;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "instituicao_id") private Instituicao instituicao;
    protected Professor() {}
    public Professor(Usuario usuario, String nome, String cpf, Departamento departamento) {
        this.usuario = usuario; this.nome = nome; this.cpf = cpf;
        this.departamento = departamento; this.instituicao = departamento.getInstituicao();
    }
    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public Departamento getDepartamento() { return departamento; }
    public Instituicao getInstituicao() { return instituicao; }
}
