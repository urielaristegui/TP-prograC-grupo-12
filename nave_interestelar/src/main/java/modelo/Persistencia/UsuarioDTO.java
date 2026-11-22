package modelo.Persistencia;

import java.util.Objects;

public class UsuarioDTO {
    private final String nombreUsuario;
    private final String contrasena;

    /**
     * construye un objeto de transferencia de datos para un usuario con sus credenciales.<br>
     * <b>pre:</b> nombreUsuario != null y no esta en blanco; contrasena != null y no esta vacia.<br>
     * <b>post:</b> inicializa nombreUsuario recortando espacios en los extremos y contrasena con el valor recibido.<br>
     *
     * @param nombreUsuario nombre del usuario.
     * @param contrasena contrasena del usuario.
     * @throws IllegalArgumentException si nombreUsuario es null o esta en blanco, o si contrasena es null o esta vacia.
     */
    public UsuarioDTO(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser vacío.");
        }
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser vacía.");
        }
        this.nombreUsuario = nombreUsuario.trim();
        this.contrasena = contrasena;
    }

    /**
     * obtiene el nombre de usuario.<br>
     * <b>pre:</b> el objeto esta inicializado.<br>
     * <b>post:</b> retorna el nombre de usuario.<br>
     *
     * @return nombre de usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * obtiene la contrasena del usuario.<br>
     * <b>pre:</b> el objeto esta inicializado.<br>
     * <b>post:</b> retorna la contrasena del usuario.<br>
     *
     * @return contrasena del usuario.
     */
    public String getContrasena() {
        return contrasena;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioDTO)) return false;
        UsuarioDTO that = (UsuarioDTO) o;
        return nombreUsuario.equals(that.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario);
    }
}