package br.pucminas.moedaestudantil.cadastro;

import jakarta.persistence.*;

@Entity
@Table(name = "instituicao")
public class Instituicao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 150) private String nome;
    protected Instituicao() {}
    public Instituicao(String nome) { this.nome = nome; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
}
