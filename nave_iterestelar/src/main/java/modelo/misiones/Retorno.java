package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;

public class Retorno extends Mision {
	private int energiaRequerida;
	private int combustibleRequerido;
	private int desgaste;

	public Retorno(AsistenteDeComandos AC, int combustibleRequerido, int energiaRequerida, int desgaste) {
		super(AC);
		this.combustibleRequerido = combustibleRequerido;
		this.energiaRequerida = energiaRequerida;
		this.desgaste = desgaste;
	}

	@Override
	protected int getCombustibleRequerido() { return combustibleRequerido; }

	@Override
	protected int getEnergiaRequerida() { return energiaRequerida; }

	@Override
	protected int getDesgasteGenerado() { return desgaste; }

	@Override
	protected void ejecutar() {
		// TODO: definir si corresponde AC.registrarEvento(...) acá,
		// igual que en Intercepcion y Recoleccion
		AC.ordenarConsumo(this.getCombustibleRequerido(), this.getEnergiaRequerida(), this.getDesgasteGenerado());
	}

	@Override
	protected ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	}
}