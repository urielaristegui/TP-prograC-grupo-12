package modelo.nave;

/**
 * Nave de tipo carguero.
 *
 * Conserva los recursos iniciales de la Parte 1.
 * Implementa Double Dispatch para resolver enfrentamientos.
 *
 * Invariantes:
 * - Se respetan los invariantes heredados de Nave.
 * - El resultado del combate depende solo de los tipos.
 */
public class NaveCarguero extends Nave {

    /**
     * Crea una nave carguero.
     *
     * Pre: ninguna.
     * Post:
     * - Combustible inicial = 100.
     * - Energia inicial = 60.
     * - Desgaste inicial = 0.
     * - Tripulacion vacia.
     *
     * @param id identificador de la nave
     */
    NaveCarguero(int id) {
        super(id, 100, 60);
    }

    /**
     * Inicia el Double Dispatch desde el carguero.
     *
     * Pre:
     * - rival != null.
     * - rival != this.
     * - ninguna nave destruida.
     *
     * Post:
     * - Devuelve el resultado desde la perspectiva
     *   de este carguero.
     * - No modifica los estados.
     *
     * @param rival nave enemiga
     * @return resultado del enfrentamiento
     * @throws IllegalArgumentException si el rival es invalido
     * @throws IllegalStateException si alguna nave esta destruida
     */
    @Override
    public ResultadoCombate enfrentar(Nave rival) {
        validarRival(rival);

        return rival.recibirEncuentroDe(this);
    }

    /**
     * Encuentro: Exploradora propia vs Carguero enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve GANA sin modificar estados.
     *
     * @param navePropia exploradora del jugador
     * @return GANA
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveExploradora navePropia) {

        return ResultadoCombate.GANA;
    }

    /**
     * Encuentro: Carguero propio vs Carguero enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve PACTO_DE_PAZ sin modificar estados.
     *
     * @param navePropia carguero del jugador
     * @return PACTO_DE_PAZ
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveCarguero navePropia) {

        return ResultadoCombate.PACTO_DE_PAZ;
    }

    /**
     * Encuentro: Combate propio vs Carguero enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve PIERDE sin modificar estados.
     *
     * @param navePropia nave de combate del jugador
     * @return PIERDE
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveCombate navePropia) {

        return ResultadoCombate.PIERDE;
    }

    /**
     * Devuelve la representacion textual de la nave.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return identificacion de la nave
     */
    @Override
    public String toString() {
        return "Nave Carguero #" + getId();
    }
}