package modelo.Persistencia;

import java.io.IOException;
import java.util.Optional;

public interface UsuarioDAO {
    boolean registrar(UsuarioDTO usuario) throws IOException;
    Optional<UsuarioDTO> buscarPorNombreUsuario(String nombreUsuario) throws IOException;
    boolean autenticar(String nombreUsuario, String contrasena) throws IOException;
}
