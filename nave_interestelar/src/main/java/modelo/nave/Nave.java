package modelo.nave;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Posicion;
import modelo.motorWarp.MotorWarp;
import modelo.Tripulacion.Tripulante;
import modelo.elementos.ElementoDelUniverso;

/**
 * Representa una nave y administra sus recursos y tripulacion.
 *
 * Invariantes:
 * - Combustible y energia dentro de sus capacidades.
 * - Desgaste entre 0 y 100.
 * - Motor no nulo.
 * - Tripulantes no nulos, con IDs unicos y no vacios.
 *
 * La nave debe completar su tripulacion minima antes de operar.
 */
public abstract class Nave implements ElementoDelUniverso {

    private static final int CAPACIDAD_COMBUSTIBLE = 100;
    private static final int CAPACIDAD_ENERGIA = 100;
    private static final int DESGASTE_MAXIMO = 100;
    private static final int UMBRAL_MANTENIMIENTO = 80;
    private static final int TRIPULACION_MINIMA = 5;

    private final int id;
    private int combustible;
    private int energia;
    private int desgaste;
    private boolean destruida;
    private Posicion posicion;
    private final MotorWarp motorWarp;
    private final Map<String, Tripulante> tripulacion;
    private int gemas;

    /**
     * Inicializa la nave con desgaste cero, tripulacion vacia
     * y un motor Warp propio en estado Disponible.
     *
     * @param id identificador de la nave
     * @param combustible combustible inicial, entre 0 y 100
     * @param energia energia inicial, entre 0 y 100
     * @throws IllegalArgumentException si los recursos estan fuera de rango
     */
    protected Nave(int id, int combustible, int energia) {

        if (combustible < 0 || combustible > CAPACIDAD_COMBUSTIBLE) {
            throw new IllegalArgumentException(
                    "El combustible debe estar entre 0 y "
                            + CAPACIDAD_COMBUSTIBLE);
        }

        if (energia < 0 || energia > CAPACIDAD_ENERGIA) {
            throw new IllegalArgumentException(
                    "La energia debe estar entre 0 y "
                            + CAPACIDAD_ENERGIA);
        }

        this.id = id;
        this.combustible = combustible;
        this.energia = energia;
        this.desgaste = 0;
        this.destruida = false;
        this.motorWarp = new MotorWarp();
        this.tripulacion = new HashMap<>();
        this.gemas = 0;
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

    public Posicion getPosicion() {
        return posicion;
    }

    public MotorWarp getMotorWarp() {
        return motorWarp;
    }

    /**
     * Actualiza la posicion actual de la nave.
     *
     * Pre: posicion != null.
     * Post: asigna la nueva coordenada a la nave.
     *
     * @param posicion nueva coordenada
     * @throws IllegalArgumentException si posicion es null
     */
    public void setPosicion(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException(
                    "La posicion no puede ser null"
            );
        }

        this.posicion = posicion;
    }

    /**
     * Descuenta energia del sistema de la nave.
     *
     * Pre: cantidad > 0.
     * Post: reduce la energia sin permitir valores negativos.
     *
     * @param cantidad unidades de energia a consumir
     * @throws IllegalArgumentException si cantidad <= 0
     */
    public void descontarEnergia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a descontar debe ser mayor a cero"
            );
        }

        this.energia = Math.max(0, this.energia - cantidad);
    }

    /**
     * Descuenta combustible.
     *
     * Pre: cantidad > 0.
     * Post: reduce el combustible sin permitir valores negativos.
     *
     * @param cantidad unidades a consumir
     * @throws IllegalArgumentException si cantidad <= 0
     */
    public void descontarCombustible(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a descontar debe ser mayor a cero"
            );
        }

        this.combustible = Math.max(0, this.combustible - cantidad);
    }

    /**
     * Incrementa el desgaste de la nave.
     *
     * Pre: cantidad > 0.
     * Post: aumenta el desgaste hasta un maximo de 100.
     *
     * @param cantidad puntos de desgaste
     * @throws IllegalArgumentException si cantidad <= 0
     */
    public void generarDesgaste(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de desgaste debe ser mayor a cero"
            );
        }

        this.desgaste = Math.min(100, this.desgaste + cantidad);
    }

    /**
     * Pre: ninguna.
     * Post: no modifica la nave.
     *
     * @return true si el desgaste es 80 o superior
     */
    public boolean requiereMantenimiento() {
        return desgaste >= UMBRAL_MANTENIMIENTO;
    }

    /**
     * Aumenta el combustible.
     *
     * Pre: cantidad positiva que no exceda la capacidad restante.
     * Post: combustible incrementado en cantidad.
     *
     * @param cantidad cantidad a cargar
     * @throws IllegalArgumentException si cantidad es invalida
     */
    public void cargarCombustible(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva"
            );
        }

        if (cantidad > CAPACIDAD_COMBUSTIBLE - combustible) {
            throw new IllegalArgumentException(
                    "La carga supera la capacidad maxima de combustible"
            );
        }

        combustible += cantidad;
    }

    /**
     * Aumenta la energia.
     *
     * Pre: cantidad positiva que no exceda la capacidad restante.
     * Post: energia incrementada en cantidad.
     *
     * @param cantidad cantidad a cargar
     * @throws IllegalArgumentException si cantidad es invalida
     */
    public void cargarEnergia(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva"
            );
        }

        if (cantidad > CAPACIDAD_ENERGIA - energia) {
            throw new IllegalArgumentException(
                    "La carga supera la capacidad maxima de energia"
            );
        }

        energia += cantidad;
    }

    /**
     * Consume combustible y energia y aumenta el desgaste.
     * Valida todas las cantidades antes de modificar los recursos.
     *
     * Pre:
     * - Los consumos y el desgaste son no negativos.
     * - Existen recursos suficientes.
     * - El desgaste final no supera 100.
     *
     * Post: se actualizan los tres recursos.
     *
     * @param combustibleConsumido combustible a descontar
     * @param energiaConsumida energia a descontar
     * @param desgasteAgregado desgaste a agregar
     * @throws IllegalArgumentException si alguna cantidad es invalida
     */
    public void consumirRecursos(
            int combustibleConsumido,
            int energiaConsumida,
            int desgasteAgregado) {

        if (combustibleConsumido < 0 ||
                energiaConsumida < 0 ||
                desgasteAgregado < 0) {
            throw new IllegalArgumentException(
                    "Los consumos y el desgaste no pueden ser negativos"
            );
        }

        if (combustibleConsumido > combustible) {
            throw new IllegalArgumentException(
                    "Combustible insuficiente"
            );
        }

        if (energiaConsumida > energia) {
            throw new IllegalArgumentException(
                    "Energia insuficiente"
            );
        }

        if (desgasteAgregado > DESGASTE_MAXIMO - desgaste) {
            throw new IllegalArgumentException(
                    "El desgaste superaria el maximo permitido"
            );
        }

        combustible -= combustibleConsumido;
        energia -= energiaConsumida;
        desgaste += desgasteAgregado;
    }

    /**
     * Restablece el desgaste.
     *
     * Pre: ninguna.
     * Post: desgaste = 0.
     */
    public void realizarMantenimiento() {
        desgaste = 0;
    }

    /**
     * Agrega un tripulante sin reemplazar integrantes existentes.
     *
     * Pre: tripulante != null, ID valido y no registrado.
     * Post: incorpora al tripulante a la nave.
     *
     * @param tripulante tripulante a incorporar
     * @throws IllegalArgumentException si es invalido o duplicado
     */
    public void agregarTripulante(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException(
                    "El tripulante no puede ser null"
            );
        }

        String idTripulante = tripulante.getID();
        validarIdTripulante(idTripulante);

        if (tripulacion.containsKey(idTripulante)) {
            throw new IllegalArgumentException(
                    "Ya existe un tripulante con ese ID"
            );
        }

        tripulacion.put(idTripulante, tripulante);
    }

    /**
     * Busca un tripulante por ID.
     *
     * Pre: ID no nulo ni vacio.
     * Post: no modifica la tripulacion.
     *
     * @param idTripulante ID buscado
     * @return tripulante encontrado o null
     */
    public Tripulante buscarTripulante(String idTripulante) {
        validarIdTripulante(idTripulante);
        return tripulacion.get(idTripulante);
    }

    /**
     * Devuelve una coleccion no modificable.
     *
     * Pre: ninguna.
     * Post: no modifica la tripulacion.
     *
     * @return tripulantes actuales
     */
    public Collection<Tripulante> getTripulacion() {
        return List.copyOf(tripulacion.values());
    }

    /**
     * Comprueba la validez de la tripulacion.
     *
     * Pre: ninguna.
     * Post: no modifica la nave.
     *
     * @return true si hay al menos cinco tripulantes y un capitan
     */
    public boolean tieneTripulacionValida() {
        if (tripulacion.size() < TRIPULACION_MINIMA) {
            return false;
        }

        for (Tripulante tripulante : tripulacion.values()) {
            if ("Capitan".equals(tripulante.getCargo())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valida el identificador de un tripulante.
     *
     * Pre: ninguna.
     * Post: no modifica la nave.
     *
     * @param idTripulante identificador
     * @throws IllegalArgumentException si es nulo o vacio
     */
    private void validarIdTripulante(String idTripulante) {
        if (idTripulante == null || idTripulante.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID del tripulante no puede ser null ni vacio"
            );
        }
    }


    /**
     * Solicita la preparacion de un Warp.
     *
     * Pre: tripulacion valida.
     * Post: inicia el primer turno de preparacion
     * si el motor se encuentra disponible.
     */
    public void prepararSalto() {

        if (!tieneTripulacionValida()) {
            throw new IllegalStateException(
                    "La nave no tiene tripulacion valida"
            );
        }

        motorWarp.saltarWarp(this);
    }

    /**
     * Metodo de compatibilidad con P1 para completar
     * el ciclo Warp.
     *
     * En P2, la navegacion por turnos debe gestionarla
     * ReglasNavegacion.
     *
     * Pre: tripulacion valida y energia suficiente.
     * Post: completa el ciclo Warp y el enfriamiento.
     * No modifica directamente las coordenadas.
     */
    public void saltar() {

        if (!tieneTripulacionValida()) {
            throw new IllegalStateException(
                    "La nave no tiene tripulacion valida"
            );
        }

        if (getEnergia() < 5) {
            throw new IllegalStateException(
                    "Energia insuficiente para Warp"
            );
        }

        boolean ejecutado = false;

        for (int intento = 0;
             intento < 3 && !ejecutado;
             intento++) {

            ejecutado = motorWarp.saltarWarp(this);
        }

        if (!ejecutado) {
            throw new IllegalStateException(
                    "No se pudo completar el Warp"
            );
        }

        motorWarp.saltar(this);
    }


    // =====================================================
    // NUEVO - DESTRUCCION DE NAVE
    // =====================================================

    /**
     * Indica si la nave esta destruida.
     *
     * Pre: ninguna.
     * Post: no modifica el estado de la nave.
     *
     * @return true si fue destruida
     */
    public final boolean estaDestruida() {
        return destruida;
    }

    /**
     * Destruye definitivamente la nave.
     *
     * Pre: ninguna.
     * Post:
     * - destruida = true.
     * - combustible = 0.
     * - energia = 0.
     * - desgaste = 100.
     * - tripulacion vacia.
     *
     * La operacion es idempotente.
     */
    public final void destruir() {
        if (destruida) {
            return;
        }

        destruida = true;
        combustible = 0;
        energia = 0;
        desgaste = DESGASTE_MAXIMO;
        tripulacion.clear();
    }

    // =====================================================
    // NUEVO - ENCUENTROS ENTRE NAVES
    // =====================================================

    /**
     * Resuelve el encuentro con esta nave enemiga.
     *
     * Pre:
     * - naveJugador != null.
     * - naveJugador != this.
     * - ninguna de las naves esta destruida.
     *
     * Post:
     * - Resuelve el enfrentamiento mediante Double Dispatch.
     * - Registra el resultado en la bitacora.
     * - Si el jugador pierde, destruye su nave.
     * - Si gana o hay pacto, mantiene sus recursos.
     *
     * @param naveJugador nave del jugador
     * @throws IllegalArgumentException si la nave es invalida
     * @throws IllegalStateException si alguna nave esta destruida
     */
    @Override
    public final void interactuar(Nave naveJugador) {
        validarRival(naveJugador);

        ResultadoCombate resultado =
                naveJugador.enfrentar(this);

        Bitacora.getInstance().registrar(
                "Encuentro: nave " + naveJugador.getId()
                        + " (" + naveJugador.getClass().getSimpleName()
                        + ") contra nave " + getId()
                        + " (" + getClass().getSimpleName()
                        + "): " + resultado
        );

        if (resultado == ResultadoCombate.PIERDE) {
            naveJugador.destruir();

            Bitacora.getInstance().registrar(
                    "La nave " + naveJugador.getId()
                            + " fue destruida en combate."
            );
        }
    }

    /**
     * Primera llamada del Double Dispatch.
     *
     * Pre:
     * - rival != null.
     * - rival != this.
     * - ninguna nave destruida.
     *
     * Post:
     * - Devuelve el resultado desde la perspectiva de esta nave.
     * - No modifica recursos ni tripulacion.
     *
     * @param rival nave enemiga
     * @return resultado del enfrentamiento
     */
    public abstract ResultadoCombate enfrentar(Nave rival);

    /**
     * Segunda llamada del Double Dispatch para Exploradora.
     *
     * Pre: navePropia != null.
     * Post: devuelve el resultado sin alterar estados.
     *
     * @param navePropia exploradora del jugador
     * @return resultado desde la perspectiva del jugador
     */
    protected abstract ResultadoCombate recibirEncuentroDe(NaveExploradora navePropia);

    /**
     * Segunda llamada del Double Dispatch para Carguero.
     *
     * Pre: navePropia != null.
     * Post: devuelve el resultado sin alterar estados.
     *
     * @param navePropia carguero del jugador
     * @return resultado desde la perspectiva del jugador
     */
    protected abstract ResultadoCombate recibirEncuentroDe(NaveCarguero navePropia);

    /**
     * Segunda llamada del Double Dispatch para Combate.
     *
     * Pre: navePropia != null.
     * Post: devuelve el resultado sin alterar estados.
     *
     * @param navePropia nave de combate del jugador
     * @return resultado desde la perspectiva del jugador
     */
    protected abstract ResultadoCombate recibirEncuentroDe(NaveCombate navePropia);

    /**
     * Valida un enfrentamiento.
     *
     * Pre: ninguna.
     * Post: no modifica el estado de las naves.
     *
     * @param rival nave enemiga
     * @throws IllegalArgumentException si rival es null o es esta nave
     * @throws IllegalStateException si alguna nave esta destruida
     */
    protected final void validarRival(Nave rival) {
        if (rival == null) {
            throw new IllegalArgumentException(
                    "La nave rival no puede ser null"
            );
        }

        if (rival == this) {
            throw new IllegalArgumentException(
                    "Una nave no puede enfrentarse a si misma"
            );
        }

        if (destruida || rival.estaDestruida()) {
            throw new IllegalStateException(
                    "No se puede combatir con una nave destruida"
            );
        }
    }


    /**
     * Consulta las gemas acumuladas.
     *
     * Pre: ninguna.
     * Post: no modifica el estado.
     *
     * @return cantidad actual de gemas
     */
    public int getGemas() {
        return gemas;
    }

    /**
     * Incorpora gemas al inventario de la nave.
     *
     * Pre:
     * - cantidad > 0.
     * - La nave no esta destruida.
     * - La suma debe poder representarse como int.
     *
     * Post:
     * - Incrementa gemas en cantidad.
     * - No modifica otros recursos.
     *
     * @param cantidad gemas a incorporar
     * @throws IllegalArgumentException si cantidad <= 0
     * @throws IllegalStateException si la nave esta destruida
     *         o la suma excede el rango de int
     */
    public void agregarGemas(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva"
            );
        }

        if (destruida) {
            throw new IllegalStateException(
                    "La nave esta destruida"
            );
        }

        try {
            gemas = Math.addExact(gemas, cantidad);
        } catch (ArithmeticException ex) {
            throw new IllegalStateException(
                    "Capacidad numerica de gemas excedida", ex
            );
        }
    }

}