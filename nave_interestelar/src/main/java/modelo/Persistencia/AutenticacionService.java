package modelo.Persistencia;

import java.io.IOException;
import java.util.Optional;

public class AutenticacionService {

    private final UsuarioDAO usuarioDAO;
    private UsuarioDTO usuarioActual;

    /**
     * construye el servicio de autenticacion asociandolo con un dao de usuarios.<br>
     * <b>pre:</b> usuarioDAO != null.<br>
     * <b>post:</b> se inicializa usuarioDAO y usuarioActual queda en null.<br>
     *
     * @param usuarioDAO dao encargado de la persistencia de usuarios. usuarioDAO != null.
     * @throws IllegalArgumentException si usuarioDAO es null.
     */
    public AutenticacionService(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException("El DAO de usuarios no puede ser nulo.");
        }
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * registra un nuevo usuario a traves del dao de persistencia.<br>
     * <b>pre:</b> usuarioDAO != null.<br>
     * <b>post:</b> crea un UsuarioDTO con nombreUsuario y contrasena y retorna el resultado de usuarioDAO.registrar(nuevoUsuario).<br>
     *
     * @param nombreUsuario nombre del usuario a registrar.
     * @param contrasena contrasena del usuario a registrar.
     * @return true si el registro fue exitoso, false si ya existia.
     * @throws IOException si ocurre un error de entrada/salida al persistir el usuario.
     */
    public boolean registrarUsuario(String nombreUsuario, String contrasena) throws IOException {
        UsuarioDTO nuevoUsuario = new UsuarioDTO(nombreUsuario, contrasena);
        return usuarioDAO.registrar(nuevoUsuario);
    }

    /**
     * autentica las credenciales del usuario y, si son validas, establece la sesion actual.<br>
     * <b>pre:</b> usuarioDAO != null.<br>
     * <b>post:</b> si autenticar retorna true, actualiza usuarioActual con el usuario encontrado (o null si no se recupera) y retorna true; si falla, no modifica usuarioActual y retorna false.<br>
     *
     * @param nombreUsuario nombre de usuario a autenticar.
     * @param contrasena contrasena a verificar.
     * @return true si las credenciales son validas, false en caso contrario.
     * @throws IOException si ocurre un error de entrada/salida al consultar el dao.
     */
    public boolean iniciarSesion(String nombreUsuario, String contrasena) throws IOException {
        if (usuarioDAO.autenticar(nombreUsuario, contrasena)) {
            Optional<UsuarioDTO> dto = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
            this.usuarioActual = dto.orElse(null);
            return true;
        }
        return false;
    }

    /**
     * cierra la sesion activa limpiando el usuario actual.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> usuarioActual pasa a ser null.<br>
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    /**
     * devuelve el usuario actualmente autenticado envuelto en un Optional.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna Optional.ofNullable(usuarioActual).<br>
     *
     * @return Optional con el usuario actual o Optional.empty() si no hay sesion iniciada.
     */
    public Optional<UsuarioDTO> getUsuarioActual() {
        return Optional.ofNullable(usuarioActual);
    }
}