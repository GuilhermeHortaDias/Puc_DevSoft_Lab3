package br.pucminas.moedaestudantil.merito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {
    Optional<Departamento> findByNomeAndInstituicaoId(String nome, Long instituicaoId);
}
