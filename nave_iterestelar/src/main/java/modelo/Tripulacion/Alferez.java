package modelo.Tripulacion;

public class Alferez extends Tripulante{
    public Alferez(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Alferez";
    }
    
    @Override
    public double getSueldo(){
        return 200 + (200 * 0.005 * antiguedad);
    }
}
