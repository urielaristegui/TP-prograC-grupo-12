package modelo.UniversoAlterno;

import java.util.ArrayList;
import java.util.List;

public class PlataformaDeCarga {
    private final int capacidadMaxima;
    private int bateriasDisponibles = 0;
    private final List<PlataformaObserver> observadores = new ArrayList<>();

    /**
     * construye una plataforma de carga con la capacidad maxima indicada.<br>
     * <b>pre:</b> capacidadMaxima > 0.<br>
     * <b>post:</b> se inicializa la plataforma con capacidadMaxima, 0 baterias disponibles y sin observadores.<br>
     *
     * @param capacidadMaxima cantidad maxima de baterias que soporta la plataforma. capacidadMaxima > 0.
     */
    public PlataformaDeCarga(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * registra un observador para recibir notificaciones ante cambios en la plataforma.<br>
     * <b>pre:</b> obs != null.<br>
     * <b>post:</b> el observador queda agregado a la lista de observadores.<br>
     *
     * @param obs observador a registrar. obs != null.
     */
    public synchronized void registrarObservador(PlataformaObserver obs) {
        observadores.add(obs);
    }

    /**
     * notifica a todos los observadores registrados sobre un cambio en la plataforma.<br>
     * <b>pre:</b> mensaje != null.<br>
     * <b>post:</b> se invoca onPlataformaCambio en cada observador registrado.<br>
     *
     * @param mensaje descripcion del evento o cambio ocurrido. mensaje != null.
     */
    private void notificar(String mensaje) {
        for (PlataformaObserver obs : observadores) {
            obs.onPlataformaCambio(mensaje, bateriasDisponibles, capacidadMaxima);
        }
    }

    /**
     * deposita una bateria en la plataforma, esperando si esta llena.<br>
     * <b>pre:</b> generadoraId != null.<br>
     * <b>post:</b> se incrementa bateriasDisponibles en 1, se notifica a observadores y se despierta a hilos en espera.<br>
     *
     * @param generadoraId identificador de la generadora que deposita la bateria. generadoraId != null.
     * @throws InterruptedException si el hilo es interrumpido mientras espera espacio disponible.
     */
    public synchronized void depositarBateria(String generadoraId) throws InterruptedException {
        // Guarda en bucle while como especifica la regla de Monitores
        while (bateriasDisponibles >= capacidadMaxima) {
            notificar("[" + generadoraId + "] esperando a que se libere espacio en la Plataforma.");
            wait(); // Libera el monitor y espera ser despertado
        }

        // Se libera espacio y se deposita
        bateriasDisponibles++;
        notificar("Batería depositada por [" + generadoraId + "]. (Total: " + bateriasDisponibles + "/" + capacidadMaxima + ")");

        // Notifica a los consumidores que hay baterías disponibles
        notifyAll();
    }

    /**
     * retira una bateria de la plataforma, esperando si no hay disponibles.<br>
     * <b>pre:</b> naveId != null.<br>
     * <b>post:</b> se decrementa bateriasDisponibles en 1, se notifica a observadores y se despierta a hilos en espera.<br>
     *
     * @param naveId identificador de la nave que retira la bateria. naveId != null.
     * @throws InterruptedException si el hilo es interrumpido mientras espera una bateria disponible.
     */
    public synchronized void retirarBateria(String naveId) throws InterruptedException {
        // Guarda en bucle while
        while (bateriasDisponibles <= 0) {
            notificar("[" + naveId + "] esperando por falta de baterías en la Plataforma.");
            wait(); // Libera el monitor y espera ser despertado
        }

        // Se retira y consume la batería
        bateriasDisponibles--;
        notificar("Batería retirada y utilizada por [" + naveId + "]. (Total: " + bateriasDisponibles + "/" + capacidadMaxima + ")");

        // Notifica a los productores que hay espacio libre
        notifyAll();
    }

    /**
     * devuelve la cantidad actual de baterias disponibles en la plataforma.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna la cantidad de baterias disponibles.<br>
     *
     * @return cantidad de baterias disponibles.
     */
    public synchronized int getBateriasDisponibles() {
        return bateriasDisponibles;
    }

    /**
     * devuelve la capacidad maxima de baterias de la plataforma.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna capacidadMaxima.<br>
     *
     * @return capacidad maxima de la plataforma.
     */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
}