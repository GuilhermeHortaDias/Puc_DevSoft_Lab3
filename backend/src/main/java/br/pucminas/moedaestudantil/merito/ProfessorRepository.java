package br.pucminas.moedaestudantil.merito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    List<Professor> findByUsuarioAtivoTrue();
    boolean existsByCpf(String cpf);
}
