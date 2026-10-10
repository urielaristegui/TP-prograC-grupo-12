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
     *Constructor principal.
     * @param id ya creada
     * @param ant > 0
     * @param o de una de las ocnstantes
     */
    
    
    public Tripulante(Identidad id,double ant){
        this.id= id;
        this.antiguedad= ant;
    }
    
    public abstract double getSueldo();
    
    public Identidad getID(){
        return id;
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
    
    public void setID(Identidad i){
        id= i;
    }
    /**
     *
     * @param a > 0
     */
    public void setAntiguedad(double a){
        antiguedad= a;
    }

}