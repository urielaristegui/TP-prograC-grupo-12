package modelo.Tripulacion;

public class Alferez extends Tripulante{
    private double sueldob= 200;
    private double bonusant= 0.005;
    public Alferez(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Alferez";
    }
    
    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad);
    }
}
