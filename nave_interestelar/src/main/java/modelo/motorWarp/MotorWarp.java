package modelo.motorWarp;

/**
 * clase que representa el motor warp de la nave (contexto del patron state).<br>
 * administra el comportamiento y las transiciones del estado del motor.<br>
 *
 * <b>invariante:</b> state != null.
 */
public class MotorWarp {

    private State state;
    private int turnosPreparacion;
    private int turnosEnfriamiento;

    /**
     * constructor de la clase motorWarp.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> inicializa el motor en estado disponible con los contadores en cero.
     */
    public MotorWarp() {
        this.turnosPreparacion = 0;
        this.turnosEnfriamiento = 0;
        setState(new Disponible(this));
    }

    /**
     * cambia el estado del motorWarp.<br>
     * <b>pre:</b> state != null.<br>
     * <b>post:</b> se actualiza el atributo state.<br>
     *
     * @param state nuevo estado a asignar.
     * @throws IllegalArgumentException si el estado recibido es null.
     */
    public void setState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("el estado no puede ser null");
        }
        this.state = state;
    }

    /**
     * devuelve el estado actual del motorWarp.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna la referencia al estado actual.<br>
     *
     * @return estado actual del motor.
     */
    public State getState() {
        return state;
    }

    /**
     * llama al metodo prepararSalto del estado actual.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> delega la accion al estado actual.
     */
    public void prepararSalto() {
        this.state.prepararSalto();
    }

    /**
     * llama al metodo entrarEnWarp del estado actual.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> delega la accion al estado actual.
     */
    public void entrarEnWarp() {
        this.state.entrarEnWarp();
    }

    /**
     * llama al metodo iniciarEnfriamiento del estado actual.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> delega la accion al estado actual.
     */
    public void iniciarEnfriamiento() {
        this.state.iniciarEnfriamiento();
    }

    /**
     * llama al metodo volverAlDisponible del estado actual.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> delega la accion al estado actual.
     */
    public void volverAlDisponible() {
        this.state.volverADisponible();
    }

    /**
     * devuelve la cantidad de turnos de preparacion acumulados.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna el entero correspondiente a los turnos.<br>
     *
     * @return turnos de preparacion acumulados.
     */
    public int getTurnosPreparacion() {
        return turnosPreparacion;
    }

    /**
     * establece la cantidad de turnos de preparacion.<br>
     * <b>pre:</b> turnosPreparacion >= 0.<br>
     * <b>post:</b> actualiza el contador de turnos.<br>
     *
     * @param turnosPreparacion cantidad de turnos.
     */
    public void setTurnosPreparacion(int turnosPreparacion) {
        this.turnosPreparacion = turnosPreparacion;
    }

    /**
     * incrementa en uno los turnos de preparacion acumulados.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> suma 1 al contador de preparacion.
     */
    public void incrementarTurnoPreparacion() {
        this.turnosPreparacion++;
    }

    /**
     * reinicia a cero el contador de turnos de preparacion.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> asigna 0 a turnosPreparacion.
     */
    public void resetearTurnosPreparacion() {
        this.turnosPreparacion = 0;
    }

    /**
     * devuelve la cantidad de turnos de enfriamiento acumulados.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> retorna el entero correspondiente.<br>
     *
     * @return turnos de enfriamiento.
     */
    public int getTurnosEnfriamiento() {
        return turnosEnfriamiento;
    }

    /**
     * establece los turnos de enfriamiento acumulados.<br>
     * <b>pre:</b> turnosEnfriamiento >= 0.<br>
     * <b>post:</b> actualiza el valor del contador.<br>
     *
     * @param turnosEnfriamiento cantidad de turnos.
     */
    public void setTurnosEnfriamiento(int turnosEnfriamiento) {
        this.turnosEnfriamiento = turnosEnfriamiento;
    }

    /**
     * reinicia a cero el contador de turnos de enfriamiento.<br>
     * <b>pre:</b> ninguna.<br>
     * <b>post:</b> asigna 0 a turnosEnfriamiento.
     */
    public void resetearTurnosEnfriamiento() {
        this.turnosEnfriamiento = 0;
    }
}