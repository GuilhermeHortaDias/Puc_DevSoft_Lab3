package br.pucminas.moedaestudantil.merito;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class CreditoSemestralJob {
    private final ProfessorRepository professores;
    private final CreditoSemestralService creditos;
    public CreditoSemestralJob(ProfessorRepository professores, CreditoSemestralService creditos) {
        this.professores = professores; this.creditos = creditos;
    }
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 1 1,7 *", zone = "America/Sao_Paulo")
    public void executar() {
        LocalDate data = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        professores.findByUsuarioAtivoTrue().forEach(p -> creditos.creditar(p.getId(), data));
    }
}
