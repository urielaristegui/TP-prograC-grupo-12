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
 - Controlador MVC del ciclo de misiones.
 *
 - Conserva los informes generados durante la ejecucion
 - del programa, sin persistirlos.
 *
 - Invariantes:
 - - asistente != null.
 - - bitacora != null.
 - - listener != null.
 - - Solo una mision puede estar ejecutandose.
 - - El historial contiene informes no nulos.
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

    // Historial en memoria. No se persiste.
    private final List<InformeMision> historial =
            new ArrayList<>();

    private volatile boolean ejecutando;

    /**
     * construye un controlador de misiones para pruebas independientes utilizando la nave del asistente.<br>
     * <b>pre:</b> asistente != null, bitacora != null, listener != null.<br>
     * <b>post:</b> inicializa el controlador con proveedorNave en null.<br>
     *
     * @param asistente asistente de comandos. asistente != null.
     * @param bitacora bitacora de eventos. bitacora != null.
     * @param listener oyente de eventos de mision. listener != null.
     */
    public MisionController(
            AsistenteDeComandos asistente,
            Bitacora bitacora,
            MisionListener listener) {

        this(asistente, bitacora, listener, null);
    }

    /**
     * construye un controlador de misiones para la ventana principal.<br>
     * <b>pre:</b> asistente != null, bitacora != null, listener != null.<br>
     * <b>post:</b> conserva las dependencias y el proveedor de nave si existe.<br>
     *
     * @param asistente asistente de comandos. asistente != null.
     * @param bitacora bitacora de eventos. bitacora != null.
     * @param listener oyente de eventos de mision. listener != null.
     * @param proveedorNave proveedor de la nave activa, o null si no aplica.
     * @throws IllegalArgumentException si asistente, bitacora o listener son null.
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
     * devuelve el historial de informes generados en memoria.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna una copia inmodificable del historial en orden cronologico sin alterar los originales.<br>
     *
     * @return lista inmodificable de informes de esta ejecucion.
     */
    public List<InformeMision> obtenerHistorial() {

        return List.copyOf(historial);
    }

    /**
     * consulta el estado de la nave seleccionada.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> sincroniza la nave en el asistente si hay proveedor y retorna la instantanea del estado sin modificar recursos.<br>
     *
     * @return descripcion del estado de la nave.
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
     * solicita ejecutar una mision de forma asincrona.<br>
     * <b>pre:</b> tipo y parametros validos, nave creada y operativa, y tripulacion valida cuando existe proveedorNave.<br>
     * <b>post:</b> rechaza ejecuciones simultaneas; si es valido, ejecuta la mision fuera del edt, guarda el informe si se genera y notifica a la vista en el edt.<br>
     *
     * @param indiceMision tipo de mision (0: intercepcion, 1: recoleccion, 2: retorno).
     * @param combustible transferencia de combustible para m-01.
     * @param energia transferencia de energia para m-01.
     * @param gemas recompensa de gemas para m-02.
     */
    public void ejecutarMision(
            int indiceMision,
            int combustible,
            int energia,
            int gemas) {

        // =====================================
        // VALIDAR EJECUCION SIMULTANEA
        // =====================================

        if (ejecutando) {

            listener.onErrorParametro(
                    "Ya hay una mision en ejecucion"
            );

            return;
        }

        // =====================================
        // VALIDAR NAVE CREADA
        // =====================================

        if (proveedorNave != null) {

            Nave naveActual = proveedorNave.get();

            if (naveActual == null) {

                listener.onErrorParametro(
                        "Primero crea una nave en "
                                + "Configuracion & Creacion"
                );

                return;
            }

            if (naveActual.estaDestruida()) {

                listener.onErrorParametro(
                        "La nave esta destruida "
                                + "y no puede iniciar misiones"
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

        // =====================================
        // CREAR MISION
        // =====================================

        final Mision mision;

        try {

            mision = crearMision(
                    indiceMision,
                    combustible,
                    energia,
                    gemas
            );

        } catch (IllegalArgumentException ex) {

            listener.onErrorParametro(
                    ex.getMessage()
            );

            return;
        }

        // =====================================
        // EJECUTAR FUERA DEL EDT
        // =====================================

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
                    new ArrayList<>(
                            bitacora.getRegistro()
                    );

            final String estado =
                    asistente.snapshotEstadoNave();

            // =====================================
            // ACTUALIZAR SWING
            // =====================================

            SwingUtilities.invokeLater(() -> {

                ejecutando = false;

                // NUEVO: guardar el informe.
                if (informeFinal != null) {

                    historial.add(informeFinal);
                }

                listener.onMisionFinalizada(
                        resultado,
                        informeFinal,
                        eventos,
                        estado
                );

                if (mensajeError != null) {

                    listener.onErrorParametro(
                            "La mision fallo: "
                                    + mensajeError
                    );
                }
            });

        }, "Hilo-Mision-Controller");

        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * construye la instancia de mision correspondiente al indice indicado.<br>
     * <b>pre:</b> indiceMision valido (0, 1 o 2) y parametros validos para la mision seleccionada.<br>
     * <b>post:</b> retorna una nueva instancia de Mision o lanza IllegalArgumentException si el indice o parametros no son validos.<br>
     *
     * @param indiceMision tipo de mision a crear.
     * @param combustible transferencia de combustible para m-01.
     * @param energia transferencia de energia para m-01.
     * @param gemas recompensa de gemas para m-02.
     * @return nueva instancia de mision creada.
     * @throws IllegalArgumentException si indiceMision no es valido o los parametros no cumplen las restricciones de la mision.
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