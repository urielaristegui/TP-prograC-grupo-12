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
        this.antiguedad= ant;
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
     * Modifica la antigüedad del tripulante.
     *
     * @param a Nueva antigüedad en años.
     *          <p><b>Precondición:</b> {@code a >= 0}. No se admiten valores negativos.</p>
     *          <p><b>Postcondición:</b> El atributo antigüedad del tripulante queda actualizado con el nuevo valor válido.</p>
     */
    public void setAntiguedad(double a){
        antiguedad= a;
    }

}