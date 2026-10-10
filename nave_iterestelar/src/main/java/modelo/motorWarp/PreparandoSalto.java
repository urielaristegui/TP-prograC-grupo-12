package modelo.motorWarp;


import modelo.bitacora.Bitacora;

public class PreparandoSalto implements modelo.motorWarp.State {
    private MotorWarp motorWarp;

    /**
     * constructor de la clase PreparandoSalto<br>
     * <b>pre:</b>motorWarp != null  <br>
     * <b>post:</b> se inicializa el atributo motorWarp <br>
     */
    public PreparandoSalto(MotorWarp motorWarp) {
        this.motorWarp = motorWarp;
    }

    /**
     * intenta preparar el salto <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void prepararSalto() {
        Bitacora.getInstance().registrar("ERROR: el motor ya esta preparando el salto");
    }

    /**
     *entrar en warp <br>
     * <b>post:</b> el motorWarp se encuentra en el estado EnWarp <br>
     */
    @Override
    public void entrarEnWarp() {
        motorWarp.setState(new EnWarp(motorWarp));
        Bitacora.getInstance().registrar("entrando en warp");
    }

    /**
     * intenta iniciar el enfriamiento <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void iniciarEnfriamiento() {
        Bitacora.getInstance().registrar("ERROR: el motor no puede iniciar el enfriamiento sin haber terminado el salto");
    }

    /**
     * intenta volver a disponible <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void volverADisponible() {
        Bitacora.getInstance().registrar("ERROR:el motor no puede volver a disponible sin haber terminado el salto");
    }
}
