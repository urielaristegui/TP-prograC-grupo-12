package modelo.asistenteDeComandos;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Destino;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;

/**
 * Único punto de contacto entre las misiones y el resto del sistema
 * (nave, bitácora, tablero). Coordina y delega: no decide nada del dominio.
 */
public class AsistenteDeComandos {

	// TODO: idealmente Nave expone estas constantes en vez de repetirlas acá.
	private static final int CAPACIDAD_MAXIMA = 100;
	private static final int DESGASTE_MAXIMO = 100;

	private Nave nave;
	private Bitacora bitacora;
	private Tablero tablero;
	private Destino destino;
	private Posicion base;

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

	/** La posición de la nave al recibir el tablero es la base de origen. */
	public void setTablero(Tablero tablero) {
		if (tablero == null) {
			throw new IllegalArgumentException("El tablero no puede ser null");
		}
		this.tablero = tablero;
		this.destino = new Destino(tablero);
		this.base = tablero.getPosicionNave();
	}

	private void exigirTablero() {
		if (tablero == null) {
			throw new IllegalStateException("El asistente no tiene un tablero asignado");
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

	/** Regla del enunciado: desgaste en 100, o energía o combustible en 0. */
	public boolean naveDestruida() {
		return nave.getDesgaste() >= DESGASTE_MAXIMO
				|| nave.getEnergia() <= 0
				|| nave.getCombustible() <= 0;
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
	// Tablero
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

	/** Radar de corto alcance: vecindario de 3x3, con la nave en [1][1]. */
	public ElementoDelUniverso[][] escanearCortoAlcance() {
		exigirTablero();
		return tablero.observarAlrededores();
	}

	public void moverNave(Posicion posicion) {
		exigirTablero();
		tablero.moverNave(posicion);
	}

	/** Turno del tablero: Destino mueve al azar los elementos. */
	public void turnoDelTablero() {
		exigirTablero();
		destino.turnoDestino();
	}

	// ---------------------------------------------------------------
	// PENDIENTES: dependen de lo que expongan Nave, MotorWarp y los elementos
	// ---------------------------------------------------------------

	/**
	 * Salto normal (2 de combustible).
	 * @return true si el desplazamiento se realizó
	 */
	public boolean saltar() {
		throw new UnsupportedOperationException("PENDIENTE: delegar en Nave/MotorWarp.saltar()");
	}

	/**
	 * Salto Warp (2 turnos de preparación, 5 de energía, 1 de enfriamiento).
	 * @return true sólo cuando el salto efectivamente se ejecutó
	 *         (false mientras prepara, o si se aborta por falta de energía)
	 */
	public boolean saltarWarp() {
		throw new UnsupportedOperationException("PENDIENTE: delegar en Nave/MotorWarp.saltarWarp()");
	}

	/** Aplica las consecuencias del encuentro (Double Dispatch según el elemento). */
	public void interactuarCon(ElementoDelUniverso elemento) {
		throw new UnsupportedOperationException("PENDIENTE: depende de la interfaz ElementoDelUniverso");
	}

	/** M-02: la nave todavía no tiene gemas. */
	public void agregarGemas(int cantidad) {
		throw new UnsupportedOperationException("PENDIENTE: Nave necesita un recurso 'gemas'");
	}
}