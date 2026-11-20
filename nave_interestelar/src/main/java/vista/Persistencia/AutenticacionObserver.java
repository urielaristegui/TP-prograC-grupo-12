package vista.Persistencia;

import modelo.Persistencia.UsuarioDTO;

public interface AutenticacionObserver {
    void onAutenticacionExitosa(UsuarioDTO usuario);
    void onRegistroResultado(boolean registrado, String mensaje);
    void onSesionCerrada();
    void onErrorAutenticacion(String mensaje);
}