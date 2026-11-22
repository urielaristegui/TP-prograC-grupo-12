package modelo.asistenteDeComandos;

import modelo.bitacora.Bitacora;
import modelo.destinoytablero.Destino;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.sensores.ReglasNavegacion;
import modelo.elementos.Asteroide;
import java.util.*;
import java.util.function.Consumer;


/**
 * unico punto de contacto entre las misiones y el resto del sistema (nave, bitacora, tablero, navegacion).<br>
 * coordina y delega sin decidir cuestiones propias del dominio.<br>
 * <b>invariante:</b> CAPACIDAD_MAXIMA > 0 && DESGASTE_MAXIMO > 0.
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

	/**
	 * crea un asistente de comandos con la nave y la bitacora indicadas.<br>
	 * <b>pre:</b> ninguna.<br>
	 * <b>post:</b> se asignan nave y bitacora mediante sus respectivos setters.<br>
	 *
	 * @param nave nave operada por el asistente.
	 * @param bitacora bitacora donde se registran los eventos.
	 */
	public AsistenteDeComandos(Nave nave, Bitacora bitacora) {
		setNave(nave);
		setBitacora(bitacora);
	}

	/**
	 * crea un asistente de comandos con nave, bitacora y tablero.<br>
	 * <b>pre:</b> tablero != null y nave != null.<br>
	 * <b>post:</b> se inicializan nave, bitacora y tablero con su destino y base.<br>
	 *
	 * @param nave nave operada por el asistente.
	 * @param bitacora bitacora donde se registran los eventos.
	 * @param tablero tablero inicial del universo. tablero != null.
	 * @throws IllegalArgumentException si tablero es null.
	 */
	public AsistenteDeComandos(Nave nave, Bitacora bitacora, Tablero tablero) {
		this(nave, bitacora);
		setTablero(tablero);
	}

	/**
	 * asigna la bitacora utilizada por el asistente.<br>
	 * <b>pre:</b> el asistente esta inicializado.<br>
	 * <b>post:</b> se actualiza el atributo bitacora.<br>
	 *
	 * @param bitacora bitacora a asignar.
	 */
	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}

	/**
	 * asigna la nave operada por el asistente.<br>
	 * <b>pre:</b> el asistente esta inicializado.<br>
	 * <b>post:</b> se actualiza el atributo nave.<br>
	 *
	 * @param nave nave a asignar.
	 */
	public void setNave(Nave nave) {
		this.nave = nave;
	}

	/**
	 * recibe el tablero, crea el destino que lo hace evolucionar y deja a la nave en la posicion inicial como base de origen.<br>
	 * <b>pre:</b> tablero != null y nave != null.<br>
	 * <b>post:</b> se asigna tablero, se crea destino, se guarda base y se posiciona la nave en base.<br>
	 *
	 * @param tablero tablero a asignar. tablero != null.
	 * @throws IllegalArgumentException si tablero es null.
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

	/**
	 * asigna las reglas de navegacion del asistente.<br>
	 * <b>pre:</b> reglas != null.<br>
	 * <b>post:</b> se actualiza el atributo reglas.<br>
	 *
	 * @param reglas reglas de navegacion a asignar. reglas != null.
	 * @throws IllegalArgumentException si reglas es null.
	 */
	public void setReglasNavegacion(ReglasNavegacion reglas) {
		if (reglas == null) {
			throw new IllegalArgumentException("Las reglas de navegación no pueden ser null");
		}
		this.reglas = reglas;
	}

	/**
	 * verifica que exista un tablero asignado.<br>
	 * <b>pre:</b> el asistente esta inicializado.<br>
	 * <b>post:</b> si tablero != null no modifica el estado; si es null lanza IllegalStateException.<br>
	 *
	 * @throws IllegalStateException si tablero es null.
	 */
	private void exigirTablero() {
		if (tablero == null) {
			throw new IllegalStateException("El asistente no tiene un tablero asignado");
		}
	}

	/**
	 * verifica que existan reglas de navegacion asignadas.<br>
	 * <b>pre:</b> el asistente esta inicializado.<br>
	 * <b>post:</b> si reglas != null no modifica el estado; si es null lanza IllegalStateException.<br>
	 *
	 * @throws IllegalStateException si reglas es null.
	 */
	private void exigirReglas() {
		if (reglas == null) {
			throw new IllegalStateException("El asistente no tiene reglas de navegación asignadas");
		}
	}

	// ---------------------------------------------------------------
	// Nave: recursos
	// ---------------------------------------------------------------

	/**
	 * consulta si la nave tiene al menos el combustible y la energia requeridos.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna true si combustible y energia actuales son mayores o iguales a los requeridos, false en caso contrario.<br>
	 *
	 * @param combustibleRequerido combustible minimo necesario.
	 * @param energiaRequerida energia minima necesaria.
	 * @return true si hay recursos disponibles, false en caso contrario.
	 */
	public boolean tieneRecursosDisponibles(int combustibleRequerido, int energiaRequerida) {
		return nave.getCombustible() >= combustibleRequerido
				&& nave.getEnergia() >= energiaRequerida;
	}

	/**
	 * ordena a la nave consumir combustible, energia y aplicar desgaste.<br>
	 * <b>pre:</b> nave != null y los parametros son validos para la nave.<br>
	 * <b>post:</b> se descuentan combustible y energia y se suma desgaste en la nave.<br>
	 *
	 * @param combustible combustible a consumir.
	 * @param energia energia a consumir.
	 * @param desgaste desgaste a agregar.
	 */
	public void ordenarConsumo(int combustible, int energia, int desgaste) {
		nave.consumirRecursos(combustible, energia, desgaste);
	}

	/**
	 * devuelve el combustible actual de la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna el nivel de combustible sin modificar la nave.<br>
	 *
	 * @return combustible actual.
	 */
	public int getCombustible() {
		return nave.getCombustible();
	}

	/**
	 * devuelve la energia actual de la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna el nivel de energia sin modificar la nave.<br>
	 *
	 * @return energia actual.
	 */
	public int getEnergia() {
		return nave.getEnergia();
	}

	/**
	 * devuelve el desgaste actual de la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna el nivel de desgaste sin modificar la nave.<br>
	 *
	 * @return desgaste actual.
	 */
	public int getDesgaste() {
		return nave.getDesgaste();
	}

	/**
	 * comprueba y actualiza el estado de destruccion de la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> si energia <= 0, combustible <= 0 o desgaste >= DESGASTE_MAXIMO y la nave no estaba destruida, la destruye y registra el evento; retorna nave.estaDestruida().<br>
	 *
	 * @return true si la nave esta destruida, false en caso contrario.
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
	 * transfiere recursos a una nave aliada en la misma coordenada.<br>
	 * <b>pre:</b> aliada != null, aliada != nave, !aliada.estaDestruida(), 0 <= combustible <= 50, 0 <= energia <= 50, tablero != null, ambas naves en la misma posicion, nave propia operativa con recursos restantes positivos y aliada con capacidad disponible.<br>
	 * <b>post:</b> descuenta combustible y energia de la nave propia, acredita las cantidades a aliada y registra el evento; si falla alguna validacion lanza excepcion sin modificar recursos.<br>
	 *
	 * @param aliada nave que recibe la asistencia.
	 * @param combustible combustible a transferir.
	 * @param energia energia a transferir.
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
	 * consulta las gemas acumuladas por la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna nave.getGemas() sin modificar el modelo.<br>
	 *
	 * @return gemas acumuladas por la nave.
	 */
	public int getGemas() {
		return nave.getGemas();
	}


	/**
	 * realiza una recarga total de combustible y energia y deja el desgaste en cero mediante mantenimiento.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> carga combustible y energia faltantes hasta CAPACIDAD_MAXIMA y realiza mantenimiento en la nave.<br>
	 */
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

	/**
	 * registra un evento en la bitacora del sistema.<br>
	 * <b>pre:</b> bitacora != null.<br>
	 * <b>post:</b> invoca bitacora.registrar(evento).<br>
	 *
	 * @param evento descripcion del evento a registrar.
	 */
	public void registrarEvento(String evento) {
		bitacora.registrar(evento);
	}

	/**
	 * devuelve una descripcion del estado actual de recursos y mantenimiento de la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> retorna una cadena con combustible, energia, desgaste y aviso si requiere mantenimiento.<br>
	 *
	 * @return estado actual de la nave.
	 */
	public String snapshotEstadoNave() {
		return "Combustible: " + nave.getCombustible()
				+ ", Energía: " + nave.getEnergia()
				+ ", Desgaste: " + nave.getDesgaste()
				+ (nave.requiereMantenimiento() ? " (requiere mantenimiento)" : "");
	}

	// ---------------------------------------------------------------
	// Tablero y navegación
	// ---------------------------------------------------------------

	/**
	 * devuelve la posicion actual de la nave en el tablero.<br>
	 * <b>pre:</b> tablero != null.<br>
	 * <b>post:</b> retorna tablero.getPosicionNave().<br>
	 *
	 * @return posicion actual de la nave.
	 * @throws IllegalStateException si tablero es null.
	 */
	public Posicion posicionNave() {
		exigirTablero();
		return tablero.getPosicionNave();
	}

	/**
	 * devuelve la posicion base de origen de la nave.<br>
	 * <b>pre:</b> tablero != null.<br>
	 * <b>post:</b> retorna el atributo base.<br>
	 *
	 * @return posicion base.
	 * @throws IllegalStateException si tablero es null.
	 */
	public Posicion posicionBase() {
		exigirTablero();
		return base;
	}

	/**
	 * sortea y devuelve un nuevo objetivo entre las casillas libres del tablero.<br>
	 * <b>pre:</b> tablero != null.<br>
	 * <b>post:</b> obtiene y retorna un nuevo objetivo desde el tablero.<br>
	 *
	 * @return nueva posicion objetivo.
	 * @throws IllegalStateException si tablero es null.
	 */
	public Posicion nuevoObjetivo() {
		exigirTablero();
		return tablero.obtenerObjetivo();
	}


	/**
	 * planifica una ruta inicial utilizando el radar de largo alcance.<br>
	 * <b>pre:</b> tablero != null, reglas != null, objetivo != null y objetivo pertenece al tablero.<br>
	 * <b>post:</b> calcula una ruta ortogonal evitando asteroides detectados (o una ruta provisional directa si no hay camino visible), sin modificar posiciones, recursos ni el tablero.<br>
	 *
	 * @param objetivo coordenada de destino.
	 * @return ruta inicial de referencia sin incluir el origen.
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

	/**
	 * inspecciona el elemento del universo en la posicion actual de la nave.<br>
	 * <b>pre:</b> tablero != null.<br>
	 * <b>post:</b> retorna el elemento en la casilla actual de la nave o null si esta vacia.<br>
	 *
	 * @return elemento en la posicion actual o null.
	 * @throws IllegalStateException si tablero es null.
	 */
	public ElementoDelUniverso inspeccionarPosicion() {
		exigirTablero();
		return tablero.inspeccionarPosicion();
	}

	/**
	 * ejecuta un turno de navegacion hacia destinoFinal y actualiza el tablero si corresponde.<br>
	 * <b>pre:</b> tablero != null, reglas != null, nave != null y destinoFinal != null.<br>
	 * <b>post:</b> evalua y ejecuta un paso hacia destinoFinal; si la nave queda fuera del tablero restaura su posicion anterior y registra el evento, y si avanzo dentro del tablero actualiza tablero.<br>
	 *
	 * @param destinoFinal posicion de destino hacia la que avanza la nave.
	 * @throws IllegalStateException si tablero o reglas son null.
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

	/**
	 * indica si una posicion esta dentro de los limites del tablero.<br>
	 * <b>pre:</b> p != null y tablero != null.<br>
	 * <b>post:</b> retorna true si 0 <= p.getX() < tablero.getX() y 0 <= p.getY() < tablero.getY(), false en caso contrario.<br>
	 *
	 * @param p posicion a evaluar. p != null.
	 * @return true si esta dentro del tablero, false en caso contrario.
	 */
	private boolean dentroDelTablero(Posicion p) {
		return p.getX() >= 0 && p.getX() < tablero.getX()
				&& p.getY() >= 0 && p.getY() < tablero.getY();
	}

	/**
	 * ejecuta el turno del entorno y sincroniza las posiciones de las naves enemigas.<br>
	 * <b>pre:</b> tablero != null y destino != null.<br>
	 * <b>post:</b> ejecuta destino.turnoDestino() y actualiza la posicion de cada nave enemiga segun la grilla del tablero sin alterar la posicion ni los recursos de la nave del jugador.<br>
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

	/**
	 * hace interactuar a la nave con el elemento del universo indicado.<br>
	 * <b>pre:</b> elemento != null y nave != null.<br>
	 * <b>post:</b> invoca elemento.interactuar(nave) aplicando sus efectos sobre la nave.<br>
	 *
	 * @param elemento elemento del universo con el que interactua la nave. elemento != null.
	 */
	public void interactuarCon(ElementoDelUniverso elemento) {
		elemento.interactuar(nave);
	}

	/**
	 * agrega una cantidad de gemas a la nave.<br>
	 * <b>pre:</b> nave != null.<br>
	 * <b>post:</b> invoca nave.agregarGemas(cantidad).<br>
	 *
	 * @param cantidad cantidad de gemas a agregar.
	 */
	public void agregarGemas(int cantidad) {
		nave.agregarGemas(cantidad);
	}

	/**
	 * configura el receptor de objetivos de mision.<br>
	 * <b>pre:</b> notificador != null.<br>
	 * <b>post:</b> asigna notificadorObjetivo sin modificar recursos ni posiciones.<br>
	 *
	 * @param notificador funcion que recibe la posicion objetivo.
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
	 * publica la coordenada objetivo de una mision hacia el notificador si esta configurado.<br>
	 * <b>pre:</b> objetivo != null.<br>
	 * <b>post:</b> si notificadorObjetivo != null, invoca accept(objetivo); no modifica recursos ni ruta.<br>
	 *
	 * @param objetivo posicion objetivo de la mision.
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