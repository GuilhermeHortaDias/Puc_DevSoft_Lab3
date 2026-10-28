package br.pucminas.moedaestudantil.config;

import br.pucminas.moedaestudantil.merito.ImportacaoInstitucionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.nio.file.Path;

@Component
public class ImportacaoInstitucional implements ApplicationRunner {
    private final String arquivo;
    private final ImportacaoInstitucionalService importacao;
    public ImportacaoInstitucional(@Value("${app.institucional.arquivo:}") String arquivo, ImportacaoInstitucionalService importacao) {
        this.arquivo = arquivo; this.importacao = importacao;
    }
    @Override public void run(ApplicationArguments args) {
        if (!arquivo.isBlank()) importacao.importar(Path.of(arquivo).toAbsolutePath().normalize());
    }
}
