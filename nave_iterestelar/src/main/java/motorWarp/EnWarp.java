package motorWarp;

public class EnWarp implements State{
    private MotorWarp motorWarp;

    /**
     * constructor de la clase EnWarp<br>
     * <b>pre:</b>motorWarp != null  <br>
     * <b>post:</b> se inicializa el atributo motorWarp <br>
     */
   public EnWarp(MotorWarp motorWarp) {
       this.motorWarp = motorWarp;
   }

    /**
     * intenta entrar en warp <br>
     * <b>post:</b> error <br>
     */
    @Override
   public void entrarEnWarp() {
       motorWarp.bitacora.registrar("el motor permanece en warp");
   }

    /**
     * intenta iniciar el enfriamiento <br>
     * <b>post:</b> el motorWarp se encuentra en el estado Enfriamento <br>
     */
    @Override
    public void iniciarEnfriamiento() {
        motorWarp.setState(new Enfriamento(motorWarp));
        motorWarp.bitacora.registrar("iniciando enfriamiento");
    }

    /**
     * intenta volver a disponible <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void volverADisponible() {
        motorWarp.bitacora.registrar("ERROR: el motor no puede volver a disponible sin haberse enfriado");
    }

    /**
     * intenta preparar el salto <br>
     * <b>post:</b> error <br>
     */
    @Override
    public void prepararSalto() {
        motorWarp.bitacora.registrar("ERROR: el motor no puede preparar el salto en warp");
    }
}
