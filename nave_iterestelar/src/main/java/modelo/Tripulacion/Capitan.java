import Tripulacion.Asistente;
import Tripulacion.Identidad;
import Tripulacion.Mision;
import Tripulacion.Tripulante;

public class Capitan extends Tripulante{
    private static Capitan instancia=null;
    private Asistente ANav;
    
    private Capitan(Identidad id,double ant,Asistente anav){
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
    public static Capitan getInstancia(Identidad id,double ant,Asistente anav){
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
