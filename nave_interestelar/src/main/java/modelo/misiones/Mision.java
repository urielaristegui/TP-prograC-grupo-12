package modelo.misiones;

import java.util.ArrayList;
import java.util.List;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;
import modelo.elementos.ElementoDelUniverso;

/**
 * ciclo de vida de una mision sobre el tablero real aplicando el patron template method.<br>
 * coordina preparar, ejecutar, evaluar y cerrar a traves de AsistenteDeComandos.<br>
 * <b>invariante:</b> PAUSA_MS >= 0 && LIMITE_TURNOS > 0 && acciones != null && ruta != null && recorrido != null.
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
	private int gemasInicial;

	/**
	 * construye una mision asociada a un asistente de comandos.<br>
	 * <b>pre:</b> ninguna.<br>
	 * <b>post:</b> inicializa el atributo AC con la instancia recibida.<br>
	 *
	 * @param AC asistente de comandos utilizado para coordinar la mision.
	 */
	public Mision(AsistenteDeComandos AC) {
		this.AC = AC;
	}

	// ------------------------------------------------------------------
	// Template Method
	// ------------------------------------------------------------------

	/**
	 * ejecuta el ciclo de vida de la mision en el orden preparar, ejecutar, evaluar y cerrar.<br>
	 * <b>pre:</b> AC != null, nave y tablero disponibles.<br>
	 * <b>post:</b> si supera preparar(), ejecuta ejecutar(), evaluar() y cerrar() y retorna true; si falla la preparacion, registra el fallo sin consumir recursos, genera el informe y retorna false.<br>
	 *
	 * @return true si se supero la preparacion, false en caso contrario.
	 */
	public final boolean director() {

		if (!preparar()) {

			registrar(
					"preparacion fallida, mision cancelada"
			);

			// Guardar el estado sin realizar consumos.
			combustibleInicial = AC.getCombustible();
			energiaInicial = AC.getEnergia();
			desgasteInicial = AC.getDesgaste();
			gemasInicial = AC.getGemas();

			resultado = ResultadoMision.FALLIDA;

			if (motivoFallo.isEmpty()) {
				motivoFallo = "no se supero la preparacion";
			}

			// Informe del intento rechazado.
			informe = construirInforme(
					resultado,
					true
			);

			return false;
		}

		ejecutar();

		resultado = evaluar();

		cerrar();

		return true;
	}

	/**
	 * devuelve el informe de la mision construido o actualizado hasta el momento.<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna el atributo informe sin modificar el estado.<br>
	 *
	 * @return informe de la mision o null si aun no fue generado.
	 */
	public InformeMision getInforme() {
		return informe;
	}

	// ------------------------------------------------------------------
	// Fase 1: preparar
	// ------------------------------------------------------------------

	/**
	 * prepara la ejecucion de la mision verificando recursos, definiendo el objetivo y calculando la ruta.<br>
	 * <b>pre:</b> AC != null, nave y tablero disponibles.<br>
	 * <b>post:</b> si hay recursos suficientes, fija objetivo, lo publica, calcula y almacena ruta, registra el origen en recorrido y retorna true; si faltan recursos, registra el motivo y retorna false.<br>
	 *
	 * @return true si pudo prepararse, false en caso contrario.
	 */
	protected boolean preparar() {


		if (!AC.tieneRecursosDisponibles(
				getCombustibleRequerido(),
				getEnergiaRequerida())) {

			motivoFallo =
					"recursos insuficientes para iniciar la mision";

			registrar(motivoFallo);

			return false;
		}


		// Determinar el objetivo de esta mision.
		objetivo = definirObjetivo();

		// NUEVO: informar el objetivo al universo.
		AC.publicarObjetivo(objetivo);

		Posicion origen = AC.posicionNave();

		// Calcular la ruta utilizando el radar de largo alcance.
		ruta = AC.planificarRutaConRadar(objetivo);

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
	 * calcula una ruta nominal ortogonal paso a paso desde origen hasta destino sin considerar obstaculos.<br>
	 * <b>pre:</b> origen != null y destino != null.<br>
	 * <b>post:</b> retorna la lista de posiciones intermedias hasta destino sin incluir origen.<br>
	 *
	 * @param origen posicion inicial.
	 * @param destino posicion final.
	 * @return lista de posiciones de la ruta nominal.
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

	/**
	 * ejecuta el consumo inicial y el ciclo de travesia por turnos hasta llegar, fallar o destruir la nave.<br>
	 * <b>pre:</b> la mision fue preparada correctamente y existe una nave operativa.<br>
	 * <b>post:</b> aplica el consumo de despegue, ejecuta turnos de nave y tablero, procesa enfriamiento pendiente si corresponde y verifica las condiciones finales y en destino.<br>
	 */
	protected void ejecutar() {

		combustibleInicial = AC.getCombustible();
		energiaInicial = AC.getEnergia();
		desgasteInicial = AC.getDesgaste();
		gemasInicial = AC.getGemas();

		try {

			AC.ordenarConsumo(
					getCombustibleRequerido(),
					getEnergiaRequerida(),
					getDesgasteGenerado()
			);

			registrar("despegue: consumo inicial aplicado");

			int turno = 0;

			while (!AC.naveDestruida()
					&& !llegoAlDestino
					&& motivoFallo.isEmpty()) {

				turno++;

				if (turno > LIMITE_TURNOS) {

					motivoFallo =
							"se supero el limite de turnos "
									+ "sin llegar al destino";

					break;
				}

				// Turno de la nave.
				turnoDeLaNave();

				if (AC.naveDestruida()
						|| !motivoFallo.isEmpty()) {

					break;
				}

				// Turno del tablero.
				AC.turnoDelTablero();

				pausar();
			}

			// =========================================
			// NUEVO: ENFRIAMIENTO FINAL DEL WARP
			// =========================================

			// Si el ultimo Warp termino exactamente
			// en el objetivo, queda pendiente procesar
			// un turno de enfriamiento.
			//
			// ReglasNavegacion procesa ese enfriamiento
			// antes de calcular cualquier otro movimiento.
			//
			// Si no hay enfriamiento pendiente y ya estamos
			// en destino, esta llamada no mueve la nave
			// ni consume combustible.

			if (llegoAlDestino
					&& motivoFallo.isEmpty()
					&& !AC.naveDestruida()) {

				AC.avanzarHacia(objetivo);
			}

		} catch (IllegalArgumentException e) {

			motivoFallo =
					"operacion rechazada por la nave: "
							+ e.getMessage();
		}

		// =========================================
		// VERIFICACIONES FINALES
		// =========================================

		if (AC.naveDestruida()) {

			if (motivoFallo.isEmpty()) {

				motivoFallo =
						"la nave quedo destruida "
								+ "(desgaste 100, energia o combustible en 0)";
			}

		} else if (llegoAlDestino
				&& motivoFallo.isEmpty()
				&& !verificarEnDestino()) {

			motivoFallo =
					"al llegar no conservaba los recursos "
							+ "que debia entregar";
		}
	}

	/**
	 * ejecuta el turno de la nave inspeccionando su casilla actual, interactuando si hay elemento y avanzando hacia el objetivo.<br>
	 * <b>pre:</b> AC != null y objetivo != null.<br>
	 * <b>post:</b> si hay elemento en la posicion actual interactua con el; si la nave sigue operativa intenta avanzar hacia objetivo y actualiza recorrido y llegoAlDestino si se desplazo.<br>
	 */
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

	/**
	 * pausa brevemente la ejecucion entre turnos para permitir la observacion visual.<br>
	 * <b>pre:</b> PAUSA_MS >= 0.<br>
	 * <b>post:</b> espera PAUSA_MS milisegundos; si el hilo es interrumpido, restaura el estado de interrupcion y registra motivoFallo.<br>
	 */
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

	/**
	 * evalua el resultado de la mision y construye el informe correspondiente.<br>
	 * <b>pre:</b> la fase ejecutar finalizo y se registraron los recursos iniciales.<br>
	 * <b>post:</b> retorna EXITOSA si motivoFallo esta vacio o FALLIDA en caso contrario, registra el recorrido y la evaluacion y actualiza informe.<br>
	 *
	 * @return resultado de la evaluacion de la mision.
	 */
	protected ResultadoMision evaluar() {

		ResultadoMision res = motivoFallo.isEmpty()
				? ResultadoMision.EXITOSA
				: ResultadoMision.FALLIDA;

		registrar(
				"recorrido real: " + formatear(recorrido)
		);

		registrar(
				"evaluacion -> " + res
		);

		informe = construirInforme(res, false);

		return res;
	}

	/**
	 * construye un informe inmutable con el balance de recursos respecto al inicio de la mision.<br>
	 * <b>pre:</b> res != null y se registraron los recursos iniciales.<br>
	 * <b>post:</b> retorna un InformeMision con el balance de combustible, energia, desgaste y gemas sin modificar recursos.<br>
	 *
	 * @param res resultado de la mision.
	 * @param finalizado indica si la mision ya completo su cierre.
	 * @return informe construido con el balance actual.
	 */
	private InformeMision construirInforme(
			ResultadoMision res,
			boolean finalizado) {

		String balance =
				"Combustible "
						+ signado(
						AC.getCombustible() - combustibleInicial
				)

						+ ", Energia "
						+ signado(
						AC.getEnergia() - energiaInicial
				)

						+ ", Desgaste "
						+ signado(
						AC.getDesgaste() - desgasteInicial
				)

						+ ", Gemas "
						+ signado(
						AC.getGemas() - gemasInicial
				);

		String observaciones;

		if (res == ResultadoMision.FALLIDA) {

			observaciones = motivoFallo;

		} else {

			observaciones =
					(finalizado
							? "Cierre completado: "
							: "Pendiente de cierre: ")
							+ describirCierre();
		}

		return new InformeMision(
				getClass().getSimpleName(),
				res,
				new ArrayList<String>(acciones),
				balance,
				AC.snapshotEstadoNave(),
				observaciones
		);
	}

	/**
	 * formatea un entero anteponiendo el signo '+' si es mayor o igual a cero.<br>
	 * <b>pre:</b> ninguna.<br>
	 * <b>post:</b> retorna la representacion con signo del valor entero.<br>
	 *
	 * @param valor numero entero a formatear.
	 * @return cadena con signo antepuesto si corresponde.
	 */
	private static String signado(int valor) {
		return (valor >= 0 ? "+" : "") + valor;
	}

	/**
	 * formatea una posicion como par ordenado "(x,y)".<br>
	 * <b>pre:</b> p != null.<br>
	 * <b>post:</b> retorna "(x,y)" con las coordenadas de p.<br>
	 *
	 * @param p posicion a formatear.
	 * @return representacion textual de la posicion.
	 */
	private static String texto(Posicion p) {
		return "(" + p.getX() + "," + p.getY() + ")";
	}

	/**
	 * formatea una secuencia de posiciones separadas por flechas.<br>
	 * <b>pre:</b> puntos != null y sus elementos no son nulos.<br>
	 * <b>post:</b> retorna las posiciones concatenadas con " -> ".<br>
	 *
	 * @param puntos lista de posiciones a formatear.
	 * @return cadena con el recorrido formateado.
	 */
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

	/**
	 * aplica el cierre de la mision si fue exitosa, verifica el estado de la nave y actualiza el informe final.<br>
	 * <b>pre:</b> resultado != null y se realizo la evaluacion previa.<br>
	 * <b>post:</b> si resultado es EXITOSA invoca aplicarCierre() (o marca FALLIDA si ocurre error o la nave queda destruida) y reconstruye informe como finalizado.<br>
	 */
	protected void cerrar() {

		if (resultado == ResultadoMision.EXITOSA) {

			try {

				aplicarCierre();

				registrar(
						"cierre aplicado: "
								+ describirCierre()
				);

			} catch (
					IllegalArgumentException |
					IllegalStateException e
			) {

				resultado = ResultadoMision.FALLIDA;

				motivoFallo =
						"error al cerrar la mision: "
								+ e.getMessage();

				registrar(motivoFallo);
			}

		} else {

			registrar(
					"cierre sin impacto: la mision fracaso"
			);
		}

		// Una mision no puede considerarse exitosa
		// si la nave queda destruida en el cierre.
		if (AC.naveDestruida()) {

			resultado = ResultadoMision.FALLIDA;

			if (motivoFallo.isEmpty()) {

				motivoFallo =
						"la nave quedo destruida durante el cierre";
			}
		}

		// Reconstruir el informe con todos los cambios
		// efectuados por la fase de cierre.
		informe = construirInforme(
				resultado,
				true
		);
	}


	// ------------------------------------------------------------------
	// Utilidad
	// ------------------------------------------------------------------

	/**
	 * registra una accion en la lista interna de la mision y en la bitacora.<br>
	 * <b>pre:</b> acciones != null y AC != null.<br>
	 * <b>post:</b> agrega accion a acciones y registra el evento prefijado en la bitacora.<br>
	 *
	 * @param accion descripcion de la accion o evento ocurrido.
	 */
	protected void registrar(String accion) {
		acciones.add(accion);
		AC.registrarEvento(getClass().getSimpleName() + ": " + accion);
	}

	// ------------------------------------------------------------------
	// Lo que cambia en cada misión
	// ------------------------------------------------------------------

	/**
	 * devuelve el combustible requerido para iniciar y despegar en la mision.<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna la cantidad de combustible requerida.<br>
	 *
	 * @return combustible requerido.
	 */
	protected abstract int getCombustibleRequerido();

	/**
	 * devuelve la energia requerida para iniciar y despegar en la mision.<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna la cantidad de energia requerida.<br>
	 *
	 * @return energia requerida.
	 */
	protected abstract int getEnergiaRequerida();

	/**
	 * devuelve el desgaste generado al despegar en la mision.<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna la cantidad de desgaste generada.<br>
	 *
	 * @return desgaste generado.
	 */
	protected abstract int getDesgasteGenerado();

	/**
	 * determina la posicion objetivo de la mision.<br>
	 * <b>pre:</b> AC != null y tablero disponible.<br>
	 * <b>post:</b> retorna la posicion objetivo a alcanzar.<br>
	 *
	 * @return posicion objetivo.
	 */
	protected abstract Posicion definirObjetivo();

	/**
	 * verifica si al llegar al destino la nave cumple las condiciones requeridas para cerrar la mision.<br>
	 * <b>pre:</b> la nave alcanzo la posicion objetivo.<br>
	 * <b>post:</b> retorna true si se cumplen las condiciones en destino, false en caso contrario.<br>
	 *
	 * @return true si es valido cerrar la mision en destino, false en caso contrario.
	 */
	protected abstract boolean verificarEnDestino();

	/**
	 * aplica el efecto definitivo del cierre de la mision sobre la nave o el entorno.<br>
	 * <b>pre:</b> la mision fue evaluada como EXITOSA y se verifico el destino.<br>
	 * <b>post:</b> impacta la entrega, recoleccion o reparacion correspondiente.<br>
	 */
	protected abstract void aplicarCierre();

	/**
	 * devuelve una descripcion breve de la accion realizada en aplicarCierre().<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna el texto descriptivo del cierre.<br>
	 *
	 * @return descripcion del cierre.
	 */
	protected abstract String describirCierre();
}