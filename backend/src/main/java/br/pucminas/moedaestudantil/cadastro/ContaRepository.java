package br.pucminas.moedaestudantil.cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
public interface ContaRepository extends JpaRepository<Conta, UUID> {
    Optional<Conta> findByTitularId(UUID titularId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conta c where c.titular.id in :titulares order by c.id")
    List<Conta> bloquearTitulares(List<UUID> titulares);
}
