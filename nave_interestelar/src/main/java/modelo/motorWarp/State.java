package modelo.motorWarp;

public interface State {
    public void prepararSalto();
    public void entrarEnWarp();
    public void iniciarEnfriamiento();
    public void volverADisponible();
}
