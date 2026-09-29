package motorWarp;
import bitacora.Bitacora;

public class MotorWarp {
    private State state;
    protected Bitacora bitacora;

    /**
     * Constructor de la clase MotorWarp<br>
     * <b>pre:</b>bitacora != null  <br>
     * <b>post:</b> se inicializa el atributo bitacora <br>
     * @param bitacora
     */
    public MotorWarp(Bitacora bitacora) {
        setState(new Disponible(this));
        this.bitacora = bitacora;
    }

    /**
     * Cambia el estado del motorWarp<br>
     * <b>post:</b> se inicializa el atributo state <br>
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * cambia la bitacora<br>
     * <b>post:</b> se inicializa el atributo bitacora <br>
     * @param bitacora
     */
    public void setBitacora(Bitacora bitacora){
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
