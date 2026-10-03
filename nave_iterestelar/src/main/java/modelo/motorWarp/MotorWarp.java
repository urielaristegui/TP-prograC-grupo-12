package modelo.motorWarp;
import modelo.bitacora.Bitacora;

public class MotorWarp {
    private modelo.motorWarp.State state;
    protected Bitacora bitacora;

    /**
     * Constructor de la clase MotorWarp<br>
     * <b>pre:</b>bitacora != null  <br>
     * <b>post:</b> se inicializa el atributo bitacora <br>
     *  * <b>trow: bitacora != null</b>
     * @param bitacora
     */
    public MotorWarp(Bitacora bitacora) {
        if (bitacora == null) {
            throw new IllegalArgumentException("La bitacora no puede ser null");
        }

        setState(new Disponible(this));
        this.bitacora = bitacora;
    }

    /**
     * Cambia el estado del motorWarp<br>
     * <b>post:</b> se inicializa el atributo state <br>
     *    * <b>trow: state != null</b>
     * @param state
     */
    public void setState(State state) {
        if (state == null)  {
            throw new IllegalArgumentException("El state no puede ser null");
        }
        this.state = state;
    }

    /**
     * cambia la bitacora<br>
     * <b>post:</b> se inicializa el atributo bitacora <br>
     * <b>trow: bitacora != null</b>
     * @param bitacora
     */
    public void setBitacora(Bitacora bitacora){
        if (bitacora== null){
            throw new IllegalArgumentException("La bitacora no puede ser null");
        }
        this.bitacora = bitacora;
    }

    /**
     * devuelve la bitacora<br>
     * <b>post:</b> se inicializa el atributo bitacora <br>
     * @return
     */
    public Bitacora getBitacora(){
        return bitacora;
    }

    /**
     * Devuelve el estado del motorWarp<br>
     * <b>post:</b> se inicializa el atributo state <br>
     * @return
     */
    public State getState() {
        return state;
    }


    //metodos del patron state

    /**
     * llama al metodo volverADisponible del state<br>
     */
    public void volverAlDisponible(){
        this.state.volverADisponible();
    }

    /**
     * llama al metodo entrarEnWarp del state<br>
     */
    public void entrarEnWarp(){
        this.state.entrarEnWarp();
    }

    /**
     * llama al metodo iniciarEnfriamiento del state<br>
     */
    public void iniciarEnfriamiento(){
        this.state.iniciarEnfriamiento();
    }

    /**
     * llama al metodo prepararSalto del state<br>
     */
    public void prepararSalto(){
        this.state.prepararSalto();
    }
}
