package Tripulacion;
import Tripulacion.Tripulante;

public class Terricola extends DecoratorPlaneta {

    public Terricola(Tripulante tripulanteDecorado) {
        super(tripulanteDecorado);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta Vulcano
        return tripulanteDecorado.getSueldo() + 20.0;
    }
}
