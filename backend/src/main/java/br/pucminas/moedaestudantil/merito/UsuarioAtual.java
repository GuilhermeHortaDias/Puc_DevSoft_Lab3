package br.pucminas.moedaestudantil.merito;

import br.pucminas.moedaestudantil.cadastro.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Arrays;

@Service
@Transactional(readOnly = true)
public class UsuarioAtual {
    private final UsuarioRepository usuarios;
    public UsuarioAtual(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    public Usuario exigir(String login, String... perfis) {
        var usuario = usuarios.findByLogin(login)
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cadastro inativo ou sessão inválida."));
        if (perfis.length > 0 && Arrays.stream(perfis).noneMatch(p -> p.equals(usuario.getPerfil()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operação não permitida para seu perfil.");
        }
        return usuario;
    }
}
