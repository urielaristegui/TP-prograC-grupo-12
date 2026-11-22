package modelo.motorWarp;

public interface State {
    /**
     * solicita iniciar la preparacion del motor warp para un salto.<br>
     * <b>pre:</b> el estado actual esta inicializado.<br>
     * <b>post:</b> ejecuta la transicion o el manejo correspondiente segun el estado concreto.<br>
     */
    public void prepararSalto();

    /**
     * solicita que el motor entre en modo warp.<br>
     * <b>pre:</b> el estado actual esta inicializado.<br>
     * <b>post:</b> ejecuta la transicion o el manejo correspondiente segun el estado concreto.<br>
     */
    public void entrarEnWarp();

    /**
     * solicita iniciar la fase de enfriamiento del motor warp.<br>
     * <b>pre:</b> el estado actual esta inicializado.<br>
     * <b>post:</b> ejecuta la transicion o el manejo correspondiente segun el estado concreto.<br>
     */
    public void iniciarEnfriamiento();

    /**
     * solicita que el motor vuelva al estado disponible.<br>
     * <b>pre:</b> el estado actual esta inicializado.<br>
     * <b>post:</b> ejecuta la transicion o el manejo correspondiente segun el estado concreto.<br>
     */
    public void volverADisponible();
}