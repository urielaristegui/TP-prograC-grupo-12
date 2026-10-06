package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;
/**
 * Patron decorator para calculo de haberes para los tripulantes.
 */

public abstract class DecoratorPlaneta extends Tripulante {
    protected Tripulante tripulanteDecorado;

    public DecoratorPlaneta(Tripulante tripulanteDecorado) {

        super(tripulanteDecorado.getID(), tripulanteDecorado.getAntiguedad());
        this.tripulanteDecorado = tripulanteDecorado;
        this.cargo = tripulanteDecorado.getCargo();
        this.origen = tripulanteDecorado.getOrigen();
    }

    @Override
    public abstract double getSueldo();
}
