package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static br.pucminas.moedaestudantil.merito.MeritoDtos.*;

@Service
@Transactional(readOnly = true)
public class ProfessorService {
    private final UsuarioAtual atual;
    private final ProfessorRepository professores;
    private final AlunoRepository alunos;
    public ProfessorService(UsuarioAtual atual, ProfessorRepository professores, AlunoRepository alunos) {
        this.atual = atual; this.professores = professores; this.alunos = alunos;
    }
    public ProfessorSaida consultar(String login) {
        var p = professor(login);
        return new ProfessorSaida(p.getId(), p.getUsuario().getLogin(), p.getNome(), p.getCpf(),
                p.getDepartamento().getNome(), p.getInstituicao().getNome(), p.getInstituicao().getId());
    }
    public List<AlunoResumo> alunos(String login) {
        return alunos.findByInstituicaoIdAndUsuarioAtivoTrueOrderByNome(professor(login).getInstituicao().getId())
                .stream().map(a -> new AlunoResumo(a.getId(), a.getNome(), a.getCurso())).toList();
    }
    private Professor professor(String login) {
        return professores.findById(atual.exigir(login, "PROFESSOR").getId()).orElseThrow();
    }
}
