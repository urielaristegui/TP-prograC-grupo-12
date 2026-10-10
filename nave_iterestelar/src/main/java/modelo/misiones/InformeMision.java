package modelo.misiones;

import java.util.List;

/**
 * E1-10 — Informe mínimo de misión.
 * Guarda exactamente lo que pide el requerimiento: misión ejecutada,
 * resultado, acciones principales, recursos consumidos, estado final
 * de la nave y observaciones relevantes.
 *
 * Objeto inmutable (sólo getters) para que, una vez cerrada la misión,
 * el informe no se pueda alterar. Los campos quedan como datos sueltos
 * (no como un único texto formateado) para que distintos clientes -una
 * consola hoy, una pantalla Swing en E2- puedan mostrarlos como quieran,
 * sin tener que parsear un string armado por el dominio.
 */
public final class InformeMision {

    private final String misionEjecutada;
    private final ResultadoMision resultado;
    private final List<String> accionesPrincipales;
    private final String recursosConsumidos;
    private final String estadoNave;
    private final String observaciones;

    public InformeMision(String misionEjecutada, ResultadoMision resultado,
                         List<String> accionesPrincipales, String recursosConsumidos,
                         String estadoNave, String observaciones) {
        this.misionEjecutada = misionEjecutada;
        this.resultado = resultado;
        this.accionesPrincipales = List.copyOf(accionesPrincipales);
        this.recursosConsumidos = recursosConsumidos;
        this.estadoNave = estadoNave;
        this.observaciones = observaciones;
    }

    public String getMisionEjecutada() { return misionEjecutada; }
    public ResultadoMision getResultado() { return resultado; }
    public List<String> getAccionesPrincipales() { return accionesPrincipales; }
    public String getRecursosConsumidos() { return recursosConsumidos; }
    public String getEstadoNave() { return estadoNave; }
    public String getObservaciones() { return observaciones; }

    @Override
    public String toString() {
        return "Informe[" + misionEjecutada + " -> " + resultado
                + ", recursos=" + recursosConsumidos
                + ", estadoNave=" + estadoNave + "]";
    }
}