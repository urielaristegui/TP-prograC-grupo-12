
package modelo.elementos;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * Representa una estacion espacial que permite
 * recuperar combustible y energia.
 *
 * Invariantes:
 * combustibleARecargar > 0.
 * energiaARecargar > 0.
 */
public final class EstacionDeCarga implements ElementoDelUniverso {

    private static final int CAPACIDAD_MAXIMA = 100;

    private final int combustibleARecargar;
    private final int energiaARecargar;

    /**
     * Crea una estacion de carga.
     *
     * Pre: ambas cantidades deben ser positivas.
     * Post: almacena las cantidades de recarga.
     *
     * @param combustibleARecargar combustible a suministrar
     * @param energiaARecargar energia a suministrar
     * @throws IllegalArgumentException si alguna cantidad <= 0
     */
    public EstacionDeCarga(
            int combustibleARecargar,
            int energiaARecargar) {

        if (combustibleARecargar <= 0 ||
                energiaARecargar <= 0) {
            throw new IllegalArgumentException(
                    "Las cantidades a recargar deben ser positivas"
            );
        }

        this.combustibleARecargar = combustibleARecargar;
        this.energiaARecargar = energiaARecargar;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return combustible que ofrece la estacion
     */
    public int getCombustibleARecargar() {
        return combustibleARecargar;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return energia que ofrece la estacion
     */
    public int getEnergiaARecargar() {
        return energiaARecargar;
    }

    /**
     * Recarga los recursos de la nave.
     *
     * Pre: nave != null.
     * Post: aumenta combustible y energia sin superar
     * la capacidad maxima de 100.
     * No modifica desgaste ni tripulacion.
     *
     * @param nave nave que utiliza la estacion
     * @throws IllegalArgumentException si nave es null
     */
    @Override
    public void interactuar(Nave nave) {
        if (nave == null) {
            throw new IllegalArgumentException(
                    "La nave no puede ser null"
            );
        }

        int combustibleCargado = Math.min(
                combustibleARecargar,
                CAPACIDAD_MAXIMA - nave.getCombustible()
        );

        int energiaCargada = Math.min(
                energiaARecargar,
                CAPACIDAD_MAXIMA - nave.getEnergia()
        );

        if (combustibleCargado > 0) {
            nave.cargarCombustible(combustibleCargado);
        }

        if (energiaCargada > 0) {
            nave.cargarEnergia(energiaCargada);
        }

        Bitacora.getInstance().registrar(
                "Estacion de carga: +"
                        + combustibleCargado + " de combustible, +"
                        + energiaCargada + " de energia."
                        + " Totales: combustible="
                        + nave.getCombustible()
                        + ", energia=" + nave.getEnergia()
        );
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return nombre del elemento
     */
    @Override
    public String toString() {
        return "Estacion de carga";
    }
}
