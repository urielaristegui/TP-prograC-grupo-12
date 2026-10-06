package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;

public class Teniente extends Tripulante{
    public Teniente(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Teniente";    
    }
    
    @Override
    public double getSueldo(){
        return 400 + (400 * 0.03 * antiguedad);
    }
}
