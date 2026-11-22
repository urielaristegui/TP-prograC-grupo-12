package modelo.Tripulacion;
/**
 - Clase Identidad para identificar a los tripulantes de la nave. Tienen su id nico y su nombre.
 - Id asignado en la creacin.
 */
public class Identidad {
    static int sig=-1;
    private int id;
    private String nombre;

    /**
     * construye una identidad con el nombre dado y asigna un id incremental.<br>
     * <b>pre:</b> nombre != null.<br>
     * <b>post:</b> se inicializa el atributo nombre y se asigna el id correspondiente.<br>
     *
     * @param nombre nombre del tripulante. nombre != null.
     */
    public Identidad(String nombre) {
        id= sig++;
        this.nombre= nombre;
    }

    /**
     * devuelve el nombre asociado a la identidad.<br>
     * <b>pre:</b> el objeto esta inicializado.<br>
     * <b>post:</b> retorna el atributo nombre.<br>
     *
     * @return nombre del tripulante.
     */
    public String getNombre(){
        return nombre;
    }
}