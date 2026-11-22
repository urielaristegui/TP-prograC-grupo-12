package modelo.misiones;

import java.util.Random;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;

/** M-02: llegar al punto de riquezas y recolectar gemas. */
public class Recoleccion extends Mision {

	private static final int COMBUSTIBLE_DESPEGUE = 4;
	private static final int ENERGIA_DESPEGUE = 10;
	private static final int DESGASTE_DESPEGUE = 4;
	private static final int GEMAS_MINIMAS = 20;
	private static final int GEMAS_MAXIMAS = 100;

	private final int gemas;

	/** Recompensa sorteada entre 20 y 100 gemas.
	 * <br>
	 * <b>pre:</b> AC != null.<br>
	 * <b>post:</b> se inicializa la mision base con AC y se sortea la cantidad de gemas entre GEMAS_MINIMAS y GEMAS_MAXIMAS.<br>
	 *
	 * @param AC asistente de comandos utilizado para ejecutar la mision. AC != null.
	 */
	public Recoleccion(AsistenteDeComandos AC) {
		this(AC, GEMAS_MINIMAS + new Random().nextInt(GEMAS_MAXIMAS - GEMAS_MINIMAS + 1));
	}

	/** @param gemas entre 20 y 100 (útil para pruebas reproducibles)
	 * <br>
	 * <b>pre:</b> AC != null y GEMAS_MINIMAS <= gemas <= GEMAS_MAXIMAS.<br>
	 * <b>post:</b> se inicializa la mision base con AC y se asigna la cantidad de gemas indicada.<br>
	 *
	 * @param AC asistente de comandos utilizado para ejecutar la mision. AC != null.
	 * @param gemas cantidad de gemas a recolectar, entre 20 y 100.
	 * @throws IllegalArgumentException si gemas esta fuera del rango [20, 100].
	 */
	public Recoleccion(AsistenteDeComandos AC, int gemas) {
		super(AC);
		if (gemas < GEMAS_MINIMAS || gemas > GEMAS_MAXIMAS) {
			throw new IllegalArgumentException("Las gemas deben estar entre "
					+ GEMAS_MINIMAS + " y " + GEMAS_MAXIMAS);
		}
		this.gemas = gemas;
	}

	@Override
	protected int getCombustibleRequerido() { return COMBUSTIBLE_DESPEGUE; }

	@Override
	protected int getEnergiaRequerida() { return ENERGIA_DESPEGUE; }

	@Override
	protected int getDesgasteGenerado() { return DESGASTE_DESPEGUE; }

	@Override
	protected Posicion definirObjetivo() {
		return AC.nuevoObjetivo(); // las riquezas están en el objetivo
	}

	@Override
	protected boolean verificarEnDestino() {
		return true; // no hay nada que entregar
	}

	@Override
	protected void aplicarCierre() {
		AC.agregarGemas(gemas);
	}

	@Override
	protected String describirCierre() {
		return "recolección de " + gemas + " gemas";
	}
}