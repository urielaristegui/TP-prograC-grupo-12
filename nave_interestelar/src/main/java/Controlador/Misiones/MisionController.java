
package Controlador.Misiones;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.InformeMision;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.misiones.Recoleccion;
import modelo.misiones.Retorno;
import modelo.nave.Nave;

/**
 * Controlador MVC del ciclo de misiones.
 *
 * Invariantes:
 * - asistente, bitacora y listener no son null.
 * - Solo se crea un hilo de mision por solicitud validada.
 * - Si se proporciona un proveedor, obtiene la nave real
 *   creada por el usuario, no la nave temporal del inicio.
 */
public class MisionController {

    public interface MisionListener {

        void onMisionIniciada();

        void onMisionFinalizada(
                boolean pasoPreparacion,
                InformeMision informe,
                List<String> eventosBitacora,
                String estadoNave
        );

        void onErrorParametro(String mensaje);
    }

    private final AsistenteDeComandos asistente;
    private final Bitacora bitacora;
    private final MisionListener listener;
    private final Supplier<Nave> proveedorNave;

    private boolean ejecutando;

    /**
     * Constructor original, conservado para pruebas
     * y paneles existentes.
     *
     * Pre: argumentos no null.
     * Post: opera con la nave del asistente.
     */
    public MisionController(
            AsistenteDeComandos asistente,
            Bitacora bitacora,
            MisionListener listener) {

        this(asistente, bitacora, listener, null);
    }

    /**
     * Constructor para la ventana principal.
     *
     * Pre:
     * - asistente != null.
     * - bitacora != null.
     * - listener != null.
     *
     * Post:
     * - Conserva el proveedor de nave.
     * - Las misiones utilizaran la nave creada.
     *
     * @param proveedorNave devuelve la nave creada
     *                      o null antes de crearla
     */
    public MisionController(
            AsistenteDeComandos asistente,
            Bitacora bitacora,
            MisionListener listener,
            Supplier<Nave> proveedorNave) {

        if (asistente == null
                || bitacora == null
                || listener == null) {

            throw new IllegalArgumentException(
                    "Asistente, bitacora y listener son obligatorios"
            );
        }

        this.asistente = asistente;
        this.bitacora = bitacora;
        this.listener = listener;
        this.proveedorNave = proveedorNave;
    }

    /**
     * Obtiene el estado de la nave seleccionada.
     *
     * Pre: ninguna.
     *
     * Post:
     * - Si hay nave creada, sincroniza el asistente.
     * - No modifica los recursos.
     *
     * @return descripcion del estado actual
     */
    public String obtenerEstadoNave() {

        if (proveedorNave != null) {

            Nave naveActual = proveedorNave.get();

            if (naveActual == null) {
                return "Primero crea una nave";
            }

            asistente.setNave(naveActual);
        }

        return asistente.snapshotEstadoNave();
    }

    /**
     * Solicita ejecutar una mision.
     *
     * Pre:
     * - Parametros de mision validos.
     * - Nave creada y tripulada cuando se usa
     *   el proveedor de la ventana principal.
     *
     * Post:
     * - Valida la nave.
     * - Crea una mision del tipo solicitado.
     * - Ejecuta la mision fuera del EDT.
     * - Notifica a la vista el resultado.
     *
     * @param indiceMision tipo de mision
     * @param combustible combustible a transferir en M-01
     * @param energia energia a transferir en M-01
     * @param gemas recompensa de M-02
     */
    public void ejecutarMision(
            int indiceMision,
            int combustible,
            int energia,
            int gemas) {

        if (ejecutando) {

            listener.onErrorParametro(
                    "Ya hay una mision en ejecucion"
            );

            return;
        }

        // ==========================================
        // SINCRONIZAR NAVE DEL JUGADOR
        // ==========================================

        if (proveedorNave != null) {

            Nave naveActual = proveedorNave.get();

            if (naveActual == null) {

                listener.onErrorParametro(
                        "Primero crea una nave en Configuracion & Creacion"
                );

                return;
            }

            if (naveActual.estaDestruida()) {

                listener.onErrorParametro(
                        "La nave esta destruida y no puede iniciar misiones"
                );

                return;
            }

            if (!naveActual.tieneTripulacionValida()) {

                listener.onErrorParametro(
                        "Necesitas al menos 5 tripulantes, "
                                + "incluyendo un Capitan"
                );

                return;
            }

            asistente.setNave(naveActual);
        }

        // ==========================================
        // CREAR MISION
        // ==========================================

        final Mision mision;

        try {

            mision = crearMision(
                    indiceMision,
                    combustible,
                    energia,
                    gemas
            );

        } catch (IllegalArgumentException ex) {

            listener.onErrorParametro(ex.getMessage());

            return;
        }

        // ==========================================
        // EJECUTAR EN HILO INDEPENDIENTE
        // ==========================================

        ejecutando = true;

        listener.onMisionIniciada();

        Thread hilo = new Thread(() -> {

            boolean pasoPreparacion = false;
            InformeMision informe = null;
            String error = null;

            try {

                pasoPreparacion = mision.director();
                informe = mision.getInforme();

            } catch (RuntimeException ex) {

                error = ex.getMessage() == null
                        ? ex.getClass().getSimpleName()
                        : ex.getMessage();
            }

            final boolean resultado = pasoPreparacion;
            final InformeMision informeFinal = informe;
            final String mensajeError = error;

            final List<String> eventos =
                    new ArrayList<>(bitacora.getRegistro());

            final String estado =
                    asistente.snapshotEstadoNave();

            // Actualizar la interfaz en el EDT.
            SwingUtilities.invokeLater(() -> {

                ejecutando = false;

                listener.onMisionFinalizada(
                        resultado,
                        informeFinal,
                        eventos,
                        estado
                );

                if (mensajeError != null) {

                    listener.onErrorParametro(
                            "La mision fallo: " + mensajeError
                    );
                }
            });

        }, "Hilo-Mision-Controller");

        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Construye la mision seleccionada.
     *
     * Pre: indiceMision entre 0 y 2.
     * Post: devuelve una nueva instancia de mision.
     *
     * @return mision correspondiente
     */
    private Mision crearMision(
            int indiceMision,
            int combustible,
            int energia,
            int gemas) {

        return switch (indiceMision) {

            case 0 -> new Intercepcion(
                    asistente,
                    combustible,
                    energia
            );

            case 1 -> new Recoleccion(
                    asistente,
                    gemas
            );

            case 2 -> new Retorno(asistente);

            default -> throw new IllegalArgumentException(
                    "Tipo de mision no valido"
            );
        };
    }
}
