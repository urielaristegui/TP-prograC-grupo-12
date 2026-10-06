package modelo.nave;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.bitacora.Bitacora;
import modelo.motorWarp.MotorWarp;
import modelo.Tripulacion.Tripulante;
import Thread;
/**
 * Representa una nave y administra sus recursos y tripulación.
 *
 * Invariantes:
 * - Combustible y energía dentro de sus capacidades.
 * - Desgaste entre 0 y 100.
 * - Motor no nulo.
 * - Tripulantes no nulos, con IDs únicos y no vacíos.
 *
 * La nave debe completar su tripulación mínima antes de operar.
 */
public abstract class Nave {
    private static final int CAPACIDAD_COMBUSTIBLE = 100;
    private static final int CAPACIDAD_ENERGIA = 100;
    private static final int DESGASTE_MAXIMO = 100;
    private static final int UMBRAL_MANTENIMIENTO = 80;
    private static final int TRIPULACION_MINIMA = 5;

    private final int id;
    private int combustible;
    private int energia;
    private int desgaste;

    private final MotorWarp motorWarp;
    private final Map<String, Tripulante> tripulacion;

    /**
     * Inicializa la nave con desgaste cero, tripulación vacía
     * y un motor Warp propio en estado Disponible.
     *
     * @param id identificador de la nave
     * @param combustible combustible inicial, entre 0 y 100
     * @param energia energía inicial, entre 0 y 100
     * @throws IllegalArgumentException si los recursos están fuera de rango
     */
    protected Nave(int id, int combustible, int energia) {

        if (combustible < 0 || combustible > CAPACIDAD_COMBUSTIBLE) {
            throw new IllegalArgumentException(
                    "El combustible debe estar entre 0 y "
                            + CAPACIDAD_COMBUSTIBLE);
        }

        if (energia < 0 || energia > CAPACIDAD_ENERGIA) {
            throw new IllegalArgumentException(
                    "La energía debe estar entre 0 y "
                            + CAPACIDAD_ENERGIA);
        }

        this.id = id;
        this.combustible = combustible;
        this.energia = energia;
        this.desgaste = 0;
        this.motorWarp = new MotorWarp(Bitacora.getInstance());
        this.tripulacion = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public int getCombustible() {
        return combustible;
    }

    public int getEnergia() {
        return energia;
    }

    public int getDesgaste() {
        return desgaste;
    }

    /**
     * @return true si el desgaste es 80 o superior
     */
    public boolean requiereMantenimiento() {
        return desgaste >= UMBRAL_MANTENIMIENTO;
    }

    /**
     * Aumenta el combustible en la cantidad indicada.
     *
     * @param cantidad cantidad positiva que no exceda la capacidad restante
     * @throws IllegalArgumentException si la cantidad es inválida;
     *         el combustible conserva su valor anterior
     */
    public void cargarCombustible(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva");
        }

        if (cantidad > CAPACIDAD_COMBUSTIBLE - combustible) {
            throw new IllegalArgumentException(
                    "La carga supera la capacidad máxima de combustible");
        }

        combustible += cantidad;
    }

    /**
     * Aumenta la energía en la cantidad indicada.
     *
     * @param cantidad cantidad positiva que no exceda la capacidad restante
     * @throws IllegalArgumentException si la cantidad es inválida;
     *         la energía conserva su valor anterior
     */
    public void cargarEnergia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva");
        }

        if (cantidad > CAPACIDAD_ENERGIA - energia) {
            throw new IllegalArgumentException(
                    "La carga supera la capacidad máxima de energía");
        }

        energia += cantidad;
    }

    /**
     * Descuenta combustible y energía y aumenta el desgaste.
     * Todas las validaciones se realizan antes de modificar los recursos.
     *
     * @param combustibleConsumido cantidad no negativa y disponible
     * @param energiaConsumida cantidad no negativa y disponible
     * @param desgasteAgregado aumento no negativo que no supere
     *        el desgaste máximo
     * @throws IllegalArgumentException si alguna cantidad es inválida;
     *         ninguno de los recursos se modifica
     */
    public void consumirRecursos(int combustibleConsumido, int energiaConsumida, int desgasteAgregado) {

        if (combustibleConsumido < 0 || energiaConsumida < 0 || desgasteAgregado < 0) {
            throw new IllegalArgumentException(
                    "Los consumos y el desgaste agregado no pueden ser negativos");
        }

        if (combustibleConsumido > combustible) {
            throw new IllegalArgumentException(
                    "Combustible insuficiente");
        }

        if (energiaConsumida > energia) {
            throw new IllegalArgumentException(
                    "Energía insuficiente");
        }

        if (desgasteAgregado > DESGASTE_MAXIMO - desgaste) {
            throw new IllegalArgumentException(
                    "El desgaste superaría el máximo permitido");
        }

        combustible -= combustibleConsumido;
        energia -= energiaConsumida;
        desgaste += desgasteAgregado;
    }

    /**
     * Restablece el desgaste a cero.
     * No modifica el combustible ni la energía.
     */
    public void realizarMantenimiento() {
        desgaste = 0;
    }

    /**
     * Agrega un tripulante sin reemplazar integrantes existentes.
     *
     * @param tripulante tripulante no nulo con ID válido y no registrado
     * @throws IllegalArgumentException si el tripulante o su ID
     *         son inválidos, o si el ID está duplicado;
     *         la colección conserva su contenido anterior
     */
    public void agregarTripulante(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException(
                    "El tripulante no puede ser null");
        }

        String idTripulante = tripulante.getID();
        validarIdTripulante(idTripulante);

        if (tripulacion.containsKey(idTripulante)) {
            throw new IllegalArgumentException(
                    "Ya existe un tripulante con ese ID");
        }

        tripulacion.put(idTripulante, tripulante);
    }

    /**
     * @param idTripulante ID no nulo ni vacío
     * @return tripulante encontrado, o null si no existe
     * @throws IllegalArgumentException si el ID es inválido
     */
    public Tripulante buscarTripulante(String idTripulante) {
        validarIdTripulante(idTripulante);
        return tripulacion.get(idTripulante);
    }

    /**
     * Devuelve una copia no modificable de la colección.
     * Los objetos Tripulante no se copian y el orden no está garantizado.
     *
     * @return colección de los tripulantes actuales
     */
    public Collection<Tripulante> getTripulacion() {
        return List.copyOf(tripulacion.values());
    }

    /**
     * @return true si hay al menos cinco integrantes
     *         y al menos uno tiene cargo de capitán
     */
    public boolean tieneTripulacionValida() {
        if (tripulacion.size() < TRIPULACION_MINIMA) {
            return false;
        }

        for (Tripulante tripulante : tripulacion.values()) {
            if (tripulante.getCargo() == Cargo.CAPITAN) {
                return true;
            }
        }

        return false;
    }

    private void validarIdTripulante(String idTripulante) {
        if (idTripulante == null || idTripulante.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID del tripulante no puede ser null ni vacío");
        }
    }

    /**
     * Solicita al motor preparar el salto.
     *
     * <b>pre:</b> el motor está Disponible.
     * <b>post:</b> el motor está PreparandoSalto.
     *
     * @throws IllegalStateException si el estado no permite preparar el salto
     */
    public void prepararSalto() {
        motorWarp.prepararSalto();
    }

    /**
     * Ejecuta el salto y lo finaliza en el mismo llamado.
     *
     * <b>pre:</b> el motor está PreparandoSalto.
     * <b>post:</b> el motor pasa por EnWarp y termina Disponible.
     *
     * @throws IllegalStateException si el estado no permite saltar
     * @throws InterruptedException si el hilo actual es interrumpido
     */
    public void saltar() {
        try {
            motorWarp.entrarEnWarp();
            motorWarp.iniciarEnfriamiento();
            // aplicas el retardo de 10 segundos (10000 milisegundos)
           Thread.sleep(10000);
            motorWarp.volverAlDisponible();

        } catch (InterruptedException e) {
            // Zona de recuperación: esto se ejecuta SÓLO si otro proceso cancela la espera de 10 segundos
            Bitacora.getInstancia().registrar("Alerta: El ciclo de enfriamiento fue interrumpido forzosamente.");
            // Forzamos al motor a volver a un estado seguro tras la emergencia
            motorWarp.volverAlDisponible();
        }
    }

}