package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;

public class Intercepcion extends Mision {
	private int combustibleRequerido;
	private int energiaRequerida;
	private int desgaste;

	public Intercepcion(AsistenteDeComandos AC, int combustibleRequerido, int energiaRequerida, int desgaste) {
		super(AC);
		this.combustibleRequerido = combustibleRequerido;
		this.energiaRequerida = energiaRequerida;
		this.desgaste = desgaste;
	}

	protected int getCombustibleRequerido() { return combustibleRequerido; }

	protected int getEnergiaRequerida() { return energiaRequerida; }

	protected int getDesgasteGenerado() { return desgaste; }

	protected void ejecutar() {
		AC.registrarEvento("M1 Intercepción: aproximando al objetivo");
		AC.ordenarConsumo(getCombustibleRequerido(), getEnergiaRequerida(), getDesgasteGenerado());
	}

	protected ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	}
}
