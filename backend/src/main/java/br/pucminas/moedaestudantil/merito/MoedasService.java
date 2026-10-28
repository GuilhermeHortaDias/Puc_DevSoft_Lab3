package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static br.pucminas.moedaestudantil.merito.MeritoDtos.*;

@Service
@Transactional(readOnly = true)
public class MoedasService {
    private final UsuarioAtual atual;
    private final ContaRepository contas;
    private final MovimentacaoRepository movimentos;
    private final ProfessorRepository professores;
    private final AlunoRepository alunos;
    private final VantagemRepository vantagens;
    private final NotificacaoRepository notificacoes;
    public MoedasService(UsuarioAtual atual, ContaRepository contas, MovimentacaoRepository movimentos,
                         ProfessorRepository professores, AlunoRepository alunos, VantagemRepository vantagens,
                         NotificacaoRepository notificacoes) {
        this.atual = atual; this.contas = contas; this.movimentos = movimentos; this.professores = professores;
        this.alunos = alunos; this.vantagens = vantagens; this.notificacoes = notificacoes;
    }
    public ContaSaida consultar(String login) {
        var usuario = atual.exigir(login, "ALUNO", "PROFESSOR");
        var conta = contas.findByTitularId(usuario.getId()).orElseThrow();
        return new ContaSaida(conta.getSaldo(), movimentos.extrato(usuario.getId()).stream().map(m -> saida(m, usuario.getId())).toList());
    }
    @Transactional
    public ExtratoSaida enviar(String login, EnvioEntrada entrada) {
        var usuario = atual.exigir(login, "PROFESSOR");
        var anterior = movimentos.findBySolicitanteIdAndRequisicaoId(usuario.getId(), entrada.requisicaoId());
        if (anterior.isPresent()) return repetirEnvio(anterior.get(), entrada, usuario.getId());
        var professor = professores.findById(usuario.getId()).orElseThrow();
        var aluno = alunos.findById(entrada.alunoId())
                .filter(a -> a.getUsuario().isAtivo())
                .orElseThrow(() -> erro(HttpStatus.BAD_REQUEST, "Selecione um aluno ativo."));
        if (!professor.getInstituicao().getId().equals(aluno.getInstituicao().getId())) {
            throw erro(HttpStatus.BAD_REQUEST, "Selecione um aluno ativo da sua instituição. Recarregue a lista se o vínculo foi alterado.");
        }
        var bloqueadas = contas.bloquearTitulares(List.of(usuario.getId(), aluno.getId()));
        var origem = conta(bloqueadas, usuario.getId());
        var destino = conta(bloqueadas, aluno.getId());
        anterior = movimentos.findBySolicitanteIdAndRequisicaoId(usuario.getId(), entrada.requisicaoId());
        if (anterior.isPresent()) return repetirEnvio(anterior.get(), entrada, usuario.getId());
        if (entrada.quantidade() <= 0 || entrada.quantidade() > origem.getSaldo()) {
            throw erro(HttpStatus.BAD_REQUEST, "Saldo insuficiente ou quantidade inválida.");
        }
        creditar(destino, entrada.quantidade());
        origem.debitar(entrada.quantidade());
        var movimento = movimentos.save(Movimentacao.reconhecimento(origem, destino, entrada.quantidade(),
                entrada.motivo().strip(), entrada.requisicaoId()));
        notificacoes.save(new NotificacaoEmail(movimento, aluno.getEmail(), "Você recebeu moedas estudantis", "Olá, " + aluno.getNome()
                + ".\n\nVocê recebeu " + entrada.quantidade() + " moedas de " + professor.getNome()
                + ".\nMotivo: " + entrada.motivo().strip() + "\n\nConsulte seu saldo e extrato no sistema."));
        movimentos.flush();
        return saida(movimento, usuario.getId());
    }
    @Transactional
    public ExtratoSaida resgatar(String login, ResgateEntrada entrada) {
        var usuario = atual.exigir(login, "ALUNO");
        var anterior = movimentos.findBySolicitanteIdAndRequisicaoId(usuario.getId(), entrada.requisicaoId());
        if (anterior.isPresent()) return repetirResgate(anterior.get(), entrada, usuario.getId());
        var origem = conta(contas.bloquearTitulares(List.of(usuario.getId())), usuario.getId());
        anterior = movimentos.findBySolicitanteIdAndRequisicaoId(usuario.getId(), entrada.requisicaoId());
        if (anterior.isPresent()) return repetirResgate(anterior.get(), entrada, usuario.getId());
        var vantagem = vantagens.findById(entrada.vantagemId())
                .filter(v -> v.getEmpresa().getUsuario().isAtivo())
                .orElseThrow(() -> erro(HttpStatus.BAD_REQUEST, "Vantagem indisponível."));
        if (vantagem.getCusto() > origem.getSaldo()) throw erro(HttpStatus.BAD_REQUEST, "Saldo insuficiente para resgatar esta vantagem.");
        origem.debitar(vantagem.getCusto());
        var movimento = movimentos.save(Movimentacao.resgate(origem, vantagem, entrada.requisicaoId()));
        var aluno = alunos.findById(usuario.getId()).orElseThrow();
        String corpo = "Cupom para troca presencial\n\nCódigo: " + movimento.getCupomCodigo()
                + "\nAluno: " + aluno.getNome() + "\nEmpresa: " + vantagem.getEmpresa().getNome()
                + "\nVantagem: " + movimento.getDescricaoVantagem() + "\nCusto: " + movimento.getQuantidade()
                + " moedas\n\nApresente este código na empresa para conferência presencial.";
        notificacoes.save(new NotificacaoEmail(movimento, aluno.getEmail(), "Seu cupom de vantagem estudantil", corpo));
        notificacoes.save(new NotificacaoEmail(movimento, vantagem.getEmpresa().getEmail(), "Cupom resgatado na sua empresa", corpo));
        movimentos.flush();
        return saida(movimento, usuario.getId());
    }
    private ExtratoSaida repetirEnvio(Movimentacao m, EnvioEntrada entrada, UUID usuario) {
        if (!"RECONHECIMENTO".equals(m.getTipo()) || m.getQuantidade() != entrada.quantidade()
                || !m.getDestino().getTitular().getId().equals(entrada.alunoId())
                || !Objects.equals(m.getMotivo(), entrada.motivo().strip())) throw conflito();
        return saida(m, usuario);
    }
    private ExtratoSaida repetirResgate(Movimentacao m, ResgateEntrada entrada, UUID usuario) {
        if (!"RESGATE".equals(m.getTipo()) || !m.getVantagem().getId().equals(entrada.vantagemId())) throw conflito();
        return saida(m, usuario);
    }
    private Conta conta(List<Conta> lista, UUID titular) {
        return lista.stream().filter(c -> c.getTitular().getId().equals(titular)).findFirst()
                .orElseThrow(() -> erro(HttpStatus.CONFLICT, "Conta de moedas não encontrada."));
    }
    private void creditar(Conta conta, long quantidade) {
        try { conta.creditar(quantidade); }
        catch (ArithmeticException e) { throw erro(HttpStatus.CONFLICT, "O saldo de destino excederia o limite suportado."); }
    }
    private ExtratoSaida saida(Movimentacao m, UUID usuario) {
        boolean entrada = m.getDestino() != null && m.getDestino().getTitular().getId().equals(usuario);
        String descricao;
        String contraparte = null;
        switch (m.getTipo()) {
            case "CREDITO_SEMESTRAL" -> descricao = "Crédito do " + m.getSemestre() + "º semestre de " + m.getAno();
            case "RECONHECIMENTO" -> {
                descricao = entrada ? "Reconhecimento recebido" : "Reconhecimento enviado";
                contraparte = entrada ? professores.findById(m.getOrigem().getTitular().getId()).orElseThrow().getNome()
                        : alunos.findById(m.getDestino().getTitular().getId()).orElseThrow().getNome();
            }
            case "RESGATE" -> { descricao = m.getDescricaoVantagem(); contraparte = m.getVantagem().getEmpresa().getNome(); }
            default -> throw new IllegalStateException("Tipo de movimentação desconhecido.");
        }
        return new ExtratoSaida(m.getId(), m.getTipo(), m.getDataHora(), m.getQuantidade(), entrada ? "ENTRADA" : "SAIDA",
                descricao, contraparte, m.getMotivo(), m.getCupomCodigo());
    }
    private ResponseStatusException conflito() { return erro(HttpStatus.CONFLICT, "Identificação já utilizada para outra operação. Atualize os dados antes de tentar novamente."); }
    private ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
