package br.pucminas.moedaestudantil.merito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;
import java.util.List;
import java.time.Instant;
public interface NotificacaoRepository extends JpaRepository<NotificacaoEmail, UUID> {
    @Query(value = "select * from notificacao where estado <> 'ENVIADA' and proxima_tentativa <= :agora order by proxima_tentativa limit 10 for update skip locked", nativeQuery = true)
    List<NotificacaoEmail> pendentes(Instant agora);
}
