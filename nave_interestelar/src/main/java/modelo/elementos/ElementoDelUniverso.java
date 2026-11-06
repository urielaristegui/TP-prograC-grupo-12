
package modelo.elementos;

import modelo.nave.Nave;

/**
 * interfaz que representa cualquier elemento espacial presente en el tablero.<br>
 * define el contrato de interaccion que deben implementar todos los objetos del universo.<br>
 */
public interface ElementoDelUniverso {

    /**
     * ejecuta la interaccion especifica entre el elemento del espacio y la nave.<br>
     * <b>pre:</b> nave != null.<br>
     * <b>post:</b> aplica las consecuencias o beneficios sobre el estado de la nave.<br>
     * 
     * @param nave referencia a la nave que interactua con el elemento.
     */
    void interactuar(Nave nave);

    public String toString();
}
