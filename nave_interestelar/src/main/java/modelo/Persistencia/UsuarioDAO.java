package modelo.Persistencia;

import java.io.IOException;
import java.util.Optional;

public interface UsuarioDAO {
    /**
     * registra un nuevo usuario en la fuente de persistencia.<br>
     * <b>pre:</b> usuario != null.<br>
     * <b>post:</b> si el usuario no existia previamente, lo persiste y retorna true; si ya existia, no modifica la persistencia y retorna false.<br>
     *
     * @param usuario datos del usuario a registrar. usuario != null.
     * @return true si el registro se realizo, false si el nombre de usuario ya estaba registrado.
     * @throws IOException si ocurre un error de entrada/salida al acceder a la fuente de datos.
     */
    boolean registrar(UsuarioDTO usuario) throws IOException;

    /**
     * busca un usuario por su nombre de usuario en la fuente de persistencia.<br>
     * <b>pre:</b> la fuente de datos esta disponible.<br>
     * <b>post:</b> retorna un Optional con el UsuarioDTO encontrado o Optional.empty() si no existe o nombreUsuario es invalido.<br>
     *
     * @param nombreUsuario nombre de usuario a buscar.
     * @return Optional con el usuario encontrado o vacio si no coincide ninguno.
     * @throws IOException si ocurre un error de entrada/salida al consultar la fuente de datos.
     */
    Optional<UsuarioDTO> buscarPorNombreUsuario(String nombreUsuario) throws IOException;

    /**
     * verifica si las credenciales proporcionadas corresponden a un usuario registrado.<br>
     * <b>pre:</b> la fuente de datos esta disponible.<br>
     * <b>post:</b> retorna true si existe un usuario con nombreUsuario y su contrasena coincide exactamente con contrasena; false en caso contrario.<br>
     *
     * @param nombreUsuario nombre de usuario a autenticar.
     * @param contrasena contrasena a verificar.
     * @return true si las credenciales son validas, false en caso contrario.
     * @throws IOException si ocurre un error de entrada/salida al verificar las credenciales.
     */
    boolean autenticar(String nombreUsuario, String contrasena) throws IOException;
}