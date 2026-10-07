package br.pucminas.moedaestudantil.cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface EmpresaRepository extends JpaRepository<EmpresaParceira, UUID> {}
