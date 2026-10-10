package modelo.motorWarp;

import modelo.bitacora.Bitacora;

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
        Bitacora.getInstance().registrar("preparando salto");
    }

    /**
     *intenta entrar en warp <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void entrarEnWarp(){
        Bitacora.getInstance().registrar("ERROR: no se puede entrar en warp sin preparar el salto");
    }

    /**
     * intenta iniciar el enfriamiento <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void iniciarEnfriamiento() {
        Bitacora.getInstance().registrar("ERROR: no requiere enfriamiento");
    }

    /**
     * intenta volver a disponible <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void volverADisponible() {
        Bitacora.getInstance().registrar("el motor permanece disponible");
    }
}
