package Tripulacion;
import Tripulacion.Tripulante;

public class Marciano extends DecoratorPlaneta {

    public Marciano(Tripulante tripulanteDecorado) {
        super(tripulanteDecorado);
    }

    @Override
    public double getSueldo() {
        // Suma los 30 PG de subsidio fijo para el planeta Vulcano
        return tripulanteDecorado.getSueldo() + 18.0;
    }
}