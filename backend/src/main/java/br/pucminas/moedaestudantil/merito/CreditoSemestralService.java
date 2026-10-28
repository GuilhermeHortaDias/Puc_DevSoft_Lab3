package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CreditoSemestralService {
    private final ContaRepository contas;
    private final ProfessorRepository professores;
    private final MovimentacaoRepository movimentos;
    public CreditoSemestralService(ContaRepository contas, ProfessorRepository professores, MovimentacaoRepository movimentos) {
        this.contas = contas; this.professores = professores; this.movimentos = movimentos;
    }
    @Transactional
    public void creditar(UUID professorId, LocalDate data) {
        var professor = professores.findById(professorId).orElseThrow();
        if (!professor.getUsuario().isAtivo()) return;
        var lista = contas.bloquearTitulares(List.of(professorId));
        if (lista.isEmpty()) throw new IllegalStateException("Professor sem conta de moedas.");
        var conta = lista.getFirst();
        int semestre = data.getMonthValue() <= 6 ? 1 : 2;
        if (movimentos.existsByDestinoIdAndTipoAndAnoAndSemestre(conta.getId(), "CREDITO_SEMESTRAL", data.getYear(), semestre)) return;
        UUID comando = UUID.nameUUIDFromBytes(("credito:" + professorId + ":" + data.getYear() + ":" + semestre).getBytes(StandardCharsets.UTF_8));
        conta.creditar(1000);
        movimentos.saveAndFlush(Movimentacao.credito(conta, data.getYear(), semestre, comando));
    }
}
