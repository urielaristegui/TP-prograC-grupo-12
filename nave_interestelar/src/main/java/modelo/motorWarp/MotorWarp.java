package modelo.motorWarp;

public class MotorWarp {
    ;private State state;

    /**
     * Constructor de la clase MotorWarp<br>
     */
    public MotorWarp() {
        setState(new Disponible(this));
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
