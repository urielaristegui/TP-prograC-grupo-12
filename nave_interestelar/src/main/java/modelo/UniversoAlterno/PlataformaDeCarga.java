package modelo.UniversoAlterno;

import java.util.ArrayList;
import java.util.List;

public class PlataformaDeCarga {
    private final int capacidadMaxima;
    private int bateriasDisponibles = 0;
    private final List<PlataformaObserver> observadores = new ArrayList<>();

    public PlataformaDeCarga(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    // Registrar observadores UI
    public synchronized void registrarObservador(PlataformaObserver obs) {
        observadores.add(obs);
    }

    private void notificar(String mensaje) {
        for (PlataformaObserver obs : observadores) {
            obs.onPlataformaCambio(mensaje, bateriasDisponibles, capacidadMaxima);
        }
    }

    // Método sincronizado para Productores (GeneradoraDeEnergia)
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

    // Método sincronizado de Consumidores
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

    public synchronized int getBateriasDisponibles() {
        return bateriasDisponibles;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
}