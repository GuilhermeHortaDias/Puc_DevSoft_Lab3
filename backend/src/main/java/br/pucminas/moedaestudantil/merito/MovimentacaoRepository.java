package br.pucminas.moedaestudantil.merito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
    Optional<Movimentacao> findBySolicitanteIdAndRequisicaoId(UUID solicitanteId, UUID requisicaoId);
    boolean existsByDestinoIdAndTipoAndAnoAndSemestre(UUID destinoId, String tipo, Integer ano, Integer semestre);
    @Query("select m from Movimentacao m left join m.origem o left join m.destino d where o.titular.id = :titular or d.titular.id = :titular order by m.dataHora desc, m.id desc")
    List<Movimentacao> extrato(UUID titular);
}
