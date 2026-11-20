package modelo.Persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Optional;

public class ArchivoUsuarioDao implements UsuarioDAO {

    private static final String SEPARADOR = ";";
    private final Path archivoUsuarios;

    public ArchivoUsuarioDao() {
        this(Paths.get("datos_usuarios.txt"));
    }

    public ArchivoUsuarioDao(Path archivoUsuarios) {
        if (archivoUsuarios == null) {
            throw new IllegalArgumentException("La ruta del archivo de usuarios no puede ser nula.");
        }
        this.archivoUsuarios = archivoUsuarios;
        asegurarArchivoExistente();
    }

    private void asegurarArchivoExistente() {
        try {
            Path padre = archivoUsuarios.getParent();
            if (padre != null && !Files.exists(padre)) {
                Files.createDirectories(padre);
            }
            if (!Files.exists(archivoUsuarios)) {
                Files.createFile(archivoUsuarios);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar el archivo de usuarios: " + archivoUsuarios, e);
        }
    }

    @Override
    public synchronized boolean registrar(UsuarioDTO usuario) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (buscarPorNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
            return false;
        }

        String usuarioCodificado = codificar(usuario.getNombreUsuario());
        String claveCodificada = codificar(usuario.getContrasena());
        String linea = usuarioCodificado + SEPARADOR + claveCodificada;

        try (BufferedWriter writer = Files.newBufferedWriter(
                archivoUsuarios,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(linea);
            writer.newLine();
        }
        return true;
    }

    @Override
    public synchronized Optional<UsuarioDTO> buscarPorNombreUsuario(String nombreUsuario) throws IOException {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return Optional.empty();
        }
        String objetivo = nombreUsuario.trim();

        try (BufferedReader reader = Files.newBufferedReader(archivoUsuarios, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split(SEPARADOR, -1);
                if (partes.length == 2) {
                    String userLeido = decodificar(partes[0]);
                    if (userLeido.equals(objetivo)) {
                        String passLeida = decodificar(partes[1]);
                        return Optional.of(new UsuarioDTO(userLeido, passLeida));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized boolean autenticar(String nombreUsuario, String contrasena) throws IOException {
        if (nombreUsuario == null || contrasena == null) {
            return false;
        }
        Optional<UsuarioDTO> encontrado = buscarPorNombreUsuario(nombreUsuario);
        return encontrado.isPresent() && encontrado.get().getContrasena().equals(contrasena);
    }

    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String base64) {
        return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    }
}

