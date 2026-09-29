package bitacora;
import java.util.ArrayList;

public class Bitacora {
    private ArrayList<String> registro;
    private static Bitacora instance;

    /**
     * constructor de la clase Bitacora  <br>
     * <b>post:</b> se inicializa el atributo registro <br>
     */
    private Bitacora() {
        registro = new ArrayList<String>();
    }

    /**
     * Retorna la instancia de la bitacora <br>
     * <b>post:</b> se inicializa el atributo instance <br>
     * @return
     */
    public static Bitacora getInstance() {
        if (instance == null) {
            instance = new Bitacora();
        }
        return instance;
    }

    /**
     * registra un mensaje en la bitacora <br>
     * <b>post:</b> se agrega el mensaje al registro <br>
     * <b>excepción:</b> mensaje != null  <br>
     * @param mensaje
     */
    public void registrar(String mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }
        registro.add(mensaje);
    }

    /**
     * Retorna el registro de la bitacora <br>
     */
    public ArrayList<String> getRegistro() {
        return registro;
    }
}
