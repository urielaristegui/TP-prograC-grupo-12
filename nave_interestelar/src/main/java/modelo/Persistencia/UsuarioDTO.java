package modelo.Persistencia;

import java.util.Objects;

public class UsuarioDTO {
    private final String nombreUsuario;
    private final String contrasena;

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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

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
