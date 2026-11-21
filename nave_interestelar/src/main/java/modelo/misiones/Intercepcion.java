
package modelo.misiones;

import java.util.concurrent.atomic.AtomicInteger;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

/**
 * M-01: llegar hasta la nave aliada y entregarle suministros.
 *
 * La nave aliada tiene posicion y recursos propios.
 * Su posicion coincide con el objetivo de la mision.
 *
 * Se representa como objetivo de asistencia, no como
 * nave enemiga del Tablero, evitando un combate.
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
	 * Crea una mision de asistencia.
	 *
	 * Pre:
	 * - AC != null.
	 * - combustibleSolicitado entre 0 y 50.
	 * - energiaSolicitada entre 0 y 50.
	 *
	 * Post:
	 * - Crea una nave aliada receptora.
	 * - Reserva capacidad para recibir recursos.
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

	/**
	 * Determina la posicion de la nave aliada.
	 *
	 * Pre:
	 * - Existe un tablero.
	 * - Existe una casilla libre.
	 *
	 * Post:
	 * - La nave aliada recibe una posicion.
	 * - La posicion es el objetivo de la mision.
	 *
	 * @return posicion de la nave aliada
	 */
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

	/**
	 * Verifica que pueda realizarse la entrega.
	 *
	 * Pre: se ejecuto la fase de navegacion.
	 *
	 * Post:
	 * - Comprueba que ambas naves coincidan.
	 * - Comprueba recursos suficientes.
	 * - No modifica ninguna nave.
	 *
	 * @return true si puede realizarse la entrega
	 */
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

	/**
	 * Realiza la transferencia de recursos.
	 *
	 * Pre:
	 * - Se verificaron los recursos.
	 * - Las naves estan en la misma posicion.
	 *
	 * Post:
	 * - La nave propia entrega recursos.
	 * - La nave aliada recibe esos recursos.
	 * - No se ejecuta un enfrentamiento.
	 */
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
				+ " de energia a nave aliada "
				+ naveAliada.getId();
	}

	/**
	 * Consulta la nave auxiliada.
	 *
	 * Pre: ninguna.
	 * Post: no modifica el estado.
	 *
	 * @return nave aliada de esta mision
	 */
	public Nave getNaveAliada() {
		return naveAliada;
	}
}
