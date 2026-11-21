package modelo.asistenteDeComandos;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Destino;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.sensores.ReglasNavegacion;

import java.util.function.Consumer;


/**
 * Único punto de contacto entre las misiones y el resto del sistema
 * (nave, bitácora, tablero, navegación). Coordina y delega: no decide nada
 * del dominio.
 */
public class AsistenteDeComandos {

	// TODO: idealmente Nave expone estas constantes en vez de repetirlas acá.
	private static final int CAPACIDAD_MAXIMA = 100;
	private static final int DESGASTE_MAXIMO = 100;

	private Nave nave;
	private Bitacora bitacora;
	private Tablero tablero;
	private Destino destino;
	private ReglasNavegacion reglas;
	private Posicion base;
	private Consumer<Posicion> notificadorObjetivo;

	public AsistenteDeComandos(Nave nave, Bitacora bitacora) {
		setNave(nave);
		setBitacora(bitacora);
	}

	public AsistenteDeComandos(Nave nave, Bitacora bitacora, Tablero tablero) {
		this(nave, bitacora);
		setTablero(tablero);
	}

	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}

	public void setNave(Nave nave) {
		this.nave = nave;
	}

	/**
	 * Recibe el tablero, crea el Destino que lo hace evolucionar y deja a la
	 * nave en la posición inicial del tablero. Esa posición es la base de origen.
	 */
	public void setTablero(Tablero tablero) {
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser null");
		}
		this.tablero = tablero;
		this.destino = new Destino(tablero);
		this.base = tablero.getPosicionNave();
		nave.setPosicion(base);
	}

	public void setReglasNavegacion(ReglasNavegacion reglas) {
		if (reglas == null) {
			throw new IllegalArgumentException("Las reglas de navegación no pueden ser null");
		}
		this.reglas = reglas;
	}

	private void exigirTablero() {
		if (tablero == null) {
			throw new IllegalStateException("El asistente no tiene un tablero asignado");
		}
	}

	private void exigirReglas() {
		if (reglas == null) {
			throw new IllegalStateException("El asistente no tiene reglas de navegación asignadas");
		}
	}

	// ---------------------------------------------------------------
	// Nave: recursos
	// ---------------------------------------------------------------

	public boolean tieneRecursosDisponibles(int combustibleRequerido, int energiaRequerida) {
		return nave.getCombustible() >= combustibleRequerido
				&& nave.getEnergia() >= energiaRequerida;
	}

	public void ordenarConsumo(int combustible, int energia, int desgaste) {
		nave.consumirRecursos(combustible, energia, desgaste);
	}

	public int getCombustible() {
		return nave.getCombustible();
	}

	public int getEnergia() {
		return nave.getEnergia();
	}

	public int getDesgaste() {
		return nave.getDesgaste();
	}


	/**
	 * Comprueba y actualiza el estado de destruccion de la nave.
	 *
	 * Pre: nave != null.
	 *
	 * Post:
	 * - Si la energia o el combustible llegan a 0,
	 *   o el desgaste alcanza 100, destruye la nave.
	 * - La destruccion elimina toda la tripulacion.
	 * - Si ya estaba destruida, no repite la operacion.
	 * - Si sigue operativa, no modifica su estado.
	 *
	 * @return true si la nave esta destruida
	 */
	public boolean naveDestruida() {

		if (!nave.estaDestruida()
				&& (nave.getEnergia() <= 0
				|| nave.getCombustible() <= 0
				|| nave.getDesgaste() >= DESGASTE_MAXIMO)) {

			nave.destruir();

			registrarEvento(
					"Nave " + nave.getId()
							+ " destruida: recursos agotados "
							+ "o desgaste maximo. "
							+ "La tripulacion ha fallecido."
			);
		}

		return nave.estaDestruida();
	}


	/** M-01: entrega de recursos a la nave aliada. */
	public void transferirRecursos(int combustible, int energia) {
		nave.consumirRecursos(combustible, energia, 0);
	}

	/** M-03: recarga total de combustible y energía, y desgaste en 0. */
	public void recargaTotalYMantenimiento() {
		int faltaCombustible = CAPACIDAD_MAXIMA - nave.getCombustible();
		int faltaEnergia = CAPACIDAD_MAXIMA - nave.getEnergia();
		if (faltaCombustible > 0) {
			nave.cargarCombustible(faltaCombustible);
		}
		if (faltaEnergia > 0) {
			nave.cargarEnergia(faltaEnergia);
		}
		nave.realizarMantenimiento();
	}

	// ---------------------------------------------------------------
	// Bitácora e informe
	// ---------------------------------------------------------------

	/** Punto único para que las misiones dejen constancia de eventos. */
	public void registrarEvento(String evento) {
		bitacora.registrar(evento);
	}

	public String snapshotEstadoNave() {
		return "Combustible: " + nave.getCombustible()
				+ ", Energía: " + nave.getEnergia()
				+ ", Desgaste: " + nave.getDesgaste()
				+ (nave.requiereMantenimiento() ? " (requiere mantenimiento)" : "");
	}

	// ---------------------------------------------------------------
	// Tablero y navegación
	// ---------------------------------------------------------------

	public Posicion posicionNave() {
		exigirTablero();
		return tablero.getPosicionNave();
	}

	public Posicion posicionBase() {
		exigirTablero();
		return base;
	}

	/** Sortea un objetivo nuevo entre las casillas libres. */
	public Posicion nuevoObjetivo() {
		exigirTablero();
		return tablero.obtenerObjetivo();
	}

	public ElementoDelUniverso inspeccionarPosicion() {
		exigirTablero();
		return tablero.inspeccionarPosicion();
	}

	/**
	 * Un turno de navegación hacia el destino. Las reglas deciden si corresponde
	 * salto normal, preparar/avanzar el Warp o no moverse, y mueven la nave.
	 * Después se actualiza el tablero con la nueva posición de la nave.
	 */
	public void avanzarHacia(Posicion destinoFinal) {
		exigirTablero();
		exigirReglas();
		Posicion anterior = nave.getPosicion();
		reglas.evaluarYEjecutarPaso(nave, destinoFinal);
		Posicion actual = nave.getPosicion();

		// Tablero.moverNave exige una posición dentro del tablero: esa garantía es nuestra.
		if (!dentroDelTablero(actual)) {
			nave.setPosicion(anterior);
			registrarEvento("Asistente: la navegación llevó la nave fuera del tablero ("
					+ actual.getX() + "," + actual.getY() + "); se mantiene en su posición");
			return;
		}
		if (!actual.equals(anterior)) {
			tablero.moverNave(actual);
		}
	}

	private boolean dentroDelTablero(Posicion p) {
		return p.getX() >= 0 && p.getX() < tablero.getX()
				&& p.getY() >= 0 && p.getY() < tablero.getY();
	}

	/** Turno del tablero: Destino mueve al azar los elementos. */
	public void turnoDelTablero() {
		exigirTablero();
		destino.turnoDestino();
	}

	// ---------------------------------------------------------------
	// Encuentros y recompensas
	// ---------------------------------------------------------------

	/** El elemento aplica sus consecuencias sobre la nave (Double Dispatch en el caso de otras naves). */
	public void interactuarCon(ElementoDelUniverso elemento) {
		elemento.interactuar(nave);
	}

	/** M-02: recompensa en gemas. */
	public void agregarGemas(int cantidad) {
		nave.agregarGemas(cantidad);
	}


	/**
	 * Configura el receptor de objetivos de mision.
	 *
	 * Pre: notificador != null.
	 * Post: conserva el notificador sin modificar
	 *       recursos ni posiciones.
	 *
	 * @param notificador funcion que recibe el objetivo
	 */
	public void setNotificadorObjetivo(
			Consumer<Posicion> notificador) {

		if (notificador == null) {
			throw new IllegalArgumentException(
					"El notificador no puede ser null"
			);
		}

		this.notificadorObjetivo = notificador;
	}

	/**
	 * Publica la coordenada objetivo de una mision.
	 *
	 * Pre: objetivo != null.
	 *
	 * Post:
	 * - Si existe un receptor, le comunica el objetivo.
	 * - No modifica los recursos ni la ruta.
	 * - Permite funcionar sin receptor en pruebas aisladas.
	 *
	 * @param objetivo posicion objetivo de la mision
	 */
	public void publicarObjetivo(Posicion objetivo) {

		if (objetivo == null) {
			throw new IllegalArgumentException(
					"El objetivo no puede ser null"
			);
		}

		if (notificadorObjetivo != null) {
			notificadorObjetivo.accept(objetivo);
		}
	}

}