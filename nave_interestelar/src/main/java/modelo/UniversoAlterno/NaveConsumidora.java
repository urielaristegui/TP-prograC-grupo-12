package modelo.UniversoAlterno;

public class NaveConsumidora implements Runnable {
    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private volatile boolean ejecutando = true;

    /**
     * construye una nave consumidora asociada a una plataforma de carga.<br>
     * <b>pre:</b> nombre != null, plataforma != null.<br>
     * <b>post:</b> se inicializa la nave consumidora con su nombre y plataforma asignados, y ejecutando en true.<br>
     *
     * @param nombre identificador de la nave consumidora. nombre != null.
     * @param plataforma plataforma de carga de donde se retiran baterias. plataforma != null.
     */
    public NaveConsumidora(String nombre, PlataformaDeCarga plataforma) {
        this.nombre = nombre;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            while (ejecutando) {
                // Solicita y retira batería
                plataforma.retirarBateria(nombre);

                // Simula consumo/vuelo de la nave
                Thread.sleep((long) (1500 + Math.random() * 2500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * solicita detener el ciclo de ejecucion de la nave consumidora.<br>
     * <b>pre:</b> la instancia esta inicializada.<br>
     * <b>post:</b> el atributo ejecutando pasa a valer false.<br>
     */
    public void detener() {
        this.ejecutando = false;
    }
}
