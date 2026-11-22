package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Terricola extends DecoratorPlaneta {
    private final double subsidio= 20;

    /**
     * construye un decorador de origen terricola que envuelve a un haber.<br>
     * <b>pre:</b> haber != null.<br>
     * <b>post:</b> se asocia el haber envuelto mediante super.setHaber(haber).<br>
     *
     * @param haber componente de haber a decorar. haber != null.
     */
    public Terricola(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 20 PG de subsidio fijo para el planeta Marte
        return getHaber().getSueldo() + subsidio;
    }
}