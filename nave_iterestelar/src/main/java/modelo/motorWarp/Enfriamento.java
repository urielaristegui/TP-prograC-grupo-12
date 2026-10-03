package modelo.motorWarp;

public class Enfriamento implements State {
    private MotorWarp motorWarp;

    /**
     * constructor de la clase Enfriamento<br>
     * <b>pre:</b>motorWarp != null  <br>
     * <b>post:</b> se inicializa el atributo motorWarp <br>
     */
    public Enfriamento(MotorWarp motorWarp) {
        this.motorWarp = motorWarp;
    }

    /**
     * intenta preparar el salto <br>
     * <b>post:</b> error <br>
     */
    @Override
   public void prepararSalto() {
        motorWarp.bitacora.registrar("ERROR: no se puede preparar el salto en enfriamiento");
   }

    /**
     * intenta entrar en warp <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void entrarEnWarp() {
        motorWarp.bitacora.registrar("ERROR: no se puede entrar en warp en enfriamiento");
    }

    /**
     * intenta volver a disponible <br>
     * <b>post:</b> el motorWarp se encuentra en el estado Disponible <br>
     */
    @Override
    public void volverADisponible() {
        motorWarp.setState(new Disponible(motorWarp));
        motorWarp.bitacora.registrar("volviendo a disponible");
    }

    /**
     * intenta iniciar el enfriamiento <br>
     * <b>post:</b> el motorWarp se encuentra en el estado Enfriamento <br>
     */
    @Override
    public void iniciarEnfriamiento() {
        motorWarp.bitacora.registrar("el motor permanece en enfriamiento");
    }
}
