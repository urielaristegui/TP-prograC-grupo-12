package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Vulcano extends DecoratorPlaneta {
    private subsidio= 30;

    public Vulcano(Haber haber){
        super.setHaber(haber);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta vulcan
        return getHaber().getSueldo() + subsidio;
    }
}
