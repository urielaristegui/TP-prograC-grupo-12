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

    /**
     * construye un dao de usuarios por defecto apuntando a datos_usuarios.txt.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> inicializa archivoUsuarios con "datos_usuarios.txt" y asegura que el archivo exista.<br>
     */
    public ArchivoUsuarioDao() {
        this(Paths.get("datos_usuarios.txt"));
    }

    /**
     * construye un dao de usuarios con la ruta de archivo indicada.<br>
     * <b>pre:</b> archivoUsuarios != null.<br>
     * <b>post:</b> inicializa archivoUsuarios y asegura que existan los directorios padres y el archivo.<br>
     *
     * @param archivoUsuarios ruta del archivo de usuarios. archivoUsuarios != null.
     * @throws IllegalArgumentException si archivoUsuarios es null.
     */
    public ArchivoUsuarioDao(Path archivoUsuarios) {
        if (archivoUsuarios == null) {
            throw new IllegalArgumentException("La ruta del archivo de usuarios no puede ser nula.");
        }
        this.archivoUsuarios = archivoUsuarios;
        asegurarArchivoExistente();
    }

    /**
     * crea los directorios padres y el archivo de usuarios si no existen.<br>
     * <b>pre:</b> archivoUsuarios != null.<br>
     * <b>post:</b> el directorio padre (si aplica) y el archivo de usuarios existen en disco.<br>
     *
     * @throws IllegalStateException si ocurre un error de entrada/salida al crear directorios o el archivo.
     */
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

    /**
     * codifica un texto plano a base64 utilizando utf-8.<br>
     * <b>pre:</b> texto != null.<br>
     * <b>post:</b> retorna la cadena codificada en base64 sin modificar el estado.<br>
     *
     * @param texto cadena a codificar. texto != null.
     * @return representacion en base64 del texto.
     */
    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * decodifica una cadena en base64 a texto plano utilizando utf-8.<br>
     * <b>pre:</b> base64 != null y contiene una secuencia base64 valida.<br>
     * <b>post:</b> retorna la cadena decodificada en utf-8 sin modificar el estado.<br>
     *
     * @param base64 cadena codificada en base64. base64 != null.
     * @return texto plano decodificado.
     */
    private String decodificar(String base64) {
        return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    }
}