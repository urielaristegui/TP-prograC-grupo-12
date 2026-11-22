package modelo.misiones;

import java.util.concurrent.atomic.AtomicInteger;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/**
 - M-01: llegar hasta la nave aliada y entregarle suministros.
 *
 - La nave aliada tiene posicion y recursos propios.
 - Su posicion coincide con el objetivo de la mision.
 *
 - Se representa como objetivo de asistencia, no como
 - nave enemiga del Tablero, evitando un combate.
 */
public class Intercepcion extends Mision {

	private static final int COMBUSTIBLE_DESPEGUE = 4;
	private static final int ENERGIA_DESPEGUE = 5;
	private static final int DESGASTE_DESPEGUE = 4;
	private static final int MAXIMO_TRANSFERENCIA = 50;

	private static final AtomicInteger SIGUIENTE_ID_ALIADA =
			new AtomicInteger(1_000_000);

	private final int combustibleSolicitado;
	private final int energiaSolicitada;
	private final Nave naveAliada;

	/**
	 * construye una mision de asistencia con las cantidades de combustible y energia a transferir.<br>
	 * <b>pre:</b> AC != null, 0 <= combustibleSolicitado <= 50 y 0 <= energiaSolicitada <= 50.<br>
	 * <b>post:</b> inicializa la mision base con AC, fija los recursos solicitados y crea una nave aliada carguera con capacidad reservada para recibir hasta 50 unidades de cada recurso.<br>
	 *
	 * @param AC asistente de comandos utilizado para la mision. AC != null.
	 * @param combustibleSolicitado combustible a transferir a la nave aliada, entre 0 y 50.
	 * @param energiaSolicitada energia a transferir a la nave aliada, entre 0 y 50.
	 * @throws IllegalArgumentException si combustibleSolicitado o energiaSolicitada estan fuera del rango [0, 50].
	 */
	public Intercepcion(
			AsistenteDeComandos AC,
			int combustibleSolicitado,
			int energiaSolicitada) {

		super(AC);

		if (combustibleSolicitado < 0
				|| combustibleSolicitado > MAXIMO_TRANSFERENCIA
				|| energiaSolicitada < 0
				|| energiaSolicitada > MAXIMO_TRANSFERENCIA) {

			throw new IllegalArgumentException(
					"La transferencia debe estar entre 0 y "
							+ MAXIMO_TRANSFERENCIA
			);
		}

		this.combustibleSolicitado = combustibleSolicitado;
		this.energiaSolicitada = energiaSolicitada;

		// Un Carguero comienza con:
		// - 100 combustible
		// - 60 energia
		//
		// Reservamos capacidad para recibir
		// hasta 50 unidades de cada recurso.

		naveAliada = NaveFactory.crearNave(
				TipoNave.CARGUERO,
				SIGUIENTE_ID_ALIADA.getAndIncrement()
		);

		naveAliada.consumirRecursos(50, 50, 0);
	}

	@Override
	protected int getCombustibleRequerido() {
		return COMBUSTIBLE_DESPEGUE;
	}

	@Override
	protected int getEnergiaRequerida() {
		return ENERGIA_DESPEGUE;
	}

	@Override
	protected int getDesgasteGenerado() {
		return DESGASTE_DESPEGUE;
	}

	@Override
	protected Posicion definirObjetivo() {

		Posicion objetivo = AC.nuevoObjetivo();

		naveAliada.setPosicion(objetivo);

		registrar(
				"nave aliada ubicada en ("
						+ objetivo.getX()
						+ ","
						+ objetivo.getY()
						+ ")"
		);

		return objetivo;
	}

	@Override
	protected boolean verificarEnDestino() {

		return naveAliada.getPosicion() != null

				&& AC.posicionNave().equals(
				naveAliada.getPosicion()
		)

				&& AC.getCombustible()
				> combustibleSolicitado

				&& AC.getEnergia()
				> energiaSolicitada;
	}

	@Override
	protected void aplicarCierre() {

		AC.transferirRecursos(
				naveAliada,
				combustibleSolicitado,
				energiaSolicitada
		);
	}

	@Override
	protected String describirCierre() {

		return "entrega de "
				+ combustibleSolicitado
				+ " de combustible y "
				+ energiaSolicitada
				+ " de energia a la nave aliada "
				+ naveAliada.getId();
	}

	/**
	 * devuelve la nave aliada asociada a esta mision de asistencia.<br>
	 * <b>pre:</b> la mision esta inicializada.<br>
	 * <b>post:</b> retorna la instancia de naveAliada sin modificar su estado.<br>
	 *
	 * @return nave aliada de esta mision.
	 */
	public Nave getNaveAliada() {
		return naveAliada;
	}
}