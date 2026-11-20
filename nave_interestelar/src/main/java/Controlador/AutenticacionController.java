package Controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import modelo.Persistencia.AutenticacionService;
import modelo.Persistencia.UsuarioDTO;
import vista.Persistencia.AutenticacionObserver;
import vista.Persistencia.NavegacionAccesoObserver;

public class AutenticacionController {

    private final AutenticacionService autenticacionService;
    private final List<AutenticacionObserver> observadores = new ArrayList<>();
    private NavegacionAccesoObserver navegacionObserver;

    public AutenticacionController(AutenticacionService autenticacionService) {
        if (autenticacionService == null) {
            throw new IllegalArgumentException("El servicio de autenticación no puede ser nulo.");
        }
        this.autenticacionService = autenticacionService;
    }

    public void agregarObservador(AutenticacionObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(AutenticacionObserver observador) {
        observadores.remove(observador);
    }

    public void setNavegacionObserver(NavegacionAccesoObserver navegacionObserver) {
        this.navegacionObserver = navegacionObserver;
    }

    public void irARegistro() {
        if (navegacionObserver != null) {
            navegacionObserver.mostrarRegistro();
        }
    }

    public void irALogin() {
        if (navegacionObserver != null) {
            navegacionObserver.mostrarLogin();
        }
    }

    public void iniciarSesion(String nombreUsuario, String contrasena) {
        try {
            boolean ok = autenticacionService.iniciarSesion(nombreUsuario, contrasena);
            if (ok && autenticacionService.getUsuarioActual().isPresent()) {
                UsuarioDTO usuario = autenticacionService.getUsuarioActual().get();
                for (AutenticacionObserver obs : observadores) {
                    obs.onAutenticacionExitosa(usuario);
                }
            } else {
                for (AutenticacionObserver obs : observadores) {
                    obs.onErrorAutenticacion("Usuario o contraseña incorrectos.");
                }
            }
        } catch (IllegalArgumentException e) {
            for (AutenticacionObserver obs : observadores) {
                obs.onErrorAutenticacion(e.getMessage());
            }
        } catch (IOException e) {
            for (AutenticacionObserver obs : observadores) {
                obs.onErrorAutenticacion("Error de E/S al leer credenciales: " + e.getMessage());
            }
        }
    }

    public void registrarUsuario(String nombreUsuario, String contrasena, String confirmacionContrasena) {
        if (contrasena == null || !contrasena.equals(confirmacionContrasena)) {
            for (AutenticacionObserver obs : observadores) {
                obs.onRegistroResultado(false, "Las contraseñas no coinciden.");
            }
            return;
        }

        try {
            boolean registrado = autenticacionService.registrarUsuario(nombreUsuario, contrasena);
            if (registrado) {
                for (AutenticacionObserver obs : observadores) {
                    obs.onRegistroResultado(true, "Usuario registrado correctamente. Ya podés iniciar sesión.");
                }
            } else {
                for (AutenticacionObserver obs : observadores) {
                    obs.onRegistroResultado(false, "El nombre de usuario ya existe.");
                }
            }
        } catch (IllegalArgumentException e) {
            for (AutenticacionObserver obs : observadores) {
                obs.onRegistroResultado(false, e.getMessage());
            }
        } catch (IOException e) {
            for (AutenticacionObserver obs : observadores) {
                obs.onRegistroResultado(false, "Error de E/S al guardar credenciales: " + e.getMessage());
            }
        }
    }

    public void cerrarSesion() {
        autenticacionService.cerrarSesion();
        for (AutenticacionObserver obs : observadores) {
            obs.onSesionCerrada();
        }
    }
}
