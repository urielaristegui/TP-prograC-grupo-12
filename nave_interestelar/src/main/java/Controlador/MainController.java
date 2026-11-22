package Controlador;

import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDTO;
import vista.Persistencia.AutenticacionObserver;

public class MainController implements AutenticacionObserver {

    private final AutenticacionService autenticacionService;
    private UsuarioDTO usuarioActual;

    /**
     * construye el controlador principal asociandolo con el servicio de autenticacion.<br>
     * <b>pre:</b> autenticacionService != null.<br>
     * <b>post:</b> se inicializa autenticacionService y usuarioActual queda en null.<br>
     *
     * @param autenticacionService servicio de autenticacion del sistema. autenticacionService != null.
     * @throws IllegalArgumentException si autenticacionService es null.
     */
    public MainController(AutenticacionService autenticacionService) {
        if (autenticacionService == null) {
            throw new IllegalArgumentException("El servicio de autenticación no puede ser nulo.");
        }
        this.autenticacionService = autenticacionService;
    }

    /**
     * devuelve el servicio de autenticacion asociado al controlador.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> retorna la instancia de autenticacionService.<br>
     *
     * @return servicio de autenticacion.
     */
    public AutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    /**
     * devuelve el usuario actualmente autenticado en la sesion.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> retorna usuarioActual o null si no hay sesion iniciada.<br>
     *
     * @return usuario autenticado actual o null.
     */
    public UsuarioDTO getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * cierra la sesion activa en el servicio de autenticacion y limpia el usuario actual.<br>
     * <b>pre:</b> autenticacionService != null.<br>
     * <b>post:</b> se invoca cerrarSesion() en autenticacionService y usuarioActual pasa a ser null.<br>
     */
    public void cerrarSesion() {
        autenticacionService.cerrarSesion();
        this.usuarioActual = null;
    }

    @Override
    public void onAutenticacionExitosa(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
    }

    @Override
    public void onRegistroResultado(boolean registrado, String mensaje) {
    }

    @Override
    public void onSesionCerrada() {
        this.usuarioActual = null;
    }

    @Override
    public void onErrorAutenticacion(String mensaje) {
    }
}