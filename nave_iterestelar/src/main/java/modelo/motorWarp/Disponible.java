package modelo.motorWarp;

public class Disponible implements modelo.motorWarp.State {
    private MotorWarp motorWarp;

    /**
     * constructor de la clase Disponible<br>
     * <b>pre:</b>motorWarp != null  <br>
     * <b>post:</b> se inicializa el atributo motorWarp <br>
     */
    public Disponible(MotorWarp motorWarp){
        this.motorWarp = motorWarp;
    }

    /**
     * cambia el estado del motorWarp a PreparandoSalto<br>
     * <b>post:</b> el motorWarp se encuentra en el estado PreparandoSalto <br>
     */
    @Override
    public void prepararSalto() {
        motorWarp.setState(new PreparandoSalto(motorWarp));
        motorWarp.bitacora.registrar("preparando salto");
    }

    /**
     *intenta entrar en warp <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void entrarEnWarp(){
        motorWarp.bitacora.registrar("ERROR: no se puede entrar en warp sin preparar el salto");
    }

    /**
     * intenta iniciar el enfriamiento <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void iniciarEnfriamiento() {
         motorWarp.bitacora.registrar("ERROR: no requiere enfriamiento");
    }

    /**
     * intenta volver a disponible <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void volverADisponible() {
        motorWarp.bitacora.registrar("el motor permanece disponible");
    }


}
