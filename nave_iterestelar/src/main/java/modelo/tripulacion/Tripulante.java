package modelo.tripulacion;

/**
 * Representa un integrante de la tripulación.
 * Invariantes:
 * - ID y nombre no nulos ni vacíos.
 * - Cargo y origen no nulos.
 * - Antigüedad no negativa, expresada en años.
 */
public class Tripulante {

    private final String id;
    private final String nombre;
    private final Cargo cargo;
    private final Origen origen;
    private final int antiguedad;

    /**
     * Crea un tripulante con los datos indicados.
     *
     * @param id identificador único del tripulante
     * @param nombre nombre del tripulante
     * @param cargo cargo que ocupa
     * @param origen planeta de origen
     * @param antiguedad años de antigüedad
     * @throws IllegalArgumentException si algún dato es inválido
     */
    public Tripulante(String id, String nombre, Cargo cargo,
                      Origen origen, int antiguedad) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID no puede ser null ni vacío");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre no puede ser null ni vacío");
        }

        if (cargo == null) {
            throw new IllegalArgumentException(
                    "El cargo no puede ser null");
        }

        if (origen == null) {
            throw new IllegalArgumentException(
                    "El origen no puede ser null");
        }

        if (antiguedad < 0) {
            throw new IllegalArgumentException(
                    "La antigüedad no puede ser negativa");
        }

        this.id = id.strip();
        this.nombre = nombre.strip();
        this.cargo = cargo;
        this.origen = origen;
        this.antiguedad = antiguedad;
    }

    public String getID() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public Origen getOrigen() {
        return origen;
    }

    public int getAntiguedad() {
        return antiguedad;
    }
}