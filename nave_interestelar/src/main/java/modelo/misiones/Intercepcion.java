package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;

/** M-01: alcanzar a la nave aliada y transferirle recursos de auxilio. */
public class Intercepcion extends Mision {

	private static final int COMBUSTIBLE_DESPEGUE = 4;
	private static final int ENERGIA_DESPEGUE = 5;
	private static final int DESGASTE_DESPEGUE = 4;
	private static final int MAXIMO_TRANSFERENCIA = 50;

	private final int combustibleSolicitado;
	private final int energiaSolicitada;

	/**
	 * @param combustibleSolicitado entre 0 y 50
	 * @param energiaSolicitada entre 0 y 50
	 */
	public Intercepcion(AsistenteDeComandos AC, int combustibleSolicitado, int energiaSolicitada) {
		super(AC);
		if (combustibleSolicitado < 0 || combustibleSolicitado > MAXIMO_TRANSFERENCIA
				|| energiaSolicitada < 0 || energiaSolicitada > MAXIMO_TRANSFERENCIA) {
			throw new IllegalArgumentException("La transferencia debe estar entre 0 y " + MAXIMO_TRANSFERENCIA);
		}
		this.combustibleSolicitado = combustibleSolicitado;
		this.energiaSolicitada = energiaSolicitada;
	}

	@Override
	protected int getCombustibleRequerido() { return COMBUSTIBLE_DESPEGUE; }

	@Override
	protected int getEnergiaRequerida() { return ENERGIA_DESPEGUE; }

	@Override
	protected int getDesgasteGenerado() { return DESGASTE_DESPEGUE; }

	@Override
	protected Posicion definirObjetivo() {
		return AC.nuevoObjetivo(); // la nave aliada está en el objetivo
	}

	@Override
	protected boolean verificarEnDestino() {
		return AC.tieneRecursosDisponibles(combustibleSolicitado, energiaSolicitada);
	}

	@Override
	protected void aplicarCierre() {
		AC.transferirRecursos(combustibleSolicitado, energiaSolicitada);
	}

	@Override
	protected String describirCierre() {
		return "transferencia de " + combustibleSolicitado + " de combustible y "
				+ energiaSolicitada + " de energía a la nave aliada";
	}
}