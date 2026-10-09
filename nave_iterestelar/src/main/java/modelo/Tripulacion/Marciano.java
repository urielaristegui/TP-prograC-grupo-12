package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Marciano extends DecoratorPlaneta {
    private subsidio= 18;

    public Marciano(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta Marte
        return getHaber().getSueldo() + subsidio;
    }
}