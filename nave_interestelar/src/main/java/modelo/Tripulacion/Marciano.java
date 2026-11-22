package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Marciano extends DecoratorPlaneta {
    private final double subsidio= 18;

    /**
     * construye un decorador de origen marciano asociado al haber indicado.<br>
     * <b>pre:</b> haber != null.<br>
     * <b>post:</b> establece haber como el componente decorado mediante super.setHaber(haber).<br>
     *
     * @param haber haber base o decorado a envolver. haber != null.
     */
    public Marciano(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 18 PG de subsidio fijo para el planeta Marte
        return getHaber().getSueldo() + subsidio;
    }
}