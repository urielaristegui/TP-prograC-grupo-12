package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;


public class Teniente extends Tripulante{
    private double sueldob= 400;
    private double bonusant= 0.03;

    /**
     * construye un tripulante con cargo de teniente.<br>
     * <b>pre:</b> id != null y ant >= 0.<br>
     * <b>post:</b> se inicializa el teniente con su identidad, antiguedad y cargo "Teniente".<br>
     *
     * @param id identidad asignada al teniente. id != null.
     * @param ant antiguedad del teniente en anios. ant >= 0.
     */
    public Teniente(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Teniente";
    }

    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad);
    }
}