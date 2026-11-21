
package Controlador;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.Consejero;

import vista.Tripulantes.TripulacionObserver;

/**
 * Controlador MVC de tripulantes y haberes.
 *
 * Invariantes:
 * - Los haberes se calculan desde el modelo.
 * - El origen utiliza el patron Decorator.
 * - El filtro seleccionado se conserva.
 */
public class TripulantesController {

    private final List<Tripulante> tripulantes =
            new ArrayList<>();

    private TripulacionObserver vista;

    private String filtroCargo = "Todos";

    public TripulantesController() {
    }

    public TripulantesController(
            List<Tripulante> tripulantesIniciales) {

        if (tripulantesIniciales != null) {
            this.tripulantes.addAll(
                    tripulantesIniciales
            );
        }
    }

    public void setVista(TripulacionObserver vista) {

        this.vista = vista;
        notificarVista();
    }

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
     * Filtra por cargo, aceptando nombres
     * con o sin tildes.
     *
     * Pre: ninguna.
     * Post: notifica la lista filtrada.
     */
    public void filtrarPorCargo(String cargo) {

        filtroCargo = cargo == null
                ? "Todos"
                : cargo;

        notificarVista();
    }

    /**
     * Registra un consejo en el modelo.
     *
     * Pre: tripulante != null.
     *
     * Post:
     * - Si es Consejero, registra el consejo.
     * - Su sueldo aumenta 2 PG.
     * - Notifica a la vista.
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
     * Consulta los consejos registrados.
     *
     * Post: no modifica el estado.
     *
     * @return cantidad de consejos
     */
    public int getConsejos(
            Tripulante tripulante) {

        return tripulante instanceof Consejero consejero
                ? consejero.getCantidadConsejos()
                : 0;
    }

    /**
     * Calcula el haber total.
     *
     * Pre: ninguna.
     *
     * Post:
     * - No modifica el tripulante.
     * - Incluye antiguedad y subsidio por origen.
     * - Incluye los consejos cuando corresponde.
     * - No duplica ningun bono.
     *
     * @return haber total en PG
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

    public void refrescar() {
        notificarVista();
    }

    /**
     * Notifica a la vista respetando el filtro.
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
     * Elimina tildes y normaliza el texto.
     *
     * Pre: ninguna.
     * Post: devuelve una representacion normalizada.
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
