package modelo.motorWarp;

import modelo.bitacora.Bitacora;

public class Disponible implements modelo.motorWarp.State {
    private MotorWarp motorWarp;

    /**
     * constructor de la clase Disponible<br>
     * <b>pre:</b> motorWarp != null<br>
     * <b>post:</b> se inicializa el atributo motorWarp<br>
     */
    public Disponible(MotorWarp motorWarp){
        this.motorWarp = motorWarp;
    }

    @Override
    public void prepararSalto() {
        motorWarp.setState(new PreparandoSalto(motorWarp));
        Bitacora.getInstance().registrar("preparando salto");
    }

    @Override
    public void entrarEnWarp(){
        Bitacora.getInstance().registrar("ERROR: no se puede entrar en warp sin preparar el salto");
    }

    @Override
    public void iniciarEnfriamiento() {
        Bitacora.getInstance().registrar("ERROR: no requiere enfriamiento");
    }

    @Override
    public void volverADisponible() {
        Bitacora.getInstance().registrar("el motor permanece disponible");
    }
}