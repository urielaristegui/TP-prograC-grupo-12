package Controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.Tripulacion.Tripulante;
import vista.Tripulantes.TripulacionObserver;

public class TripulantesController {

    private final List<Tripulante> tripulantes = new ArrayList<>();
    private final Map<String, Integer> consejosPorTripulante = new HashMap<>();
    private TripulacionObserver vista;

    public TripulantesController() {
    }

    public TripulantesController(List<Tripulante> tripulantesIniciales) {
        if (tripulantesIniciales != null) {
            this.tripulantes.addAll(tripulantesIniciales);
        }
    }

    public void setVista(TripulacionObserver vista) {
        this.vista = vista;
        notificarVista();
    }

    public void setTripulantes(List<Tripulante> nuevosTripulantes) {
        this.tripulantes.clear();
        if (nuevosTripulantes != null) {
            this.tripulantes.addAll(nuevosTripulantes);
        }
        notificarVista();
    }

    public void agregarTripulante(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo.");
        }
        this.tripulantes.add(tripulante);
        notificarVista();
    }

    public void filtrarPorCargo(String cargo) {
        if (vista == null) {
            return;
        }
        if (cargo == null || cargo.equals("Todos")) {
            vista.onTripulacionActualizada(Collections.unmodifiableList(new ArrayList<>(tripulantes)));
            return;
        }
        List<Tripulante> filtrados = new ArrayList<>();
        for (Tripulante t : tripulantes) {
            if (t.getCargo() != null && t.getCargo().equalsIgnoreCase(cargo)) {
                filtrados.add(t);
            }
        }
        vista.onTripulacionActualizada(filtrados);
    }

    public void registrarConsejo(Tripulante tripulante) {
        if (tripulante == null || tripulante.getCargo() == null) {
            return;
        }
        if (!tripulante.getCargo().equalsIgnoreCase("Consejero")) {
            return;
        }
        String clave = obtenerClaveTripulante(tripulante);
        int actual = consejosPorTripulante.getOrDefault(clave, 0);
        consejosPorTripulante.put(clave, actual + 1);
        notificarVista();
    }

    public int getConsejos(Tripulante tripulante) {
        if (tripulante == null) {
            return 0;
        }
        return consejosPorTripulante.getOrDefault(obtenerClaveTripulante(tripulante), 0);
    }

    public double calcularHaberTotal(Tripulante tripulante) {
        if (tripulante == null) {
            return 0.0;
        }
        double haber = tripulante.getSueldo();
        if (tripulante.getCargo() != null && tripulante.getCargo().equalsIgnoreCase("Consejero")) {
            haber += getConsejos(tripulante) * 2.0;
        }
        return haber;
    }

    public void refrescar() {
        notificarVista();
    }

    private void notificarVista() {
        if (vista != null) {
            vista.onTripulacionActualizada(Collections.unmodifiableList(new ArrayList<>(tripulantes)));
        }
    }

    private String obtenerClaveTripulante(Tripulante t) {
        String nombreId = (t.getID() != null) ? t.getID().toString() : "";
        return nombreId + "|" + t.getCargo() + "|" + t.getOrigen() + "|" + System.identityHashCode(t);
    }
}
