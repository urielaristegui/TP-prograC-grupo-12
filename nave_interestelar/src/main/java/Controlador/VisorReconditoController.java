package Controlador;


import modelo.UniversoAlterno.GeneradoraDeEnergia;
import modelo.UniversoAlterno.NaveConsumidora;
import modelo.UniversoAlterno.PlataformaDeCarga;
import vista.VisorRecondito.IVisorReconditoVista;

import java.util.ArrayList;
import java.util.List;

public class VisorReconditoController {
    private final PlataformaDeCarga modeloPlataforma;
    private final IVisorReconditoVista vista;

    private final List<GeneradoraDeEnergia> generadoras = new ArrayList<>();
    private final List<NaveConsumidora> navesConsumidoras = new ArrayList<>();

    private int contadorGeneradoras = 1;
    private int contadorNaves = 1;

    /**
     * construye el controlador del visor recondito y registra los listeners de creacion en la vista.<br>
     * <b>pre:</b> modeloPlataforma != null, vista != null.<br>
     * <b>post:</b> se inicializan las referencias y se registran los oyentes para agregar generadoras y naves consumidoras.<br>
     *
     * @param modeloPlataforma plataforma de carga compartida del universo paralelo. modeloPlataforma != null.
     * @param vista interfaz de vista asociada al controlador. vista != null.
     */
    public VisorReconditoController(PlataformaDeCarga modeloPlataforma, IVisorReconditoVista vista) {
        this.modeloPlataforma = modeloPlataforma;
        this.vista = vista;

        // Registrar los oyentes de eventos a través de la interfaz de la vista
        this.vista.setAgregarGeneradoraListener(e -> agregarGeneradora());
        this.vista.setAgregarNaveListener(e -> agregarNaveConsumidora());
    }

    /**
     * crea e inicia una nueva generadora de energia en su propio hilo y notifica en la consola de la vista.<br>
     * <b>pre:</b> modeloPlataforma != null, vista != null y las listas internas estan inicializadas.<br>
     * <b>post:</b> se instancia una GeneradoraDeEnergia, se agrega a generadoras, se inicia su hilo y se muestra el mensaje en la vista.<br>
     */
    public void agregarGeneradora() {
        String nombre = "Generadora-" + contadorGeneradoras++;
        GeneradoraDeEnergia generadora = new GeneradoraDeEnergia(nombre, modeloPlataforma);
        generadoras.add(generadora);

        new Thread(generadora, nombre).start();
        vista.mostrarMensajeLog("¡Ingresó una nueva [" + nombre + "] al universo paralelo!");
    }

    /**
     * crea e inicia una nueva nave consumidora en su propio hilo y notifica en la consola de la vista.<br>
     * <b>pre:</b> modeloPlataforma != null, vista != null y las listas internas estan inicializadas.<br>
     * <b>post:</b> se instancia una NaveConsumidora, se agrega a navesConsumidoras, se inicia su hilo y se muestra el mensaje en la vista.<br>
     */
    public void agregarNaveConsumidora() {
        String nombre = "Nave-" + contadorNaves++;
        NaveConsumidora nave = new NaveConsumidora(nombre, modeloPlataforma);
        navesConsumidoras.add(nave);

        new Thread(nave, nombre).start();
        vista.mostrarMensajeLog("¡Ingresó una nueva [" + nombre + "] al universo paralelo!");
    }

    /**
     * solicita detener todas las generadoras y naves activas y deshabilita los controles de la vista.<br>
     * <b>pre:</b> generadoras, navesConsumidoras y vista estan inicializados.<br>
     * <b>post:</b> se invoca detener() en cada generadora y nave registrada y se deshabilitan los controles en la vista.<br>
     */
    public void detenerSimulacion() {
        for (GeneradoraDeEnergia g : generadoras) g.detener();
        for (NaveConsumidora n : navesConsumidoras) n.detener();
        vista.habilitarControles(false);
    }
}