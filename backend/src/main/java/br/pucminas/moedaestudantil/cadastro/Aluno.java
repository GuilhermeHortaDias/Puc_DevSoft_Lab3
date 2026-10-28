package br.pucminas.moedaestudantil.cadastro;

import jakarta.persistence.*;
import java.util.UUID;
import br.pucminas.moedaestudantil.cadastro.CadastrosDtos.AlunoDados;

@Entity
@Table(name = "aluno")
public class Aluno {
    @Id private UUID id;
    @MapsId @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "id") private Usuario usuario;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, length = 254) private String email;
    @Column(nullable = false, unique = true, length = 11) private String cpf;
    @Column(nullable = false, length = 30) private String rg;
    @Column(nullable = false, length = 300) private String endereco;
    @Column(nullable = false, length = 150) private String curso;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "instituicao_id") private Instituicao instituicao;

    protected Aluno() {}
    public Aluno(Usuario usuario, AlunoDados dados, Instituicao instituicao) {
        this.usuario = usuario; atualizar(dados, instituicao);
    }
    public void atualizar(AlunoDados dados, Instituicao instituicao) {
        nome = dados.nome().strip(); email = dados.email().strip(); cpf = dados.cpf();
        rg = dados.rg().strip(); endereco = dados.endereco().strip(); curso = dados.curso().strip();
        this.instituicao = instituicao;
    }
    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getRg() { return rg; }
    public String getEndereco() { return endereco; }
    public String getCurso() { return curso; }
    public Instituicao getInstituicao() { return instituicao; }
}
