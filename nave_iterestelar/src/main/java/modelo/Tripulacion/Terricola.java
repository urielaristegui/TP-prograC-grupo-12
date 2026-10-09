package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Terricola extends DecoratorPlaneta {
    private subsidio= 20;

    public Terricola(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta Marte
        return getHaber().getSueldo() + subsidio;
    }
}
