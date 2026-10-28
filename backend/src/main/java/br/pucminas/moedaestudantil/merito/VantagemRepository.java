package br.pucminas.moedaestudantil.merito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
public interface VantagemRepository extends JpaRepository<Vantagem, UUID> {
    List<Vantagem> findByEmpresaUsuarioAtivoTrueOrderByDescricao();
    List<Vantagem> findByEmpresaIdOrderByDescricao(UUID empresaId);
}
