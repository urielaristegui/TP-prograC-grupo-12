package modelo.bitacora;

/**
 * interfaz para los observadores de la bitacora.<br>
 * define el contrato de notificacion de eventos.<br>
 */
public interface IObservadorBitacora {

    /**
     * metodo invocado cuando la bitacora registra un nuevo evento.<br>
     * <b>pre:</b> evento != null.<br>
     * <b>post:</b> notifica al observador sobre el evento registrado.<br>
     * 
     * @param evento cadena de texto con el suceso registrado.
     */
    void actualizarBitacora(String evento);
}
