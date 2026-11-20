
package modelo.elementos;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * Representa un fenomeno espacial que afecta
 * negativamente los recursos de una nave.
 *
 * Invariantes:
 * - perdidaEnergia >= 0.
 * - perdidaCombustible >= 0.
 * - desgasteGenerado >= 0.
 * - Al menos uno de los efectos es positivo.
 */
public final class Anomalia implements ElementoDelUniverso {

    private final int perdidaEnergia;
    private final int perdidaCombustible;
    private final int desgasteGenerado;

    /**
     * Crea una anomalia con efectos configurables.
     *
     * Pre:
     * - Todas las cantidades >= 0.
     * - Al menos una cantidad > 0.
     *
     * Post:
     * - Almacena los efectos de la anomalia.
     * - No modifica ninguna nave.
     *
     * @param perdidaEnergia energia a descontar
     * @param perdidaCombustible combustible a descontar
     * @param desgasteGenerado desgaste a incrementar
     * @throws IllegalArgumentException si alguna cantidad
     *         es negativa o todas son cero
     */
    public Anomalia(
            int perdidaEnergia,
            int perdidaCombustible,
            int desgasteGenerado) {

        if (perdidaEnergia < 0 ||
                perdidaCombustible < 0 ||
                desgasteGenerado < 0 ||
                (perdidaEnergia == 0 &&
                        perdidaCombustible == 0 &&
                        desgasteGenerado == 0)) {

            throw new IllegalArgumentException(
                    "Los efectos deben ser no negativos "
                            + "y al menos uno debe ser positivo"
            );
        }

        this.perdidaEnergia = perdidaEnergia;
        this.perdidaCombustible = perdidaCombustible;
        this.desgasteGenerado = desgasteGenerado;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return energia que descuenta
     */
    public int getPerdidaEnergia() {
        return perdidaEnergia;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return combustible que descuenta
     */
    public int getPerdidaCombustible() {
        return perdidaCombustible;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return desgaste que genera
     */
    public int getDesgasteGenerado() {
        return desgasteGenerado;
    }

    /**
     * Aplica los efectos de la anomalia sobre la nave.
     *
     * Pre:
     * - nave != null.
     * - La nave no esta destruida.
     *
     * Post:
     * - Descuenta energia sin bajar de cero.
     * - Descuenta combustible sin bajar de cero.
     * - Incrementa desgaste sin superar 100.
     * - No modifica las gemas ni la tripulacion.
     *
     * La verificacion de destruccion de la nave
     * corresponde a Universo.
     *
     * @param nave nave afectada por la anomalia
     * @throws IllegalArgumentException si nave es null
     * @throws IllegalStateException si nave esta destruida
     */
    @Override
    public void interactuar(Nave nave) {

        if (nave == null) {
            throw new IllegalArgumentException(
                    "La nave no puede ser null"
            );
        }

        if (nave.estaDestruida()) {
            throw new IllegalStateException(
                    "Una nave destruida no puede interactuar"
            );
        }

        if (perdidaEnergia > 0) {
            nave.descontarEnergia(perdidaEnergia);
        }

        if (perdidaCombustible > 0) {
            nave.descontarCombustible(perdidaCombustible);
        }

        if (desgasteGenerado > 0) {
            nave.generarDesgaste(desgasteGenerado);
        }

        Bitacora.getInstance().registrar(
                "Anomalia: -" + perdidaEnergia
                        + " energia, -" + perdidaCombustible
                        + " combustible, +" + desgasteGenerado
                        + " desgaste."
        );
    }

    /**
     * Devuelve la representacion textual.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return nombre del elemento
     */
    @Override
    public String toString() {
        return "Anomalia";
    }
}
