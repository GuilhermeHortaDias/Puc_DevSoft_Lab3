package br.pucminas.moedaestudantil.cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AlunoRepository extends JpaRepository<Aluno, UUID> {
    boolean existsByCpfAndIdNot(String cpf, UUID id);
}
