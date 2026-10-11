
package modelo.nave;

/**
 * Fábrica encargada de crear los distintos tipos de naves.
 *
 * Implementa el patrón Simple Factory, centralizando
 * la creación de objetos de las subclases de Nave.
 *
 * No permite instanciación ni herencia.
 */
public final class NaveFactory {

    private NaveFactory() {
        // Evita instanciar la fábrica.
    }

    /**
     * Crea una nave del tipo solicitado.
     *
     * <b>Precondiciones:</b>
     * - El tipo de nave no debe ser null.
     *
     * <b>Postcondiciones:</b>
     * - Se devuelve una nueva instancia del tipo solicitado.
     * - La nave posee el ID indicado.
     * - Sus recursos iniciales corresponden a su tipo.
     * - La nave inicia con desgaste cero y tripulación vacía.
     *
     * @param tipo tipo de nave a crear
     * @param id identificador de la nave
     * @return nueva instancia de Nave
     * @throws IllegalArgumentException si tipo es null
     */
    public static Nave crearNave(TipoNave tipo, int id) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de nave no puede ser null"
            );
        }

        return switch (tipo) {
            case EXPLORADORA -> new NaveExploradora(id);
            case CARGUERO -> new NaveCarguero(id);
            case COMBATE -> new NaveCombate(id);
        };
    }
}
