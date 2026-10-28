package br.pucminas.moedaestudantil.cadastro;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "conta")
public class Conta {
    @Id private UUID id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", unique = true) private Usuario titular;
    @Column(nullable = false) private long saldo;
    @Version @Column(name = "versao", nullable = false) private long versao;
    protected Conta() {}
    public Conta(Usuario titular) { id = UUID.randomUUID(); this.titular = titular; saldo = 0; }
    public long getSaldo() { return saldo; }
    public UUID getId() { return id; }
    public Usuario getTitular() { return titular; }
    public void creditar(long quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva.");
        saldo = Math.addExact(saldo, quantidade);
    }
    public void debitar(long quantidade) {
        if (quantidade <= 0 || quantidade > saldo) throw new IllegalArgumentException("Saldo insuficiente ou quantidade inválida.");
        saldo -= quantidade;
    }
}
