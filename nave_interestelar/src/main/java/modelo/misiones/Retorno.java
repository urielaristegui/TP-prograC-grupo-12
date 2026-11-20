package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.destinoytablero.Posicion;

/** M-03: volver a la base de origen, recargar todo y reparar el desgaste. */
public class Retorno extends Mision {

	private static final int COMBUSTIBLE_DESPEGUE = 4;
	private static final int ENERGIA_DESPEGUE = 0;
	private static final int DESGASTE_DESPEGUE = 4;

	public Retorno(AsistenteDeComandos AC) {
		super(AC);
	}

	@Override
	protected int getCombustibleRequerido() { return COMBUSTIBLE_DESPEGUE; }

	@Override
	protected int getEnergiaRequerida() { return ENERGIA_DESPEGUE; }

	@Override
	protected int getDesgasteGenerado() { return DESGASTE_DESPEGUE; }

	@Override
	protected Posicion definirObjetivo() {
		// PENDIENTE Tablero: hoy no hay forma de registrar la base como objetivo
		// (falta fijarObjetivo(Posicion)); Destino podría poner un elemento encima.
		return AC.posicionBase();
	}

	@Override
	protected boolean verificarEnDestino() {
		return true; // no hay nada que entregar
	}

	@Override
	protected void aplicarCierre() {
		AC.recargaTotalYMantenimiento();
	}

	@Override
	protected String describirCierre() {
		return "recarga total de combustible y energía, y desgaste en 0";
	}
}