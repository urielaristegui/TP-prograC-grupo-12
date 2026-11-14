
package modelo.elementos;

import modelo.bitacora.Bitacora;
import modelo.nave.Nave;

/**
 * Representa un recurso coleccionable del universo.
 *
 * Invariantes:
 * - cantidad > 0.
 * - Una gema recolectada no puede entregar recursos otra vez.
 */
public final class Gema implements ElementoDelUniverso {

    private final int cantidad;
    private boolean recolectada;

    /**
     * Crea una gema disponible para recolectar.
     *
     * Pre: cantidad > 0.
     * Post:
     * - Almacena la cantidad indicada.
     * - recolectada = false.
     *
     * @param cantidad cantidad de gemas que otorga
     * @throws IllegalArgumentException si cantidad <= 0
     */
    public Gema(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de gemas debe ser positiva"
            );
        }

        this.cantidad = cantidad;
        this.recolectada = false;
    }

    /**
     * Consulta la cantidad de gemas.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return cantidad de gemas que ofrece
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Consulta si fue recolectada.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return true si ya fue recolectada
     */
    public boolean estaRecolectada() {
        return recolectada;
    }

    /**
     * Recolecta la gema y la agrega a la nave.
     *
     * Pre:
     * - nave != null.
     * - La nave no esta destruida.
     *
     * Post:
     * - Si estaba disponible, incrementa las gemas
     *   de la nave y marca recolectada = true.
     * - Si ya estaba recolectada, no modifica la nave.
     * - No modifica energia, combustible ni desgaste.
     *
     * @param nave nave que recolecta la gema
     * @throws IllegalArgumentException si nave es null
     * @throws IllegalStateException si la nave esta destruida
     *         o no puede incorporar la cantidad de gemas
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
                    "Una nave destruida no puede recolectar gemas"
            );
        }

        if (recolectada) {
            return;
        }

        nave.agregarGemas(cantidad);

        recolectada = true;

        Bitacora.getInstance().registrar(
                "Gemas recolectadas: +" + cantidad
                        + ". Total: " + nave.getGemas()
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
        return "Gema";
    }
}
