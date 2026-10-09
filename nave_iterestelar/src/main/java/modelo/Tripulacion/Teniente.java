package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;


public class Teniente extends Tripulante{
    private double sueldob= 400;
    private double bonusant= 0.03;

    public Teniente(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Teniente";    
    }
    
    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad);
    }
}
