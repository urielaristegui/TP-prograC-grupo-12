
package modelo.elementos;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * Representa un obstaculo espacial que resta energia
 * a la nave cuando ocurre una colision.
 *
 * Invariante: danioEnergia > 0.
 */
public final class Asteroide implements ElementoDelUniverso {

    private final int danioEnergia;

    /**
     * Crea un asteroide con un danio fijo.
     *
     * Pre: danioEnergia > 0.
     * Post: se crea el asteroide con el danio indicado.
     *
     * @param danioEnergia puntos de energia a descontar
     * @throws IllegalArgumentException si danioEnergia <= 0
     */
    public Asteroide(int danioEnergia) {
        if (danioEnergia <= 0) {
            throw new IllegalArgumentException(
                    "El danio del asteroide debe ser positivo"
            );
        }
        this.danioEnergia = danioEnergia;
    }

    /**
     * Consulta el danio del asteroide.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return puntos de energia que descuenta
     */
    public int getDanioEnergia() {
        return danioEnergia;
    }

    /**
     * Aplica las consecuencias de la colision.
     *
     * Pre: nave != null.
     * Post: descuenta energia sin bajar de cero.
     * No modifica combustible ni desgaste.
     *
     * @param nave nave que colisiona
     * @throws IllegalArgumentException si nave es null
     */
    @Override
    public void interactuar(Nave nave) {
        if (nave == null) {
            throw new IllegalArgumentException(
                    "La nave no puede ser null"
            );
        }

        int energiaAnterior = nave.getEnergia();

        nave.descontarEnergia(danioEnergia);

        int energiaPerdida =
                energiaAnterior - nave.getEnergia();

        Bitacora.getInstance().registrar(
                "Colision con asteroide: -" + energiaPerdida
                        + " de energia. Energia restante: "
                        + nave.getEnergia()
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
        return "Asteroide";
    }
}
