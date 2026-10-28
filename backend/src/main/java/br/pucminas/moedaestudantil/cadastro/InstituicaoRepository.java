package br.pucminas.moedaestudantil.cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    Optional<Instituicao> findByNome(String nome);
}
