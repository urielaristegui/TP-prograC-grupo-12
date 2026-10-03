package Tripulacion;
/**
 * Clase Identidad para identificar a los tripulantes de la nave. Tienen su id único y su nombre.
 * Id asignado en la creación.
 */
public class Identidad {
    static int sig=-1;
    private int id;
    private String nombre;
    
    public Identidad(String nombre) {
        id= sig++;
        this.nombre= nombre;
    }
}
