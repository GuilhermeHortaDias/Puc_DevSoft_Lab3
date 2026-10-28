package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.EmpresaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import static br.pucminas.moedaestudantil.merito.MeritoDtos.*;

@Service
@Transactional(readOnly = true)
public class CatalogoService {
    private final UsuarioAtual atual;
    private final EmpresaRepository empresas;
    private final VantagemRepository vantagens;
    public CatalogoService(UsuarioAtual atual, EmpresaRepository empresas, VantagemRepository vantagens) {
        this.atual = atual; this.empresas = empresas; this.vantagens = vantagens;
    }
    public List<VantagemSaida> listar(String login) {
        var usuario = atual.exigir(login, "ALUNO", "EMPRESA");
        var lista = "EMPRESA".equals(usuario.getPerfil()) ? vantagens.findByEmpresaIdOrderByDescricao(usuario.getId())
                : vantagens.findByEmpresaUsuarioAtivoTrueOrderByDescricao();
        return lista.stream().map(this::saida).toList();
    }
    @Transactional
    public VantagemSaida cadastrar(String login, String descricao, long custo, MultipartFile arquivo) {
        var usuario = atual.exigir(login, "EMPRESA");
        if (descricao == null || descricao.isBlank() || descricao.strip().length() > 2000 || custo <= 0) {
            throw erro(HttpStatus.BAD_REQUEST, "Informe descrição de até 2.000 caracteres e custo inteiro positivo.");
        }
        if (arquivo == null || arquivo.isEmpty() || arquivo.getSize() > 2 * 1024 * 1024) {
            throw erro(HttpStatus.BAD_REQUEST, "Adicione uma foto PNG ou JPEG de até 2 MB.");
        }
        try {
            byte[] foto = arquivo.getBytes();
            String tipo;
            try (var imagem = ImageIO.createImageInputStream(new ByteArrayInputStream(foto))) {
                var leitores = ImageIO.getImageReaders(imagem);
                if (!leitores.hasNext()) throw erro(HttpStatus.BAD_REQUEST, "Foto inválida. Use PNG ou JPEG.");
                var leitor = leitores.next();
                try {
                    leitor.setInput(imagem);
                    tipo = switch (leitor.getFormatName().toLowerCase(java.util.Locale.ROOT)) {
                        case "png" -> "image/png";
                        case "jpeg", "jpg" -> "image/jpeg";
                        default -> throw erro(HttpStatus.BAD_REQUEST, "Formato de foto não aceito. Use PNG ou JPEG.");
                    };
                    long pixels = (long) leitor.getWidth(0) * leitor.getHeight(0);
                    if (pixels <= 0 || pixels > 8_000_000) throw erro(HttpStatus.BAD_REQUEST, "Use uma imagem com até 8 megapixels.");
                    leitor.read(0);
                } finally { leitor.dispose(); }
            }
            var empresa = empresas.findById(usuario.getId()).orElseThrow();
            return saida(vantagens.saveAndFlush(new Vantagem(empresa, descricao.strip(), custo, foto, tipo)));
        } catch (IOException exception) {
            throw erro(HttpStatus.BAD_REQUEST, "Não foi possível ler a foto. Use PNG ou JPEG válido.");
        }
    }
    public FotoSaida foto(String login, UUID id) {
        var usuario = atual.exigir(login, "ALUNO", "EMPRESA");
        var vantagem = vantagens.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Vantagem não encontrada."));
        if ("EMPRESA".equals(usuario.getPerfil()) && !vantagem.getEmpresa().getId().equals(usuario.getId())) {
            throw erro(HttpStatus.FORBIDDEN, "Você pode acessar somente suas próprias ofertas.");
        }
        if (!vantagem.getEmpresa().getUsuario().isAtivo()) throw erro(HttpStatus.NOT_FOUND, "Vantagem indisponível.");
        return new FotoSaida(vantagem.getTipoFoto(), vantagem.getFoto());
    }
    private VantagemSaida saida(Vantagem vantagem) {
        return new VantagemSaida(vantagem.getId(), vantagem.getEmpresa().getId(), vantagem.getEmpresa().getNome(),
                vantagem.getDescricao(), vantagem.getCusto(), "/api/vantagens/" + vantagem.getId() + "/foto");
    }
    private ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
