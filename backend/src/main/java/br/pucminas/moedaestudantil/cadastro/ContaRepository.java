package br.pucminas.moedaestudantil.cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface ContaRepository extends JpaRepository<Conta, UUID> {
    Optional<Conta> findByTitularId(UUID titularId);
}
