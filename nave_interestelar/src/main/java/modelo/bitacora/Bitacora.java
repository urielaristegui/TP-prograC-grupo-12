package modelo.bitacora;

import java.util.ArrayList;

/**
 * clase singleton que administra la bitacora de eventos del sistema.<br>
 * actua como sujeto manteniendo su lista de observadores e implementando el metodo notificar.<br>
 *
 * <b>invariante:</b> registro != null && observadores != null.
 */
public class Bitacora {
    private ArrayList<String> registro;
    private ArrayList<IObservadorBitacora> observadores;
    private static Bitacora instance;

    /**
     * constructor privado de la clase bitacora.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> se inicializan las listas de registro y observadores.<br>
     */
    private Bitacora() {
        this.registro = new ArrayList<String>();
        this.observadores = new ArrayList<IObservadorBitacora>();
    }

    /**
     * retorna la instancia unica de la bitacora.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> garantiza la existencia e inicializacion de la instancia singleton.<br>
     *
     * @return instancia unica de la bitacora.
     */
    public static Bitacora getInstance() {
        if (instance == null) {
            instance = new Bitacora();
        }
        return instance;
    }

    /**
     * suscribe un nuevo observador a la bitacora.<br>
     * <b>pre:</b> observador != null.<br>
     * <b>post:</b> se agrega el observador a la lista si no estaba suscrito.<br>
     *
     * @param observador objeto que implementa IObservadorBitacora.
     * @throws IllegalArgumentException si observador es null.
     */
    public void agregarObservador(IObservadorBitacora observador) {
        if (observador == null) {
            throw new IllegalArgumentException("el observador no puede ser null");
        }
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * remueve un observador previamente suscrito.<br>
     * <b>pre:</b> observador != null.<br>
     * <b>post:</b> se quita el observador de la lista.<br>
     *
     * @param observador objeto a desuscribir.
     */
    public void eliminarObservador(IObservadorBitacora observador) {
        observadores.remove(observador);
    }

    /**
     * notifica a todos los observadores registrados sobre un nuevo evento.<br>
     * <b>pre:</b> mensaje != null.<br>
     * <b>post:</b> ejecuta actualizarBitacora en cada observador de la lista.<br>
     *
     * @param mensaje cadena de texto a notificar.
     */
    public void notificarObservadores(String mensaje) {
        for (IObservadorBitacora obs : observadores) {
            obs.actualizarBitacora(mensaje);
        }
    }

    /**
     * registra un mensaje en la bitacora y notifica a los observadores.<br>
     * <b>pre:</b> mensaje != null && !mensaje.trim().isEmpty().<br>
     * <b>post:</b> se agrega el mensaje al registro y se llama a notificarObservadores.<br>
     *
     * @param mensaje texto descriptivo del evento.
     * @throws IllegalArgumentException si mensaje es null o vacio.
     */
    public void registrar(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("el mensaje no puede ser null ni estar vacio");
        }
        registro.add(mensaje);
        notificarObservadores(mensaje);
    }

    /**
     * retorna el registro completo de la bitacora.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> devuelve la referencia a la lista de mensajes registrados.<br>
     *
     * @return lista de mensajes acumulados en la bitacora.
     */
    public ArrayList<String> getRegistro() {
        return registro;
    }
}
