package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;
/**
 * Patron decorator para calculo de haberes para los tripulantes.
 */

public abstract class DecoratorPlaneta implements Haber {
    private Haber haber;

    public Haber getHaber(){
        return haber;
    }
    public void setHaber(Haber haber){
        this.haber= haber;
    }
}
