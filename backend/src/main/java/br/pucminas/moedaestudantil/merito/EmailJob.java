package br.pucminas.moedaestudantil.merito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.notificacoes.habilitadas", havingValue = "true", matchIfMissing = true)
public class EmailJob {
    private final EmailService emails;
    public EmailJob(EmailService emails) { this.emails = emails; }
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void executar() { emails.processarPendentes(); }
}
