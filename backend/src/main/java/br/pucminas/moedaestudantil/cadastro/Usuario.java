package br.pucminas.moedaestudantil.cadastro;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 80) private String login;
    @Column(name = "senha_hash", nullable = false, length = 100) private String senhaHash;
    @Column(nullable = false, length = 20) private String perfil;
    @Column(nullable = false) private boolean ativo = true;
    @Column(name = "criado_em", nullable = false) private Instant criadoEm = Instant.now();

    protected Usuario() {}
    public Usuario(String login, String senhaHash, String perfil) {
        this.id = UUID.randomUUID(); this.login = login; this.senhaHash = senhaHash; this.perfil = perfil;
    }
    public UUID getId() { return id; }
    public String getLogin() { return login; }
    public String getSenhaHash() { return senhaHash; }
    public String getPerfil() { return perfil; }
    public boolean isAtivo() { return ativo; }
    public void inativar() { ativo = false; }
}
