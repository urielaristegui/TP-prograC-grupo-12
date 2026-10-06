package modelo.Tripulacion;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.Tripulacion.Identidad;
import modelo.misiones.Mision;
import modelo.Tripulacion.Tripulante;

public class Capitan extends Tripulante{
    private static Capitan instancia=null;
    private AsistenteDeComandos ANav;
    
    private Capitan(Identidad id,double ant,AsistenteDeComandos anav){
        super(id,ant);
        super.cargo= "Capitan";
        ANav= anav;
    }
    @Override
    public double getSueldo(){
        return 1000 + (1000 * 0.2 * antiguedad);
    }
    /**
     *Patron singleton para hacer solo 1 capitan.
     */
    public static Capitan getInstancia(Identidad id,double ant,AsistenteDeComandos anav){
        if (instancia == null)
            return instancia= new Capitan(id,ant,anav);
        else
            return instancia;
    }
    
    public void ejecutarMision(Mision M){
        ANav.ejecutarMision(M); 
    }
    public void echarTripulante(){}
}
