package Controlador;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.Consejero;

import vista.Tripulantes.TripulacionObserver;

/**
 - Controlador MVC de tripulantes y haberes.
 *
 - Invariantes:
 - - Los haberes se calculan desde el modelo.
 - - El origen utiliza el patron Decorator.
 - - El filtro seleccionado se conserva.
 */
public class TripulantesController {

    private final List<Tripulante> tripulantes =
            new ArrayList<>();

    private TripulacionObserver vista;

    private String filtroCargo = "Todos";

    /**
     * construye un controlador de tripulantes vacio con el filtro inicial en "Todos".<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> se inicializa la lista de tripulantes vacia, vista en null y filtroCargo en "Todos".<br>
     */
    public TripulantesController() {
    }

    /**
     * construye un controlador de tripulantes cargando los tripulantes iniciales indicados.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> si tripulantesIniciales != null, agrega sus elementos a tripulantes; si es null, deja la lista vacia.<br>
     *
     * @param tripulantesIniciales lista inicial de tripulantes.
     */
    public TripulantesController(
            List<Tripulante> tripulantesIniciales) {

        if (tripulantesIniciales != null) {
            this.tripulantes.addAll(
                    tripulantesIniciales
            );
        }
    }

    /**
     * asocia un observador de vista y le notifica la tripulacion filtrada actual.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> actualiza vista y notifica a la vista si no es nula.<br>
     *
     * @param vista observador de tripulacion a vincular.
     */
    public void setVista(TripulacionObserver vista) {

        this.vista = vista;
        notificarVista();
    }

    /**
     * reemplaza la lista de tripulantes y notifica a la vista respetando el filtro actual.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> limpia tripulantes, agrega los elementos de nuevosTripulantes si no es null y notifica a la vista.<br>
     *
     * @param nuevosTripulantes nueva lista de tripulantes.
     */
    public void setTripulantes(
            List<Tripulante> nuevosTripulantes) {

        this.tripulantes.clear();

        if (nuevosTripulantes != null) {

            this.tripulantes.addAll(
                    nuevosTripulantes
            );
        }

        notificarVista();
    }

    /**
     * agrega un tripulante a la lista y notifica a la vista.<br>
     * <b>pre:</b> tripulante != null.<br>
     * <b>post:</b> agrega tripulante a tripulantes y notifica a la vista.<br>
     *
     * @param tripulante tripulante a agregar. tripulante != null.
     * @throws IllegalArgumentException si tripulante es null.
     */
    public void agregarTripulante(
            Tripulante tripulante) {

        if (tripulante == null) {
            throw new IllegalArgumentException(
                    "El tripulante no puede ser nulo"
            );
        }

        this.tripulantes.add(tripulante);

        notificarVista();
    }

    /**
     - Filtra por cargo, aceptando nombres
     - con o sin tildes.
     *
     - Pre: ninguna.
     - Post: notifica la lista filtrada.
     */
    public void filtrarPorCargo(String cargo) {

        filtroCargo = cargo == null
                ? "Todos"
                : cargo;

        notificarVista();
    }

    /**
     - Registra un consejo en el modelo.
     *
     - Pre: tripulante != null.
     *
     - Post:
     - - Si es Consejero, registra el consejo.
     - - Su sueldo aumenta 2 PG.
     - - Notifica a la vista.
     */
    public void registrarConsejo(
            Tripulante tripulante) {

        if (tripulante instanceof Consejero consejero) {

            consejero.registrarConsejo(
                    "Consejo registrado desde la interfaz"
            );

            notificarVista();
        }
    }

    /**
     - Consulta los consejos registrados.
     *
     - Post: no modifica el estado.
     *
     - @return cantidad de consejos
     */
    public int getConsejos(
            Tripulante tripulante) {

        return tripulante instanceof Consejero consejero
                ? consejero.getCantidadConsejos()
                : 0;
    }

    /**
     - Calcula el haber total.
     *
     - Pre: ninguna.
     *
     - Post:
     - - No modifica el tripulante.
     - - Incluye antiguedad y subsidio por origen.
     - - Incluye los consejos cuando corresponde.
     - - No duplica ningun bono.
     *
     - @return haber total en PG
     */
    public double calcularHaberTotal(
            Tripulante tripulante) {

        if (tripulante == null) {
            return 0.0;
        }

        // Consejero.getSueldo() ya incluye
        // +2 PG por cada consejo registrado.
        // No debemos sumarlos nuevamente.

        return tripulante.getSueldoConOrigen();
    }

    /**
     * refresca la vista notificando la lista actual segun el filtro vigente.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> si vista != null, se le notifica la lista filtrada sin modificar el estado.<br>
     */
    public void refrescar() {
        notificarVista();
    }

    /**
     - Notifica a la vista respetando el filtro.
     */
    private void notificarVista() {

        if (vista == null) {
            return;
        }

        List<Tripulante> filtrados =
                new ArrayList<>();

        for (Tripulante tripulante : tripulantes) {

            if ("todos".equals(normalizar(filtroCargo))
                    || normalizar(filtroCargo).equals(
                    normalizar(tripulante.getCargo())
            )) {

                filtrados.add(tripulante);
            }
        }

        vista.onTripulacionActualizada(
                Collections.unmodifiableList(filtrados)
        );
    }

    /**
     - Elimina tildes y normaliza el texto.
     *
     - Pre: ninguna.
     - Post: devuelve una representacion normalizada.
     */
    private static String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(java.util.Locale.ROOT);
    }
}