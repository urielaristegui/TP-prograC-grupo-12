package modelo.UniversoAlterno;

public class GeneradoraDeEnergia implements Runnable {
    private final String nombre;
    private final PlataformaDeCarga plataforma;
    private volatile boolean ejecutando = true;

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

    public void detener() {
        this.ejecutando = false;
    }
}