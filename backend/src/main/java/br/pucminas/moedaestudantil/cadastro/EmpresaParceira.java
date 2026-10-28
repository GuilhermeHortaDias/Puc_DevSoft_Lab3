package br.pucminas.moedaestudantil.cadastro;

import jakarta.persistence.*;
import java.util.UUID;
import br.pucminas.moedaestudantil.cadastro.CadastrosDtos.EmpresaDados;

@Entity
@Table(name = "empresa_parceira")
public class EmpresaParceira {
    @Id private UUID id;
    @MapsId @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "id") private Usuario usuario;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, length = 254) private String email;

    protected EmpresaParceira() {}
    public EmpresaParceira(Usuario usuario, EmpresaDados dados) { this.usuario = usuario; atualizar(dados); }
    public void atualizar(EmpresaDados dados) { nome = dados.nome().strip(); email = dados.email().strip(); }
    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
