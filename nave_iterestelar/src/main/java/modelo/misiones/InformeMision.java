package modelo.misiones;

import java.util.Collections;
import java.util.List;

/**
 * E1-10 — Informe mínimo de misión.
 * Objeto inmutable: se arma una sola vez, en Mision.cerrar(), con el
 * resultado ya evaluado. No tiene setters.
 *
 * LIMITACIÓN ACTUAL: objetivoEvaluado, accionesRealizadas, ruta y
 * observaciones quedan vacíos por ahora. Las misiones concretas
 * (M1/M2/M3) todavía son "carteles" sin comportamiento detallado
 * (ver aclaración del TP) -- se completan cuando se definan de verdad.
 */
public final class InformeMision {

    private final String misionEjecutada;
    private final String objetivoEvaluado;
    private final List<String> accionesRealizadas;
    private final List<String> ruta;
    private final List<String> eventos;
    private final String recursosConsumidos;
    private final String resultadoFinal;
    private final String estadoNave;
    private final String observaciones;

    private InformeMision(Builder b) {
        this.misionEjecutada = b.misionEjecutada;
        this.objetivoEvaluado = b.objetivoEvaluado;
        this.accionesRealizadas = List.copyOf(b.accionesRealizadas);
        this.ruta = List.copyOf(b.ruta);
        this.eventos = List.copyOf(b.eventos);
        this.recursosConsumidos = b.recursosConsumidos;
        this.resultadoFinal = b.resultadoFinal;
        this.estadoNave = b.estadoNave;
        this.observaciones = b.observaciones;
    }

    public String getMisionEjecutada() { return misionEjecutada; }
    public String getObjetivoEvaluado() { return objetivoEvaluado; }
    public List<String> getAccionesRealizadas() { return accionesRealizadas; }
    public List<String> getRuta() { return ruta; }
    public List<String> getEventos() { return eventos; }
    public String getRecursosConsumidos() { return recursosConsumidos; }
    public String getResultadoFinal() { return resultadoFinal; }
    public String getEstadoNave() { return estadoNave; }
    public String getObservaciones() { return observaciones; }

    @Override
    public String toString() {
        return "Informe[" + misionEjecutada + " -> " + resultadoFinal
                + ", recursos=" + recursosConsumidos
                + ", estadoNave=" + estadoNave
                + ", eventos=" + eventos.size() + "]";
    }

    public static class Builder {
        private String misionEjecutada = "";
        private String objetivoEvaluado = "";
        private List<String> accionesRealizadas = Collections.emptyList();
        private List<String> ruta = Collections.emptyList();
        private List<String> eventos = Collections.emptyList();
        private String recursosConsumidos = "";
        private String resultadoFinal = "";
        private String estadoNave = "";
        private String observaciones = "";

        public Builder misionEjecutada(String v) { this.misionEjecutada = v; return this; }
        public Builder objetivoEvaluado(String v) { this.objetivoEvaluado = v; return this; }
        public Builder accionesRealizadas(List<String> v) { this.accionesRealizadas = v; return this; }
        public Builder ruta(List<String> v) { this.ruta = v; return this; }
        public Builder eventos(List<String> v) { this.eventos = v; return this; }
        public Builder recursosConsumidos(String v) { this.recursosConsumidos = v; return this; }
        public Builder resultadoFinal(String v) { this.resultadoFinal = v; return this; }
        public Builder estadoNave(String v) { this.estadoNave = v; return this; }
        public Builder observaciones(String v) { this.observaciones = v; return this; }

        public InformeMision build() {
            if (misionEjecutada.isEmpty() || resultadoFinal.isEmpty()) {
                throw new IllegalStateException("Informe incompleto: falta misión ejecutada o resultado final");
            }
            return new InformeMision(this);
        }
    }
}