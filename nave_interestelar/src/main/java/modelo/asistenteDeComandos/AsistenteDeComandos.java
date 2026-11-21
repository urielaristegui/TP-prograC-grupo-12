package modelo.asistenteDeComandos;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Destino;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.sensores.ReglasNavegacion;

import java.util.*;
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



	/**
	 * Transfiere recursos a una nave aliada.
	 *
	 * Pre:
	 * - aliada != null y distinta de la nave propia.
	 * - Ambas naves estan en la misma coordenada.
	 * - Cantidades entre 0 y 50.
	 * - La nave propia puede realizar la transferencia
	 *   sin quedarse con recursos en cero.
	 * - La aliada tiene capacidad para recibirlos.
	 *
	 * Post:
	 * - Descuenta recursos de la nave propia.
	 * - Acredita las mismas cantidades a la aliada.
	 * - Si la validacion falla, no modifica recursos.
	 *
	 * @param aliada nave que recibe la asistencia
	 * @param combustible combustible a transferir
	 * @param energia energia a transferir
	 */
	public void transferirRecursos(
			Nave aliada,
			int combustible,
			int energia) {

		if (aliada == null
				|| aliada == nave
				|| aliada.estaDestruida()) {

			throw new IllegalArgumentException(
					"La nave aliada no es valida"
			);
		}

		if (combustible < 0
				|| combustible > 50
				|| energia < 0
				|| energia > 50) {

			throw new IllegalArgumentException(
					"La transferencia debe estar entre 0 y 50"
			);
		}

		exigirTablero();

		// Ambas naves deben ocupar la misma coordenada.
		if (aliada.getPosicion() == null
				|| !posicionNave().equals(
				aliada.getPosicion()
		)) {

			throw new IllegalStateException(
					"Las naves no estan en la misma coordenada"
			);
		}

		// La nave propia debe continuar operativa
		// despues de entregar los suministros.
		if (nave.estaDestruida()
				|| nave.getCombustible() <= combustible
				|| nave.getEnergia() <= energia) {

			throw new IllegalStateException(
					"Recursos insuficientes para una entrega segura"
			);
		}

		// Comprobar capacidad de la nave receptora.
		if (aliada.getCombustible()
				> CAPACIDAD_MAXIMA - combustible

				|| aliada.getEnergia()
				> CAPACIDAD_MAXIMA - energia) {

			throw new IllegalStateException(
					"La nave aliada no tiene capacidad disponible"
			);
		}

		// Todas las validaciones se realizan
		// antes de modificar cualquiera de las naves.
		nave.consumirRecursos(
				combustible,
				energia,
				0
		);

		if (combustible > 0) {
			aliada.cargarCombustible(combustible);
		}

		if (energia > 0) {
			aliada.cargarEnergia(energia);
		}

		registrarEvento(
				"Asistencia entregada a nave aliada "
						+ aliada.getId()
						+ ": "
						+ combustible
						+ " de combustible y "
						+ energia
						+ " de energia"
		);
	}

	/**
	 * Consulta las gemas de la nave.
	 *
	 * Pre: nave != null.
	 * Post: no modifica el modelo.
	 *
	 * @return gemas acumuladas por la nave
	 */
	public int getGemas() {
		return nave.getGemas();
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


	/**
	 * Planifica una ruta inicial utilizando el radar de largo
	 * alcance de 5x5.
	 *
	 * Pre:
	 * - El tablero y las reglas de navegacion estan configurados.
	 * - objetivo != null y pertenece al tablero.
	 *
	 * Post:
	 * - Utiliza el radar de largo alcance.
	 * - Calcula una ruta ortogonal evitando los asteroides
	 *   detectados, siempre que exista un camino.
	 * - Devuelve los pasos sin incluir el origen.
	 * - No modifica posiciones, recursos ni el tablero.
	 *
	 * Las posiciones fuera del radar se consideran
	 * transitables provisionalmente, ya que aun no
	 * fueron observadas.
	 *
	 * @param objetivo coordenada de destino
	 * @return ruta inicial de referencia
	 */
	public List<Posicion> planificarRutaConRadar(
			Posicion objetivo) {

		exigirTablero();
		exigirReglas();

		if (objetivo == null || !dentroDelTablero(objetivo)) {
			throw new IllegalArgumentException(
					"Objetivo fuera del tablero"
			);
		}

		Posicion origen = posicionNave();

		// ==========================================
		// 1. ESCANEO DE LARGO ALCANCE
		// ==========================================

		ElementoDelUniverso[][] radar =
				reglas.getSensores().escanearLargoAlcance();

		int filas = tablero.getX();
		int columnas = tablero.getY();

		boolean[][] obstaculos =
				new boolean[filas][columnas];

		// ==========================================
		// 2. IDENTIFICAR ASTEROIDES DETECTADOS
		// ==========================================

		// Traducir las coordenadas locales del radar
		// a coordenadas absolutas del tablero.

		for (int dx = -2; dx <= 2; dx++) {

			for (int dy = -2; dy <= 2; dy++) {

				int x = origen.getX() + dx;
				int y = origen.getY() + dy;

				if (x >= 0 && x < filas
						&& y >= 0 && y < columnas
						&& radar[dx + 2][dy + 2]
						instanceof Asteroide
						&& !(x == objetivo.getX()
						&& y == objetivo.getY())) {

					obstaculos[x][y] = true;
				}
			}
		}

		// ==========================================
		// 3. BUSQUEDA DE RUTA (BFS)
		// ==========================================

		boolean[][] visitado =
				new boolean[filas][columnas];

		Posicion[][] anterior =
				new Posicion[filas][columnas];

		ArrayDeque<Posicion> pendientes =
				new ArrayDeque<>();

		// Movimientos ortogonales.
		int[][] direcciones = {
				{1, 0},
				{-1, 0},
				{0, 1},
				{0, -1}
		};

		pendientes.add(origen);

		visitado[origen.getX()][origen.getY()] = true;

		while (!pendientes.isEmpty()) {

			Posicion actual = pendientes.removeFirst();

			if (actual.equals(objetivo)) {
				break;
			}

			List<Posicion> vecinos = new ArrayList<>();

			for (int[] dir : direcciones) {

				Posicion vecino = new Posicion(
						actual.getX() + dir[0],
						actual.getY() + dir[1]
				);

				if (dentroDelTablero(vecino)
						&& !visitado
						[vecino.getX()][vecino.getY()]
						&& !obstaculos
						[vecino.getX()][vecino.getY()]) {

					vecinos.add(vecino);
				}
			}

			// Priorizar las posiciones mas cercanas
			// al objetivo cuando hay alternativas.

			vecinos.sort(
					Comparator.comparingInt(p ->
							Math.abs(
									p.getX() - objetivo.getX()
							)
									+ Math.abs(
									p.getY() - objetivo.getY()
							)
					)
			);

			for (Posicion vecino : vecinos) {

				if (!visitado
						[vecino.getX()][vecino.getY()]) {

					visitado
							[vecino.getX()][vecino.getY()] = true;

					anterior
							[vecino.getX()][vecino.getY()] = actual;

					pendientes.addLast(vecino);
				}
			}
		}

		// ==========================================
		// 4. SI NO EXISTE CAMINO NORMAL VISIBLE
		// ==========================================

		if (!visitado
				[objetivo.getX()][objetivo.getY()]) {

			// No cancelar la mision: la navegacion
			// puede utilizar Warp o esperar que
			// cambie la distribucion del tablero.

			registrarEvento(
					"Radar: sin camino normal visible; "
							+ "ruta provisional directa"
			);

			List<Posicion> provisional =
					new ArrayList<>();

			int x = origen.getX();
			int y = origen.getY();

			while (x != objetivo.getX()
					|| y != objetivo.getY()) {

				if (Math.abs(objetivo.getX() - x)
						>= Math.abs(objetivo.getY() - y)
						&& x != objetivo.getX()) {

					x += Integer.signum(
							objetivo.getX() - x
					);

				} else {

					y += Integer.signum(
							objetivo.getY() - y
					);
				}

				provisional.add(
						new Posicion(x, y)
				);
			}

			return provisional;
		}

		// ==========================================
		// 5. RECONSTRUIR LA RUTA CALCULADA
		// ==========================================

		List<Posicion> ruta = new ArrayList<>();

		for (Posicion p = objetivo;
			 !p.equals(origen);
			 p = anterior[p.getX()][p.getY()]) {

			ruta.add(p);
		}

		Collections.reverse(ruta);

		registrarEvento(
				"Radar de largo alcance: ruta inicial "
						+ "planificada con "
						+ ruta.size()
						+ " pasos; sectores lejanos aun desconocidos"
		);

		return ruta;
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


	/**
	 * Ejecuta el turno del entorno y sincroniza las
	 * posiciones de las naves enemigas.
	 *
	 * Pre:
	 * - Existe un tablero configurado.
	 *
	 * Post:
	 * - Destino desplaza aleatoriamente los elementos.
	 * - Cada nave enemiga conoce su posicion real.
	 * - No modifica la posicion ni los recursos
	 *   de la nave del jugador.
	 * - No modifica las clases Tablero ni Destino.
	 */
	public void turnoDelTablero() {

		exigirTablero();

		// 1. Destino mueve los elementos del universo.
		destino.turnoDestino();

		// 2. Obtener el estado actualizado del tablero.
		ElementoDelUniverso[][] grilla =
				tablero.verTablero();

		// 3. Sincronizar las posiciones de las naves enemigas.
		for (int fila = 0; fila < grilla.length; fila++) {

			for (int columna = 0;
				 columna < grilla[fila].length;
				 columna++) {

				if (grilla[fila][columna] instanceof Nave enemiga
						&& enemiga != nave) {

					enemiga.setPosicion(
							new Posicion(fila, columna)
					);
				}
			}
		}
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