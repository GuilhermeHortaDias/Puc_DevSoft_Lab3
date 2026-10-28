package br.pucminas.moedaestudantil.merito;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificacao")
public class NotificacaoEmail {
    @Id private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "movimentacao_id") private Movimentacao movimentacao;
    @Column(nullable = false, length = 254) private String destinatario;
    @Column(nullable = false, length = 200) private String assunto;
    @Column(nullable = false, columnDefinition = "text") private String conteudo;
    @Column(nullable = false, length = 15) private String estado;
    @Column(nullable = false) private int tentativas;
    @Column(name = "proxima_tentativa", nullable = false) private Instant proximaTentativa;
    @Column(name = "ultimo_erro", length = 300) private String ultimoErro;
    protected NotificacaoEmail() {}
    public NotificacaoEmail(Movimentacao movimentacao, String destinatario, String assunto, String conteudo) {
        id = UUID.randomUUID(); this.movimentacao = movimentacao; this.destinatario = destinatario;
        this.assunto = assunto; this.conteudo = conteudo; estado = "PENDENTE"; proximaTentativa = Instant.now();
    }
    public String getDestinatario() { return destinatario; }
    public String getAssunto() { return assunto; }
    public String getConteudo() { return conteudo; }
    public void enviada() { tentativas++; estado = "ENVIADA"; ultimoErro = null; }
    public void falhou() {
        tentativas++; estado = "FALHA";
        ultimoErro = "Envio SMTP não confirmado. Uma nova tentativa será realizada.";
        proximaTentativa = Instant.now().plusSeconds(Math.min(3600L, 30L * (1L << Math.min(tentativas, 6))));
    }
}
