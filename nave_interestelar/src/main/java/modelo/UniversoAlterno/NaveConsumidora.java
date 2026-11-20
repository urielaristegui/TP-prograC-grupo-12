package modelo.UniversoAlterno;

public class NaveConsumidora implements Runnable {
    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private volatile boolean ejecutando = true;

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

    public void detener() {
        this.ejecutando = false;
    }
}