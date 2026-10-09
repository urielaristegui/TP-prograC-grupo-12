package modelo.Tripulacion;
import  modelo.Tripulacion.Tripulante;
import java.util.ArrayList;

public class Consejero extends Tripulante{
    private double sueldob= 600;
    private double bonusant = 0.05;
    
    private ArrayList<String> LConsejos= new ArrayList<>();
    
    public Consejero(Identidad id,double ant){
        super(id,ant);
        super.cargo= "Consejero";
    }
    
    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad) + 2 * LConsejos.length;
    }
    
    public void registrarConsejo(String consejo){
        LConsejos.add(consejo);
    }
}
