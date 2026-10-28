package br.pucminas.moedaestudantil.merito;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EmailService {
    private final NotificacaoRepository notificacoes;
    private final JavaMailSender smtp;
    private final String remetente;
    public EmailService(NotificacaoRepository notificacoes, JavaMailSender smtp,
                        @Value("${app.email.remetente}") String remetente) {
        this.notificacoes = notificacoes; this.smtp = smtp; this.remetente = remetente;
    }
    @Transactional
    public void processarPendentes() {
        for (var notificacao : notificacoes.pendentes(Instant.now())) {
            var mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente); mensagem.setTo(notificacao.getDestinatario());
            mensagem.setSubject(notificacao.getAssunto()); mensagem.setText(notificacao.getConteudo());
            try { smtp.send(mensagem); notificacao.enviada(); }
            catch (MailException exception) { notificacao.falhou(); }
        }
    }
}
