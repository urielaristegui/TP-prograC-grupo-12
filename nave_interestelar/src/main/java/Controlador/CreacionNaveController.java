
package Controlador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import modelo.Tripulacion.*;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.*;
import modelo.universo.Universo;

/**
 * Controlador MVC para crear una nave y asignarle tripulantes.
 *
 * Invariantes:
 * - universo != null.
 * - tripulantesController != null.
 * - Solo se selecciona una nave por sesion.
 */
public final class CreacionNaveController {

    public enum Cargo {
        CAPITAN("Capitan"),
        TENIENTE("Teniente"),
        ALFEREZ("Alferez"),
        CONSEJERO("Consejero");

        private final String nombre;

        Cargo(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private final Universo universo;
    private final TripulantesController tripulantesController;
    private boolean creada;

    /**
     * Pre: universo y tripulantesController != null.
     * Post: almacena las referencias sin modificar el modelo.
     *
     * @throws IllegalArgumentException si hay argumentos nulos
     */
    public CreacionNaveController(
            Universo universo,
            TripulantesController tripulantesController) {

        if (universo == null || tripulantesController == null) {
            throw new IllegalArgumentException(
                    "Universo y controlador de tripulantes son obligatorios"
            );
        }

        this.universo = universo;
        this.tripulantesController = tripulantesController;
    }

    /**
     * Crea la nave que controlara el jugador.
     *
     * Pre:
     * - tipo != null.
     * - id > 0.
     * - ID no utilizado por una nave enemiga.
     * - No se creo otra nave durante esta sesion.
     *
     * Post:
     * - Crea una nave utilizando NaveFactory.
     * - La establece como nave principal del universo.
     * - Conserva los elementos existentes del tablero.
     * - Actualiza los observadores.
     *
     * @param tipo tipo de nave
     * @param id identificador de la nave
     * @return nave creada
     */
    public Nave crearNave(TipoNave tipo, int id) {

        if (creada) {
            throw new IllegalStateException(
                    "La nave del jugador ya fue creada"
            );
        }

        if (tipo == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un tipo y un ID positivo"
            );
        }

        for (ElementoDelUniverso[] fila :
                universo.getTablero().verTablero()) {

            for (ElementoDelUniverso elemento : fila) {

                if (elemento instanceof Nave enemiga
                        && enemiga.getId() == id) {

                    throw new IllegalArgumentException(
                            "El ID ya pertenece a una nave enemiga"
                    );
                }
            }
        }

        Nave nueva = NaveFactory.crearNave(tipo, id);

        universo.cambiarNaveJugador(nueva);

        tripulantesController.setTripulantes(List.of());

        creada = true;

        return nueva;
    }

    /**
     * Agrega un tripulante a la nave creada.
     *
     * Pre:
     * - Existe una nave creada.
     * - nombre no vacio.
     * - cargo != null.
     * - antiguedad >= 0 y finita.
     * - No existe otro tripulante con el mismo nombre.
     * - La nave no posee otro Capitan.
     *
     * Post:
     * - Crea e incorpora el tripulante.
     * - Actualiza el controlador de Tripulantes y Haberes.
     *
     * @param nombre identidad del tripulante
     * @param cargo cargo seleccionado
     * @param antiguedad antiguedad en anios
     * @return tripulante creado
     */
    public Tripulante agregarTripulante(
            String nombre,
            Cargo cargo,
            double antiguedad) {

        if (!creada) {
            throw new IllegalStateException(
                    "Primero tenes que crear una nave"
            );
        }

        String id = nombre == null ? "" : nombre.trim();

        if (id.isEmpty() || cargo == null
                || !Double.isFinite(antiguedad)
                || antiguedad < 0) {

            throw new IllegalArgumentException(
                    "Completa nombre, cargo y antiguedad valida"
            );
        }

        Nave nave = universo.getNave();

        if (nave.buscarTripulante(id) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un tripulante con esa identidad"
            );
        }

        if (cargo == Cargo.CAPITAN
                && getTripulantes().stream().anyMatch(
                t -> "Capitan".equals(t.getCargo()))) {

            throw new IllegalArgumentException(
                    "La nave ya tiene un Capitan"
            );
        }

        Identidad identidad = new Identidad(id);

        Tripulante nuevo = switch (cargo) {

            case TENIENTE ->
                    new Teniente(identidad, antiguedad);

            case ALFEREZ ->
                    new Alferez(identidad, antiguedad);

            case CONSEJERO ->
                    new Consejero(identidad, antiguedad);

            case CAPITAN -> {

                AsistenteDeComandos asistente =
                        new AsistenteDeComandos(
                                nave,
                                Bitacora.getInstance(),
                                universo.getTablero()
                        );

                Capitan capitan = Capitan.getInstancia(
                        identidad,
                        antiguedad,
                        asistente
                );

                if (!id.equals(capitan.getID())) {
                    throw new IllegalStateException(
                            "El Capitan Singleton ya existe "
                                    + "con otra identidad"
                    );
                }

                yield capitan;
            }
        };

        nave.agregarTripulante(nuevo);

        tripulantesController.setTripulantes(
                getTripulantes()
        );

        return nuevo;
    }


    /**
     * Crea un tripulante con origen planetario.
     *
     * Pre:
     * - Existe una nave creada.
     * - Identidad unica y datos validos.
     * - origen != null.
     *
     * Post:
     * - Incorpora el tripulante a la nave.
     * - Aplica el Decorator correspondiente.
     * - Actualiza la vista de haberes.
     *
     * @param nombre identidad del tripulante
     * @param cargo cargo seleccionado
     * @param antiguedad antiguedad en anios
     * @param origen planeta de origen
     * @return tripulante incorporado
     */
    public Tripulante agregarTripulante(
            String nombre,
            Cargo cargo,
            double antiguedad,
            Tripulante.Origen origen) {

        if (origen == null) {
            throw new IllegalArgumentException(
                    "Selecciona un origen"
            );
        }

        Tripulante nuevo = agregarTripulante(
                nombre,
                cargo,
                antiguedad
        );

        nuevo.asignarOrigen(origen);

        tripulantesController.refrescar();

        return nuevo;
    }


    /**
     * Pre: ninguna.
     * Post: no modifica el modelo.
     *
     * @return true si ya se creo la nave
     */
    public boolean fueCreada() {
        return creada;
    }

    /**
     * Pre: ninguna.
     * Post: no modifica el modelo.
     *
     * @return nave actual o null si no fue creada
     */
    public Nave getNave() {
        return creada ? universo.getNave() : null;
    }

    /**
     * Devuelve la tripulacion ordenada por identidad.
     *
     * Pre: ninguna.
     * Post: no modifica la nave.
     *
     * @return lista independiente de tripulantes
     */
    public List<Tripulante> getTripulantes() {

        if (!creada) {
            return List.of();
        }

        ArrayList<Tripulante> lista = new ArrayList<>(
                universo.getNave().getTripulacion()
        );

        lista.sort(
                Comparator.comparing(Tripulante::getID)
        );

        return lista;
    }
}
