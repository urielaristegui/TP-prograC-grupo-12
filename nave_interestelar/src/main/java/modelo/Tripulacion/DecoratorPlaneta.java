package modelo.Tripulacion;
import modelo.Tripulacion.Tripulante;
import modelo.Tripulacion.DecoratorPlaneta;
/**
 * Clase abstracta de Patron decorator para calculo de haberes para los tripulantes.
 */

public abstract class DecoratorPlaneta implements Haber {
    private Haber haber;

    /**
     * Metodos de referencia a los tripulantes. Pide y devuelve Haber válido.
     */
    public Haber getHaber(){
        return haber;
    }
    public void setHaber(Haber haber){
        this.haber= haber;
    }
}
