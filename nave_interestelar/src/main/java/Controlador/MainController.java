package Controlador;

import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDTO;
import vista.Persistencia.AutenticacionObserver;

public class MainController implements AutenticacionObserver {

    private final AutenticacionService autenticacionService;
    private UsuarioDTO usuarioActual;

    public MainController(AutenticacionService autenticacionService) {
        if (autenticacionService == null) {
            throw new IllegalArgumentException("El servicio de autenticación no puede ser nulo.");
        }
        this.autenticacionService = autenticacionService;
    }

    public AutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    public UsuarioDTO getUsuarioActual() {
        return usuarioActual;
    }

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
