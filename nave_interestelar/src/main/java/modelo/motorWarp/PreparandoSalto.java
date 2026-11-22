package modelo.motorWarp;


import modelo.bitacora.Bitacora;

public class PreparandoSalto implements modelo.motorWarp.State {
    private MotorWarp motorWarp;

    /**
     - constructor de la clase PreparandoSalto<br>
     - <b>pre:</b>motorWarp != null  <br>
     - <b>post:</b> se inicializa el atributo motorWarp <br>
     */
    public PreparandoSalto(MotorWarp motorWarp) {
        this.motorWarp = motorWarp;
    }

    @Override
    public void prepararSalto() {
        Bitacora.getInstance().registrar("ERROR: el motor ya esta preparando el salto");
    }

    @Override
    public void entrarEnWarp() {
        motorWarp.setState(new EnWarp(motorWarp));
        Bitacora.getInstance().registrar("entrando en warp");
    }

    @Override
    public void iniciarEnfriamiento() {
        Bitacora.getInstance().registrar("ERROR: el motor no puede iniciar el enfriamiento sin haber terminado el salto");
    }

    @Override
    public void volverADisponible() {
        Bitacora.getInstance().registrar("ERROR:el motor no puede volver a disponible sin haber terminado el salto");
    }
}