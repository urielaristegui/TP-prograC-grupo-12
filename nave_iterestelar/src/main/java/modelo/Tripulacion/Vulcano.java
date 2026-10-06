package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;

public class Vulcano extends DecoratorPlaneta {

    public Vulcano(Tripulante tripulanteDecorado) {
        super(tripulanteDecorado);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta Vulcano
        return tripulanteDecorado.getSueldo() + 30.0;
    }
}
