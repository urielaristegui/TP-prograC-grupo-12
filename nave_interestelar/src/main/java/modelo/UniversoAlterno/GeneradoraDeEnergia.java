package modelo.UniversoAlterno;

public class GeneradoraDeEnergia implements Runnable {
    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private volatile boolean ejecutando = true;

    /**
     * construye una generadora de energia asociada a una plataforma de carga.<br>
     * <b>pre:</b> nombre != null y plataforma != null.<br>
     * <b>post:</b> se inicializan nombre, plataforma y ejecutando en true.<br>
     *
     * @param nombre identificador de la generadora de energia. nombre != null.
     * @param plataforma plataforma de carga compartida donde se depositan baterias. plataforma != null.
     */
    public GeneradoraDeEnergia(String nombre, PlataformaDeCarga plataforma) {
        this.nombre = nombre;
        this.plataforma = plataforma;
    }

    @Override
    public void run() {
        try {
            while (ejecutando) {
                // Simula el tiempo de fabricación de una batería
                Thread.sleep((long) (1000 + Math.random() * 2000));

                // Intenta depositar en la plataforma sincronizada
                plataforma.depositarBateria(nombre);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * detiene el ciclo de ejecucion de la generadora de energia.<br>
     * <b>pre:</b> la generadora esta inicializada.<br>
     * <b>post:</b> ejecutando pasa a ser false para finalizar el ciclo de produccion.<br>
     */
    public void detener() {
        this.ejecutando = false;
    }
}