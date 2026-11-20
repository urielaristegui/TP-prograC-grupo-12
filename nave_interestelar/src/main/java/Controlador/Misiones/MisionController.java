package Controlador.Misiones;

import java.util.List;
import javax.swing.SwingUtilities;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.misiones.InformeMision;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.misiones.Recoleccion;
import modelo.misiones.Retorno;

public class MisionController {

    public interface MisionListener {
        void onMisionIniciada();
        void onMisionFinalizada(boolean pasoPreparacion, InformeMision informe,
                                List<String> eventosBitacora, String estadoNave);
        void onErrorParametro(String mensaje);
    }

    private final AsistenteDeComandos asistente;
    private final Bitacora bitacora;
    private final MisionListener listener;

    public MisionController(AsistenteDeComandos asistente, Bitacora bitacora, MisionListener listener) {
        if (asistente == null) {
            throw new IllegalArgumentException("El asistente de comandos no puede ser null");
        }
        if (bitacora == null) {
            throw new IllegalArgumentException("La bitácora no puede ser null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("El listener de vista no puede ser null");
        }
        this.asistente = asistente;
        this.bitacora = bitacora;
        this.listener = listener;
    }

    public String obtenerEstadoNave() {
        return asistente.snapshotEstadoNave();
    }

    public void ejecutarMision(int indiceMision, int combustible, int energia, int gemas) {
        final Mision mision;
        try {
            mision = crearMision(indiceMision, combustible, energia, gemas);
        } catch (IllegalArgumentException ex) {
            listener.onErrorParametro(ex.getMessage());
            return;
        }

        listener.onMisionIniciada();

        Thread hilo = new Thread(() -> {
            boolean pasoPreparacion = mision.director();
            InformeMision informe = mision.getInforme();
            List<String> eventos = bitacora.getRegistro();
            String estadoFinal = asistente.snapshotEstadoNave();

            SwingUtilities.invokeLater(() ->
                    listener.onMisionFinalizada(pasoPreparacion, informe, eventos, estadoFinal));
        }, "Hilo-Mision-Controller");

        hilo.setDaemon(true);
        hilo.start();
    }

    private Mision crearMision(int indiceMision, int combustible, int energia, int gemas) {
        switch (indiceMision) {
            case 0:
                return new Intercepcion(asistente, combustible, energia);
            case 1:
                return new Recoleccion(asistente, gemas);
            case 2:
                return new Retorno(asistente);
            default:
                throw new IllegalArgumentException("Tipo de misión no válido");
        }
    }
}
