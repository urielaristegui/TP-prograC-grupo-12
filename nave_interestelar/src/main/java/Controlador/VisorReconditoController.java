package Controlador;


import modelo.UniversoAlterno.GeneradoraDeEnergia;
import modelo.UniversoAlterno.NaveConsumidora;
import modelo.UniversoAlterno.PlataformaDeCarga;
import vista.IVisorReconditoVista;

import java.util.ArrayList;
import java.util.List;

public class VisorReconditoController {
    private final PlataformaDeCarga modeloPlataforma;
    private final IVisorReconditoVista vista;

    private final List<GeneradoraDeEnergia> generadoras = new ArrayList<>();
    private final List<NaveConsumidora> navesConsumidoras = new ArrayList<>();

    private int contadorGeneradoras = 1;
    private int contadorNaves = 1;

    public VisorReconditoController(PlataformaDeCarga modeloPlataforma, IVisorReconditoVista vista) {
        this.modeloPlataforma = modeloPlataforma;
        this.vista = vista;

        // Registrar los oyentes de eventos a través de la interfaz de la vista
        this.vista.setAgregarGeneradoraListener(e -> agregarGeneradora());
        this.vista.setAgregarNaveListener(e -> agregarNaveConsumidora());
    }

    public void agregarGeneradora() {
        String nombre = "Generadora-" + contadorGeneradoras++;
        GeneradoraDeEnergia generadora = new GeneradoraDeEnergia(nombre, modeloPlataforma);
        generadoras.add(generadora);

        new Thread(generadora, nombre).start();
        vista.mostrarMensajeLog("¡Ingresó una nueva [" + nombre + "] al universo paralelo!");
    }

    public void agregarNaveConsumidora() {
        String nombre = "Nave-" + contadorNaves++;
        NaveConsumidora nave = new NaveConsumidora(nombre, modeloPlataforma);
        navesConsumidoras.add(nave);

        new Thread(nave, nombre).start();
        vista.mostrarMensajeLog("¡Ingresó una nueva [" + nombre + "] al universo paralelo!");
    }

    public void detenerSimulacion() {
        for (GeneradoraDeEnergia g : generadoras) g.detener();
        for (NaveConsumidora n : navesConsumidoras) n.detener();
        vista.habilitarControles(false);
    }
}
