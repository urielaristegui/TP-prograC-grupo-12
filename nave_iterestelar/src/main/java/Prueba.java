import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Consejero;
import modelo.Tripulacion.Identidad;
import modelo.Tripulacion.Teniente;
import modelo.Tripulacion.Tripulante;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
/**
 * Prueba manual de las operaciones principales del proyecto.
 */
public final class Prueba {
    public static void main(String[] args) {
        Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
        Bitacora bitacora = Bitacora.getInstance();
        AsistenteDeComandos asistente = new AsistenteDeComandos(nave, bitacora);

        Capitan capitan = Capitan.getInstancia(
                new Identidad("Capitán"), 10, asistente);
        Tripulante alferez = new Alferez(new Identidad("Alférez"), 3);
        Tripulante consejero = new Consejero(new Identidad("Consejero"), 4);
        Tripulante teniente = new Teniente(new Identidad("Teniente"), 5);
        Tripulante segundoAlferez = new Alferez(
                new Identidad("Alférez auxiliar"), 2);

        nave.agregarTripulante(capitan);
        nave.agregarTripulante(alferez);
        nave.agregarTripulante(consejero);
        nave.agregarTripulante(teniente);
        nave.agregarTripulante(segundoAlferez);

        System.out.println("Nave: " + nave.getId());
        System.out.println("Tripulantes: " + nave.getTripulacion().size());
        System.out.println("Tripulación válida: " + nave.tieneTripulacionValida());
        System.out.println("Sueldo del capitán: " + capitan.getSueldo());
        System.out.println("Capitán encontrado: "
                + (nave.buscarTripulante(String.valueOf(capitan.getID())) != null));

        nave.consumirRecursos(10, 15, 20);
        System.out.println("Combustible restante: " + nave.getCombustible());
        System.out.println("Energía restante: " + nave.getEnergia());
        System.out.println("Desgaste: " + nave.getDesgaste());

        asistente.registrarEvento("Prueba ejecutada correctamente");
        System.out.println("Eventos en bitácora: " + bitacora.getRegistro().size());
    }
}