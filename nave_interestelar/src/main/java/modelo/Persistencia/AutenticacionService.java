package modelo.Persistencia;

import java.io.IOException;
import java.util.Optional;

public class AutenticacionService {

    private final UsuarioDAO usuarioDAO;
    private UsuarioDTO usuarioActual;

    public AutenticacionService(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException("El DAO de usuarios no puede ser nulo.");
        }
        this.usuarioDAO = usuarioDAO;
    }

    public boolean registrarUsuario(String nombreUsuario, String contrasena) throws IOException {
        UsuarioDTO nuevoUsuario = new UsuarioDTO(nombreUsuario, contrasena);
        return usuarioDAO.registrar(nuevoUsuario);
    }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) throws IOException {
        if (usuarioDAO.autenticar(nombreUsuario, contrasena)) {
            Optional<UsuarioDTO> dto = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
            this.usuarioActual = dto.orElse(null);
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Optional<UsuarioDTO> getUsuarioActual() {
        return Optional.ofNullable(usuarioActual);
    }
}
