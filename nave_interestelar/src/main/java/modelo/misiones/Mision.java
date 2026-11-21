package modelo.misiones;

import java.util.ArrayList;
import java.util.List;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;
import modelo.elementos.ElementoDelUniverso;

/**
 * Ciclo de vida de una misión sobre el tablero real (Template Method).
 *
 * director() fija el orden preparar -> ejecutar -> evaluar -> cerrar y no se
 * puede redefinir. Las subclases sólo aportan lo que cambia entre misiones:
 * costos de despegue, objetivo, verificación en destino y acción final.
 *
 * La misión sólo conoce al AsistenteDeComandos: nunca toca Nave ni Tablero
 * directamente. Cómo se mueve la nave en cada turno (salto normal o Warp) lo
 * deciden las reglas de navegación, a través del asistente.
 *
 * NOTA: director() contiene un bucle con pausas (Thread.sleep). Cuando haya
 * interfaz Swing hay que invocarlo desde un hilo propio, nunca desde el hilo
 * de eventos, o la pantalla se congela.
 */
public abstract class Mision {

	/** Pausa entre turnos, para que la vista pueda mostrar el avance. */
	private static final int PAUSA_MS = 1000;
	/** Tope de turnos: evita que la misión quede en un bucle infinito. */
	private static final int LIMITE_TURNOS = 500;

	protected final AsistenteDeComandos AC;
	protected ResultadoMision resultado;
	protected InformeMision informe;

	// Estado interno de la misión
	private final List<String> acciones = new ArrayList<String>();
	private List<Posicion> ruta = new ArrayList<Posicion>();
	private final List<Posicion> recorrido = new ArrayList<Posicion>();
	private Posicion objetivo;
	private boolean llegoAlDestino;
	private String motivoFallo = "";
	private int combustibleInicial;
	private int energiaInicial;
	private int desgasteInicial;

	public Mision(AsistenteDeComandos AC) {
		this.AC = AC;
	}

	// ------------------------------------------------------------------
	// Template Method
	// ------------------------------------------------------------------

	/**
	 * @return false si la misión no pudo prepararse (no llegó a ejecutarse y
	 *         no genera informe); true si recorrió las cuatro fases, aunque
	 *         haya terminado FALLIDA.
	 */
	public final boolean director() {
		if (!preparar()) {
			registrar("preparación fallida, misión cancelada");
			return false;
		}
		ejecutar();
		resultado = evaluar();
		cerrar();
		return true;
	}

	public InformeMision getInforme() {
		return informe;
	}

	// ------------------------------------------------------------------
	// Fase 1: preparar
	// ------------------------------------------------------------------


	/**
	 * Prepara la ejecucion de la mision.
	 *
	 * Pre:
	 * - AsistenteDeComandos inicializado.
	 * - Nave y tablero disponibles.
	 *
	 * Post:
	 * - Verifica los recursos iniciales.
	 * - Determina el objetivo.
	 * - Publica el objetivo para actualizar el tablero.
	 * - Calcula y almacena la ruta.
	 *
	 * @return true si pudo prepararse
	 */
	protected boolean preparar() {

		if (!AC.tieneRecursosDisponibles(
				getCombustibleRequerido(),
				getEnergiaRequerida())) {

			registrar(
					"recursos insuficientes para iniciar la mision"
			);

			return false;
		}

		// Determinar el objetivo de esta mision.
		objetivo = definirObjetivo();

		// NUEVO: informar el objetivo al universo.
		AC.publicarObjetivo(objetivo);

		Posicion origen = AC.posicionNave();

		ruta = calcularRuta(origen, objetivo);

		recorrido.add(origen);

		llegoAlDestino = origen.equals(objetivo);

		List<Posicion> planificado =
				new ArrayList<>();

		planificado.add(origen);
		planificado.addAll(ruta);

		registrar(
				"ruta planificada hacia "
						+ texto(objetivo)
						+ " (" + ruta.size()
						+ " pasos): "
						+ formatear(planificado)
		);

		return true;
	}


	/**
	 * Ruta nominal: la que seguirían las reglas de navegación si no hubiera
	 * obstáculos (un paso ortogonal por vez, primero sobre el eje de mayor
	 * distancia). No incluye el origen. El recorrido real puede diferir:
	 * los obstáculos y el salto Warp las modifican.
	 */
	private List<Posicion> calcularRuta(Posicion origen, Posicion destino) {
		List<Posicion> pasos = new ArrayList<Posicion>();
		int x = origen.getX();
		int y = origen.getY();
		while (x != destino.getX() || y != destino.getY()) {
			int dx = destino.getX() - x;
			int dy = destino.getY() - y;
			if (dx != 0 && Math.abs(dx) >= Math.abs(dy)) {
				x += Integer.signum(dx);
			} else {
				y += Integer.signum(dy);
			}
			pasos.add(new Posicion(x, y));
		}
		return pasos;
	}

	// ------------------------------------------------------------------
	// Fase 2: ejecutar
	// ------------------------------------------------------------------

	protected void ejecutar() {
		combustibleInicial = AC.getCombustible();
		energiaInicial = AC.getEnergia();
		desgasteInicial = AC.getDesgaste();

		try {
			AC.ordenarConsumo(getCombustibleRequerido(), getEnergiaRequerida(), getDesgasteGenerado());
			registrar("despegue: consumo inicial aplicado");

			int turno = 0;
			while (!AC.naveDestruida() && !llegoAlDestino && motivoFallo.isEmpty()) {
				turno++;
				if (turno > LIMITE_TURNOS) {
					motivoFallo = "se superó el límite de turnos sin llegar al destino";
					break;
				}

				turnoDeLaNave();

				// Si la nave fue destruida durante el turno,
				// la mision termina inmediatamente.
				if (AC.naveDestruida() || !motivoFallo.isEmpty()) {
					break;
				}

				// Solo avanzar el tablero si la nave sigue operativa.
				AC.turnoDelTablero();

				pausar();

			}
		} catch (IllegalArgumentException e) {
			// Nave rechazó una operación (recurso insuficiente, desgaste fuera de rango...)
			motivoFallo = "operación rechazada por la nave: " + e.getMessage();
		}

		if (AC.naveDestruida()) {
			if (motivoFallo.isEmpty()) {
				motivoFallo = "la nave quedó destruida (desgaste 100, o energía/combustible en 0)";
			}
		} else if (llegoAlDestino && motivoFallo.isEmpty() && !verificarEnDestino()) {
			motivoFallo = "al llegar no conservaba los recursos que debía entregar";
		}
	}

	private void turnoDeLaNave() {
		ElementoDelUniverso presente = AC.inspeccionarPosicion();
		if (presente != null) {
			registrar("encuentro con " + presente.getClass().getSimpleName());
			AC.interactuarCon(presente);
			if (AC.naveDestruida()) {
				return;
			}
		}

		Posicion anterior = AC.posicionNave();
		AC.avanzarHacia(objetivo);
		Posicion actual = AC.posicionNave();

		if (!actual.equals(anterior)) {
			recorrido.add(actual);
			llegoAlDestino = actual.equals(objetivo);
			registrar("avance a " + texto(actual));
		} else {
			registrar("la nave no se desplazó en este turno");
		}
	}

	private void pausar() {
		try {
			Thread.sleep(PAUSA_MS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			motivoFallo = "misión interrumpida";
		}
	}

	// ------------------------------------------------------------------
	// Fase 3: evaluar (arma el informe final)
	// ------------------------------------------------------------------

	protected ResultadoMision evaluar() {
		ResultadoMision res = motivoFallo.isEmpty() ? ResultadoMision.EXITOSA : ResultadoMision.FALLIDA;
		registrar("recorrido real: " + formatear(recorrido));
		registrar("evaluación -> " + res);

		String balance = "Combustible " + signado(AC.getCombustible() - combustibleInicial)
				+ ", Energía " + signado(AC.getEnergia() - energiaInicial)
				+ ", Desgaste " + signado(AC.getDesgaste() - desgasteInicial);

		String observaciones = (res == ResultadoMision.EXITOSA)
				? "Se aplicará al cierre: " + describirCierre()
				: motivoFallo;

		// El estado de la nave del informe es previo al cierre (el cierre se aplica después).
		informe = new InformeMision(
				getClass().getSimpleName(),
				res,
				new ArrayList<String>(acciones),
				balance,
				AC.snapshotEstadoNave(),
				observaciones);
		return res;
	}

	private static String signado(int valor) {
		return (valor >= 0 ? "+" : "") + valor;
	}

	private static String texto(Posicion p) {
		return "(" + p.getX() + "," + p.getY() + ")";
	}

	private static String formatear(List<Posicion> puntos) {
		StringBuilder camino = new StringBuilder();
		for (Posicion p : puntos) {
			if (camino.length() > 0) {
				camino.append(" -> ");
			}
			camino.append(texto(p));
		}
		return camino.toString();
	}

	// ------------------------------------------------------------------
	// Fase 4: cerrar (impacta lo ganado o entregado)
	// ------------------------------------------------------------------

	protected void cerrar() {
		if (resultado == ResultadoMision.EXITOSA) {
			aplicarCierre();
			registrar("cierre aplicado: " + describirCierre());
		} else {
			registrar("cierre sin impacto: la misión fracasó");
		}
	}

	// ------------------------------------------------------------------
	// Utilidad
	// ------------------------------------------------------------------

	/** Guarda la acción en la lista propia de la misión y en la Bitácora. */
	protected void registrar(String accion) {
		acciones.add(accion);
		AC.registrarEvento(getClass().getSimpleName() + ": " + accion);
	}

	// ------------------------------------------------------------------
	// Lo que cambia en cada misión
	// ------------------------------------------------------------------

	protected abstract int getCombustibleRequerido();
	protected abstract int getEnergiaRequerida();
	protected abstract int getDesgasteGenerado();

	/** Posición a la que hay que llegar. */
	protected abstract Posicion definirObjetivo();

	/** Al llegar: ¿conserva la nave lo que debe entregar? */
	protected abstract boolean verificarEnDestino();

	/** Impacto definitivo sobre la nave al completar la misión. */
	protected abstract void aplicarCierre();

	/** Texto breve de lo que hace aplicarCierre(), para informe y bitácora. */
	protected abstract String describirCierre();
}