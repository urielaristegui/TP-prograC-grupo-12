package vista;

import java.awt.event.ActionListener;

public interface IVisorReconditoVista {

    /**
     * Registra el manejador de eventos del controlador para el botón de agregar generadoras.
     */
    void setAgregarGeneradoraListener(ActionListener listener);

    /**
     * Registra el manejador de eventos del controlador para el botón de agregar naves consumidoras.
     */
    void setAgregarNaveListener(ActionListener listener);

    /**
     * Imprime un mensaje en la consola de eventos del visor recóndito.
     */
    void mostrarMensajeLog(String mensaje);

    /**
     * Actualiza el estado visual de la barra de capacidad de la plataforma de carga.
     */
    void actualizarCapacidad(int bateriasActuales, int capacidadMaxima);

    /**
     * Permite habilitar o deshabilitar los componentes de interacción.
     */
    void habilitarControles(boolean habilitado);
}