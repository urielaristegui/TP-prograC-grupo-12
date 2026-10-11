package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;

public abstract class Mision {
	protected AsistenteDeComandos AC;
	protected ResultadoMision resultado;
	protected InformeMision informe;

	public Mision(AsistenteDeComandos AC) {
		this.AC = AC;
	}

	// Template Method: fija el orden. No se redefine en las subclases.
	public final boolean director() {
		if (!preparar()) {
			AC.registrarEvento(getClass().getSimpleName() + ": preparación fallida, misión cancelada");
			return false; // resultado queda en null: no llegó a evaluarse
		}

		ejecutar();
		resultado = evaluar();
		AC.registrarEvento(getClass().getSimpleName() + ": evaluación -> " + resultado);

		cerrar();
		return true;
	}

	// Hooks obligatorios: protected, no public -> no se pueden llamar sueltos desde afuera
	protected abstract void ejecutar();
	protected abstract ResultadoMision evaluar();

	protected boolean preparar() {
		return AC.tieneRecursosDisponibles(getCombustibleRequerido(), getEnergiaRequerida());
	}

	protected void cerrar() {
		if (resultado == ResultadoMision.EXITOSA) {
			AC.preparaSalto();
			AC.salta();
		}

		String prefijo = getClass().getSimpleName();
		AC.registrarEvento(prefijo + ": cerrada, resultado " + resultado);

		String recursos = "Combustible: " + getCombustibleRequerido()
				+ ", Energía: " + getEnergiaRequerida()
				+ ", Desgaste: " + getDesgasteGenerado();

		informe = new InformeMision(
				prefijo,
				resultado,
				AC.eventosDe(prefijo),
				recursos,
				AC.snapshotEstadoNave(),
				"" // observaciones: sin contenido hasta que existan misiones concretas
		);
	}


	public InformeMision getInforme() {
		return informe;
	}

	protected abstract int getCombustibleRequerido();
	protected abstract int getEnergiaRequerida();
	protected abstract int getDesgasteGenerado();
}