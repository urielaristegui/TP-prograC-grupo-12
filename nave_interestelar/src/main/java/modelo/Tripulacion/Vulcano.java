package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Vulcano extends DecoratorPlaneta {
    private final double subsidio= 30;

    /**
     * construye un decorador de origen vulcano que envuelve a un haber.<br>
     * <b>pre:</b> haber != null.<br>
     * <b>post:</b> se asocia el haber envuelto mediante super.setHaber(haber).<br>
     *
     * @param haber componente de haber a decorar. haber != null.
     */
    public Vulcano(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta vulcan
        return getHaber().getSueldo() + subsidio;
    }
}