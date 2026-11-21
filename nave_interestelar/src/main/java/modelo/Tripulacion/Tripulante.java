
package modelo.Tripulacion;

/**
 * Modelo base de un tripulante.
 *
 * Implementa Haber y permite aplicar un unico
 * decorador por origen planetario.
 *
 * Invariantes:
 * - id no es null.
 * - antiguedad es finita y no negativa.
 * - Un tripulante tiene como maximo un origen.
 */
public abstract class Tripulante implements Haber {

    public enum Origen {

        TERRICOLA("Terrícola"),
        MARCIANO("Marciano"),
        VULCANO("Vulcano");

        private final String nombre;

        Origen(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    protected Identidad id;
    protected String cargo;
    protected double antiguedad;
    protected String origen;
    protected double sueldob;

    private Haber haberConOrigen;

    /**
     * Pre:
     * - id != null.
     * - Nombre no vacio.
     * - ant >= 0 y finita.
     *
     * Post:
     * - Inicializa identidad y antiguedad.
     * - El origen queda pendiente.
     */
    public Tripulante(Identidad id, double ant) {

        if (id == null
                || id.getNombre() == null
                || id.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "Identidad invalida"
            );
        }

        this.id = id;
        setAntiguedad(ant);
    }

    /**
     * Calcula el sueldo propio del cargo.
     *
     * Post: no modifica al tripulante.
     *
     * @return sueldo del cargo sin subsidio planetario
     */
    @Override
    public abstract double getSueldo();

    /**
     * Aplica el patron Decorator para el origen elegido.
     *
     * Pre: nuevoOrigen != null.
     *
     * Post:
     * - Guarda el origen.
     * - Aplica el subsidio correspondiente.
     * - Reemplaza cualquier decorador anterior.
     * - No acumula subsidios de distintos planetas.
     *
     * @param nuevoOrigen origen seleccionado
     */
    public void asignarOrigen(Origen nuevoOrigen) {

        if (nuevoOrigen == null) {
            throw new IllegalArgumentException(
                    "Selecciona un origen"
            );
        }

        Haber nuevoHaber = switch (nuevoOrigen) {

            case TERRICOLA ->
                    new Terricola(this);

            case MARCIANO ->
                    new Marciano(this);

            case VULCANO ->
                    new Vulcano(this);
        };

        this.haberConOrigen = nuevoHaber;
        this.origen = nuevoOrigen.toString();
    }

    /**
     * Calcula el sueldo aplicando Decorator.
     *
     * Pre: ninguna.
     *
     * Post:
     * - No modifica el modelo.
     * - Devuelve el sueldo del cargo mas el subsidio.
     * - Si no tiene origen, devuelve el sueldo base.
     *
     * @return haber incluyendo el origen
     */
    public double getSueldoConOrigen() {

        return haberConOrigen != null
                ? haberConOrigen.getSueldo()
                : getSueldo();
    }

    public void setID(Identidad i) {

        if (i == null
                || i.getNombre() == null
                || i.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "Identidad invalida"
            );
        }

        id = i;
    }

    public String getID() {
        return id.getNombre();
    }

    public String getCargo() {
        return cargo;
    }

    public double getAntiguedad() {
        return antiguedad;
    }

    public String getOrigen() {
        return origen;
    }

    /**
     * Pre: a >= 0 y finita.
     * Post: antiguedad = a.
     */
    public void setAntiguedad(double a) {

        if (a < 0 || !Double.isFinite(a)) {

            throw new IllegalArgumentException(
                    "La antiguedad debe ser un numero "
                            + "finito no negativo"
            );
        }

        this.antiguedad = a;
    }
}
