package Tripulacion;

import java.util.ArrayList;

public class Consejero extends Tripulante{
    
    private ArrayList<String> LConsejos= new ArrayList<>();
    
    public Consejero(Identidad id,double ant){
        super(id,ant);
        super.cargo= "Consejero";
    }
    
    @Override
    public double getSueldo(){
        return 600 + (600 * 0.05 * antiguedad);
    }
    
    public void registrarConsejo(String consejo){
        LConsejos.add(consejo);
    }
}
