package modelo.Tripulacion;

public class Alferez extends Tripulante{
    private double sueldob= 200;
    private double bonusant= 0.005;

    /**
     * construye un tripulante con cargo de alferez.<br>
     * <b>pre:</b> id != null y ant >= 0.<br>
     * <b>post:</b> se inicializa el alferez con su identidad, antiguedad y cargo "Alferez".<br>
     *
     * @param id identidad asignada al alferez. id != null.
     * @param ant antiguedad del alferez en anios. ant >= 0.
     */
    public Alferez(Identidad id,double ant) {
        super(id,ant);
        super.cargo= "Alferez";
    }

    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad);
    }
}