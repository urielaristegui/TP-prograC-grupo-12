package modelo.motorWarp;

import modelo.bitacora.Bitacora;

public class EnWarp implements modelo.motorWarp.State {
    private MotorWarp motorWarp;

    /**
     - constructor de la clase EnWarp<br>
     - <b>pre:</b>motorWarp != null  <br>
     - <b>post:</b> se inicializa el atributo motorWarp <br>
     */
    public EnWarp(MotorWarp motorWarp) {
        this.motorWarp = motorWarp;
    }

    @Override
    public void entrarEnWarp() {
        Bitacora.getInstance().registrar("el motor permanece en warp");
    }

    @Override
    public void iniciarEnfriamiento() {
        motorWarp.setState(new Enfriamento(motorWarp));
        Bitacora.getInstance().registrar("iniciando enfriamiento");
    }

    @Override
    public void volverADisponible() {
        Bitacora.getInstance().registrar("ERROR: el motor no puede volver a disponible sin haberse enfriado");
    }


    @Override
    public void prepararSalto() {
        Bitacora.getInstance().registrar("ERROR: el motor no puede preparar el salto en warp");
    }
}