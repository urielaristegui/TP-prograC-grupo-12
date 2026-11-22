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

    /**
     * construye el controlador de autenticacion asociandolo con el servicio correspondiente.<br>
     * <b>pre:</b> autenticacionService != null.<br>
     * <b>post:</b> se inicializa el atributo autenticacionService.<br>
     *
     * @param autenticacionService servicio encargado de gestionar la autenticacion y el registro. autenticacionService != null.
     * @throws IllegalArgumentException si autenticacionService es null.
     */
    public AutenticacionController(AutenticacionService autenticacionService) {
        if (autenticacionService == null) {
            throw new IllegalArgumentException("El servicio de autenticación no puede ser nulo.");
        }
        this.autenticacionService = autenticacionService;
    }

    /**
     * registra un observador de autenticacion si no es nulo y no esta repetido.<br>
     * <b>pre:</b> la lista de observadores esta inicializada.<br>
     * <b>post:</b> si observador != null y no estaba presente, se agrega a la lista observadores.<br>
     *
     * @param observador observador a suscribir.
     */
    public void agregarObservador(AutenticacionObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * elimina un observador de autenticacion de la lista de suscriptores.<br>
     * <b>pre:</b> la lista de observadores esta inicializada.<br>
     * <b>post:</b> se remueve observador de la lista observadores si estaba presente.<br>
     *
     * @param observador observador a desuscribir.
     */
    public void removerObservador(AutenticacionObserver observador) {
        observadores.remove(observador);
    }

    /**
     * establece el observador encargado de gestionar la navegacion entre login y registro.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> se asigna el valor recibido al atributo navegacionObserver.<br>
     *
     * @param navegacionObserver observador de navegacion de acceso.
     */
    public void setNavegacionObserver(NavegacionAccesoObserver navegacionObserver) {
        this.navegacionObserver = navegacionObserver;
    }

    /**
     * solicita mostrar la vista de registro a traves del observador de navegacion.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> si navegacionObserver != null, se invoca mostrarRegistro().<br>
     */
    public void irARegistro() {
        if (navegacionObserver != null) {
            navegacionObserver.mostrarRegistro();
        }
    }

    /**
     * solicita mostrar la vista de login a traves del observador de navegacion.<br>
     * <b>pre:</b> el controlador esta inicializado.<br>
     * <b>post:</b> si navegacionObserver != null, se invoca mostrarLogin().<br>
     */
    public void irALogin() {
        if (navegacionObserver != null) {
            navegacionObserver.mostrarLogin();
        }
    }

    /**
     * intenta iniciar sesion con las credenciales indicadas y notifica el resultado a los observadores.<br>
     * <b>pre:</b> autenticacionService != null y observadores != null.<br>
     * <b>post:</b> si las credenciales son validas y existe usuario actual, notifica onAutenticacionExitosa a todos los observadores; en caso contrario o ante excepcion, notifica onErrorAutenticacion.<br>
     *
     * @param nombreUsuario nombre de usuario ingresado.
     * @param contrasena contrasena ingresada.
     */
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

    /**
     * valida la confirmacion de contrasena, intenta registrar un nuevo usuario y notifica el resultado a los observadores.<br>
     * <b>pre:</b> autenticacionService != null y observadores != null.<br>
     * <b>post:</b> notifica onRegistroResultado con true si el registro fue exitoso, o con false y el mensaje correspondiente si las contrasenas no coinciden, el usuario ya existe o ocurre una excepcion.<br>
     *
     * @param nombreUsuario nombre de usuario a registrar.
     * @param contrasena contrasena elegida.
     * @param confirmacionContrasena repeticion de la contrasena para verificacion.
     */
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

    /**
     * cierra la sesion actual en el servicio y notifica el cierre a todos los observadores.<br>
     * <b>pre:</b> autenticacionService != null y observadores != null.<br>
     * <b>post:</b> se cierra la sesion activa y se invoca onSesionCerrada() en cada observador registrado.<br>
     */
    public void cerrarSesion() {
        autenticacionService.cerrarSesion();
        for (AutenticacionObserver obs : observadores) {
            obs.onSesionCerrada();
        }
    }
}