
package modelo.Tripulacion;

import java.util.ArrayList;

/**
 * Representa a un Consejero de la nave.
 *
 * Cada consejo registrado incrementa
 * su haber en 2 PG.
 */
public class Consejero extends Tripulante {

    private double sueldob = 600;
    private double bonusant = 0.05;

    private ArrayList<String> LConsejos =
            new ArrayList<>();

    /**
     * Pre: identidad valida y antiguedad no negativa.
     * Post: crea un tripulante con cargo Consejero.
     */
    public Consejero(Identidad id, double ant) {

        super(id, ant);
        super.cargo = "Consejero";
    }

    /**
     * Calcula el sueldo del Consejero.
     *
     * Post: no modifica el estado.
     *
     * @return sueldo base, antiguedad y consejos
     */
    @Override
    public double getSueldo() {

        return sueldob
                + (sueldob * bonusant * antiguedad)
                + 2 * LConsejos.size();
    }

    /**
     * Registra un consejo.
     *
     * Pre: consejo no nulo ni vacio.
     * Post: incrementa en uno los consejos.
     */
    public void registrarConsejo(String consejo) {

        if (consejo == null || consejo.isBlank()) {

            throw new IllegalArgumentException(
                    "El consejo no puede estar vacio"
            );
        }

        LConsejos.add(consejo);
    }

    /**
     * Consulta el numero de consejos registrados.
     *
     * Post: no modifica el estado.
     *
     * @return cantidad de consejos
     */
    public int getCantidadConsejos() {
        return LConsejos.size();
    }
}
