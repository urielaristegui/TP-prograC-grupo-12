package modelo.Tripulacion;

/**
 * Clase abstracta padre de todos los tipos de  tripulantes.
 */

public abstract class Tripulante implements Haber{
    protected Identidad id;
    protected String cargo;
    protected double antiguedad;
    protected String origen;
    protected double sueldob;

    /**
     * Constructor principal de Tripulante.
     *
     * @param id Identidad ya creada del tripulante. No debe ser nula.
     * @param ant Antigüedad en años.
     *            <p><b>Precondición:</b> {@code ant >= 0}. La antigüedad no puede ser negativa.</p>
     *            <p><b>Postcondición:</b> Se crea una instancia de Tripulante con la identidad y antigüedad asignadas correctamente.</p>
     */
    public Tripulante(Identidad id,double ant){
        this.id= id;
        setAntiguedad(ant);
    }

    /**
     * Método abstracto para obtener el sueldo o haber del tripulante.
     * @return El monto correspondiente calculado o base.
     */
    public abstract double getSueldo();

    public void setID(Identidad i){
        id= i;
    }
    public String getID(){
        return id.getNombre();
    }

    public String getCargo(){
        return cargo;
    }
    public double getAntiguedad(){
        return antiguedad;
    }
    public String getOrigen(){
        return origen;
    }



    /**
     * Establece la antigüedad del tripulante.
     *
     * @param a años de antigüedad, valor no negativo
     * @throws IllegalArgumentException si la antigüedad es negativa
     */
    public void setAntiguedad(double a) {
        if (a < 0 || !Double.isFinite(a)) {
            throw new IllegalArgumentException(
                    "La antigüedad debe ser un número finito no negativo"
            );
        }

        this.antiguedad = a;
    }

}