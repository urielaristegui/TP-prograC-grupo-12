package modelo.Tripulacion;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.Tripulacion.Identidad;
import modelo.misiones.Mision;
import modelo.Tripulacion.Tripulante;

public class Capitan extends Tripulante{
    private static Capitan instancia=null;
    private AsistenteDeComandos ANav;
    private double sueldob= 1000;
    private double bonusant= 0.2;

    /**
     * construye una instancia privada de capitan.<br>
     * <b>pre:</b> id != null, ant >= 0.<br>
     * <b>post:</b> se inicializa el capitan con su identidad, antiguedad, cargo "Capitan" y asistente de comandos.<br>
     *
     * @param id identidad asignada al capitan. id != null.
     * @param ant antiguedad del capitan en anios. ant >= 0.
     * @param anav asistente de comandos asociado al capitan.
     */
    private Capitan(Identidad id,double ant,AsistenteDeComandos anav){
        super(id,ant);
        super.cargo= "Capitan";
        ANav= anav;
    }

    @Override
    public double getSueldo(){
        return sueldob + (sueldob * bonusant * antiguedad);
    }

    /**
     * obtiene la instancia unica de capitan (singleton), creandola si aun no existe.<br>
     * <b>pre:</b> id != null, ant >= 0.<br>
     * <b>post:</b> retorna la instancia unica de Capitan.<br>
     *
     * @param id identidad asignada al capitan. id != null.
     * @param ant antiguedad del capitan en anios. ant >= 0.
     * @param anav asistente de comandos asociado al capitan.
     * @return instancia unica de Capitan.
     */
    public static Capitan getInstancia(Identidad id,double ant,AsistenteDeComandos anav){
        if (instancia == null)
            return instancia= new Capitan(id,ant,anav);
        else
            return instancia;
    }

    /**
     * ejecuta la mision indicada invocando su metodo director.<br>
     * <b>pre:</b> M != null.<br>
     * <b>post:</b> se inicia la ejecucion de la mision mediante M.director().<br>
     *
     * @param M mision a ejecutar. M != null.
     */
    public void ejecutarMision(Mision M){
        M.director();
    }

    /**
     * expulsa o remueve a un tripulante de la operacion.<br>
     * <b>pre:</b> el capitan esta inicializado.<br>
     * <b>post:</b> ejecuta la accion correspondiente sobre el tripulante.<br>
     */
    public void echarTripulante(){}
}