package modelo.nave;

/**
 * Nave de tipo combate.
 *
 * Conserva los recursos iniciales de la Parte 1.
 * Implementa Double Dispatch para resolver enfrentamientos.
 *
 * Invariantes:
 * - Se respetan los invariantes heredados de Nave.
 * - El resultado del combate depende solo de los tipos.
 */
public class NaveCombate extends Nave {

    /**
     * Crea una nave de combate.
     *
     * Pre: ninguna.
     * Post:
     * - Combustible inicial = 80.
     * - Energia inicial = 100.
     * - Desgaste inicial = 0.
     * - Tripulacion vacia.
     *
     * @param id identificador de la nave
     */
    NaveCombate(int id) {
        super(id, 80, 100);
    }

    /**
     * Inicia el Double Dispatch desde la nave de combate.
     *
     * Pre:
     * - rival != null.
     * - rival != this.
     * - ninguna nave destruida.
     *
     * Post:
     * - Devuelve el resultado desde la perspectiva
     *   de esta nave.
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
     * Encuentro: Exploradora propia vs Combate enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve PIERDE sin modificar estados.
     *
     * @param navePropia exploradora del jugador
     * @return PIERDE
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveExploradora navePropia) {

        return ResultadoCombate.PIERDE;
    }

    /**
     * Encuentro: Carguero propio vs Combate enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve GANA sin modificar estados.
     *
     * @param navePropia carguero del jugador
     * @return GANA
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveCarguero navePropia) {

        return ResultadoCombate.GANA;
    }

    /**
     * Encuentro: Combate propio vs Combate enemigo.
     *
     * Pre: navePropia != null.
     * Post: devuelve PACTO_DE_PAZ sin modificar estados.
     *
     * @param navePropia nave de combate del jugador
     * @return PACTO_DE_PAZ
     */
    @Override
    protected ResultadoCombate recibirEncuentroDe(
            NaveCombate navePropia) {

        return ResultadoCombate.PACTO_DE_PAZ;
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
        return "Nave Combate #" + getId();
    }
}