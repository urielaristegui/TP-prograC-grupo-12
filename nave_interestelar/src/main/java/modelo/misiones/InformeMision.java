package modelo.misiones;

import java.util.List;

/**
 * informe minimo de mision inmutable que almacena los datos relevantes al cierre de una mision.<br>
 * <b>invariante:</b> accionesPrincipales != null y la instancia no se modifica una vez construida.
 */
public final class InformeMision {

    private final String misionEjecutada;
    private final ResultadoMision resultado;
    private final List<String> accionesPrincipales;
    private final String recursosConsumidos;
    private final String estadoNave;
    private final String observaciones;

    /**
     * construye un informe minimo de mision inmutable.<br>
     * <b>pre:</b> accionesPrincipales != null.<br>
     * <b>post:</b> inicializa todos los campos del informe y copia defensivamente accionesPrincipales como lista inmodificable.<br>
     *
     * @param misionEjecutada nombre o identificador de la mision ejecutada.
     * @param resultado resultado final obtenido en la mision.
     * @param accionesPrincipales lista de acciones principales realizadas. accionesPrincipales != null.
     * @param recursosConsumidos descripcion de los recursos consumidos durante la mision.
     * @param estadoNave estado final de la nave al cierre de la mision.
     * @param observaciones observaciones relevantes registradas.
     */
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

    /**
     * devuelve el nombre o identificador de la mision ejecutada.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna misionEjecutada sin modificar el objeto.<br>
     *
     * @return mision ejecutada.
     */
    public String getMisionEjecutada() { return misionEjecutada; }

    /**
     * devuelve el resultado final de la mision.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna resultado sin modificar el objeto.<br>
     *
     * @return resultado de la mision.
     */
    public ResultadoMision getResultado() { return resultado; }

    /**
     * devuelve la lista inmodificable de acciones principales.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna accionesPrincipales sin modificar el objeto.<br>
     *
     * @return lista de acciones principales.
     */
    public List<String> getAccionesPrincipales() { return accionesPrincipales; }

    /**
     * devuelve la descripcion de los recursos consumidos.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna recursosConsumidos sin modificar el objeto.<br>
     *
     * @return recursos consumidos.
     */
    public String getRecursosConsumidos() { return recursosConsumidos; }

    /**
     * devuelve el estado final de la nave.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna estadoNave sin modificar el objeto.<br>
     *
     * @return estado final de la nave.
     */
    public String getEstadoNave() { return estadoNave; }

    /**
     * devuelve las observaciones relevantes del informe.<br>
     * <b>pre:</b> el informe esta inicializado.<br>
     * <b>post:</b> retorna observaciones sin modificar el objeto.<br>
     *
     * @return observaciones registradas.
     */
    public String getObservaciones() { return observaciones; }

    @Override
    public String toString() {
        return "Informe[" + misionEjecutada + " -> " + resultado
                + ", recursos=" + recursosConsumidos
                + ", estadoNave=" + estadoNave + "]";
    }
}