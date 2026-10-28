package br.pucminas.moedaestudantil.merito;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import static br.pucminas.moedaestudantil.merito.MeritoDtos.*;

@RestController
@RequestMapping("/api")
public class MeritoController {
    private final ProfessorService professores;
    private final MoedasService moedas;
    private final CatalogoService catalogo;
    public MeritoController(ProfessorService professores, MoedasService moedas, CatalogoService catalogo) {
        this.professores = professores; this.moedas = moedas; this.catalogo = catalogo;
    }
    @GetMapping("/professores/me") public ProfessorSaida professor(Authentication auth) { return professores.consultar(auth.getName()); }
    @GetMapping("/professores/alunos") public List<AlunoResumo> alunos(Authentication auth) { return professores.alunos(auth.getName()); }
    @GetMapping("/conta") public ContaSaida conta(Authentication auth) { return moedas.consultar(auth.getName()); }
    @PostMapping("/moedas/envios") public ResponseEntity<ExtratoSaida> enviar(Authentication auth, @Valid @RequestBody EnvioEntrada entrada) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moedas.enviar(auth.getName(), entrada));
    }
    @PostMapping("/moedas/resgates") public ResponseEntity<ExtratoSaida> resgatar(Authentication auth, @Valid @RequestBody ResgateEntrada entrada) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moedas.resgatar(auth.getName(), entrada));
    }
    @GetMapping("/vantagens") public List<VantagemSaida> vantagens(Authentication auth) { return catalogo.listar(auth.getName()); }
    @PostMapping(value = "/vantagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VantagemSaida> cadastrar(Authentication auth, @RequestParam String descricao,
                                                   @RequestParam long custo, @RequestPart MultipartFile foto) {
        var vantagem = catalogo.cadastrar(auth.getName(), descricao, custo, foto);
        return ResponseEntity.created(URI.create(vantagem.fotoUrl())).body(vantagem);
    }
    @GetMapping("/vantagens/{id}/foto") public ResponseEntity<byte[]> foto(Authentication auth, @PathVariable UUID id) {
        var foto = catalogo.foto(auth.getName(), id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(foto.tipo()))
                .cacheControl(CacheControl.noStore()).body(foto.bytes());
    }
}
