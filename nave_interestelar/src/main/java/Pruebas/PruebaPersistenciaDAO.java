
package Pruebas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import modelo.Persistencia.ArchivoUsuarioDao;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDTO;

/**
 * Verifica DAO/DTO y autenticacion con un archivo temporal
 * independiente de los datos reales de la aplicacion.
 *
 * Comprueba:
 * - Registro de usuarios.
 * - Rechazo de usuarios duplicados.
 * - Busqueda por nombre.
 * - Autenticacion correcta e incorrecta.
 * - Validacion de datos de entrada.
 * - Persistencia entre instancias del DAO.
 * - Inicio y cierre de sesion.
 *
 * Pre: permisos para crear archivos temporales.
 * Post: elimina los archivos utilizados en la prueba.
 */
public final class PruebaPersistenciaDAO {

    private PruebaPersistenciaDAO() {
    }

    public static void main(String[] args)
            throws IOException {

        Path carpeta = Files.createTempDirectory(
                "prueba-persistencia-dao-"
        );

        Path archivo = carpeta.resolve(
                "usuarios_prueba.txt"
        );

        try {

            ArchivoUsuarioDao dao =
                    new ArchivoUsuarioDao(archivo);

            AutenticacionService servicio =
                    new AutenticacionService(dao);

            // =====================================
            // 1. REGISTRO DE USUARIOS
            // =====================================

            exigir(
                    Files.exists(archivo),
                    "El DAO no creo el archivo"
            );

            exigir(
                    Files.size(archivo) == 0,
                    "El archivo debe comenzar vacio"
            );

            exigir(
                    servicio.registrarUsuario(
                            "capitan_prueba", "clave-123"
                    ),
                    "No pudo registrar el primer usuario"
            );

            exigir(
                    servicio.registrarUsuario(
                            "  piloto_prueba  ", "clave-456"
                    ),
                    "No pudo registrar el segundo usuario"
            );

            exigir(
                    !servicio.registrarUsuario(
                            "capitan_prueba", "otra-clave"
                    ),
                    "Acepto un usuario duplicado"
            );

            exigir(
                    !servicio.registrarUsuario(
                            "piloto_prueba", "otra-clave"
                    ),
                    "No reconocio el duplicado "
                            + "con espacios normalizados"
            );

            System.out.println(
                    "[OK] Registro de usuarios "
                            + "y rechazo de duplicados"
            );

            // =====================================
            // 2. BUSQUEDA Y AUTENTICACION
            // =====================================

            Optional<UsuarioDTO> usuario =
                    dao.buscarPorNombreUsuario(
                            " piloto_prueba "
                    );

            exigir(
                    usuario.isPresent(),
                    "No encontro al usuario registrado"
            );

            exigir(
                    "piloto_prueba".equals(
                            usuario.get().getNombreUsuario()
                    ),
                    "No normalizo el nombre de usuario"
            );

            exigir(
                    "clave-456".equals(
                            usuario.get().getContrasena()
                    ),
                    "La contrasena recuperada "
                            + "no coincide"
            );

            exigir(
                    dao.buscarPorNombreUsuario(
                            "inexistente"
                    ).isEmpty(),
                    "Encontro un usuario inexistente"
            );

            exigir(
                    dao.autenticar(
                            "capitan_prueba", "clave-123"
                    ),
                    "No autentico credenciales correctas"
            );

            exigir(
                    !dao.autenticar(
                            "capitan_prueba", "incorrecta"
                    ),
                    "Acepto una contrasena incorrecta"
            );

            exigir(
                    !dao.autenticar(
                            "inexistente", "clave-123"
                    ),
                    "Autentico un usuario inexistente"
            );

            exigir(
                    !dao.autenticar(
                            null, "clave-123"
                    ),
                    "Acepto un usuario nulo"
            );

            // =====================================
            // 3. VALIDACION DE DTO
            // =====================================

            esperarArgumentoInvalido(
                    () -> new UsuarioDTO(
                            "  ", "clave"
                    ),
                    "Nombre de usuario vacio"
            );

            esperarArgumentoInvalido(
                    () -> new UsuarioDTO(
                            "usuario", ""
                    ),
                    "Contrasena vacia"
            );

            System.out.println(
                    "[OK] Busqueda, credenciales "
                            + "y entradas invalidas"
            );

            // =====================================
            // 4. VERIFICAR ARCHIVO
            // =====================================

            List<String> lineas =
                    Files.readAllLines(
                            archivo,
                            StandardCharsets.UTF_8
                    );

            exigir(
                    lineas.size() == 2,
                    "El archivo debe contener "
                            + "exactamente dos usuarios"
            );

            for (String linea : lineas) {

                exigir(
                        linea.split(";", -1).length == 2,
                        "Formato de registro incorrecto"
                );
            }

            String contenido =
                    Files.readString(
                            archivo,
                            StandardCharsets.UTF_8
                    );

            exigir(
                    !contenido.contains("capitan_prueba")
                            && !contenido.contains("clave-123"),
                    "Las credenciales no deben aparecer "
                            + "en texto plano"
            );

            // Base64 es una codificacion reversible.
            // No equivale a cifrado seguro.

            System.out.println(
                    "[OK] Archivo con dos registros "
                            + "y formato esperado"
            );

            // =====================================
            // 5. REABRIR ARCHIVO
            // =====================================

            ArchivoUsuarioDao daoReabierto =
                    new ArchivoUsuarioDao(archivo);

            AutenticacionService nuevoServicio =
                    new AutenticacionService(
                            daoReabierto
                    );

            exigir(
                    nuevoServicio.getUsuarioActual().isEmpty(),
                    "La nueva sesion debe comenzar vacia"
            );

            exigir(
                    !nuevoServicio.iniciarSesion(
                            "piloto_prueba", "incorrecta"
                    ),
                    "El servicio acepto una "
                            + "contrasena incorrecta"
            );

            exigir(
                    nuevoServicio.getUsuarioActual().isEmpty(),
                    "Un login fallido no debe "
                            + "crear una sesion"
            );

            exigir(
                    nuevoServicio.iniciarSesion(
                            "piloto_prueba", "clave-456"
                    ),
                    "No autentico al reabrir el archivo"
            );

            exigir(
                    nuevoServicio.getUsuarioActual().isPresent()
                            && "piloto_prueba".equals(
                            nuevoServicio
                                    .getUsuarioActual()
                                    .get()
                                    .getNombreUsuario()
                    ),
                    "El servicio no guardo "
                            + "el usuario autenticado"
            );

            // =====================================
            // 6. CERRAR SESION
            // =====================================

            nuevoServicio.cerrarSesion();

            exigir(
                    nuevoServicio.getUsuarioActual().isEmpty(),
                    "Cerrar sesion no limpio "
                            + "el usuario actual"
            );

            exigir(
                    daoReabierto.autenticar(
                            "capitan_prueba", "clave-123"
                    ),
                    "No se conservaron ambos usuarios "
                            + "al reabrir"
            );

            System.out.println(
                    "[OK] Persistencia entre instancias "
                            + "y cierre de sesion"
            );

            // =====================================
            // RESULTADO FINAL
            // =====================================

            System.out.println(
                    "\n[OK] TODAS LAS PRUEBAS "
                            + "DE PERSISTENCIA PASARON"
            );

        } finally {

            // Limpiar solamente los datos temporales
            // de esta prueba.

            Files.deleteIfExists(archivo);
            Files.deleteIfExists(carpeta);
        }
    }

    /**
     * Comprueba que una operacion rechace
     * argumentos invalidos.
     */
    private static void esperarArgumentoInvalido(
            Runnable accion,
            String caso) {

        try {

            accion.run();

        } catch (IllegalArgumentException e) {
            return;
        }

        throw new AssertionError(
                "No se rechazo: " + caso
        );
    }

    /**
     * Falla la prueba ante un resultado incorrecto.
     */
    private static void exigir(
            boolean condicion,
            String mensaje) {

        if (!condicion) {

            throw new AssertionError(
                    "ERROR: " + mensaje
            );
        }
    }
}
