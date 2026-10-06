package modelo.misiones;

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

	@Override
	protected int getCombustibleRequerido() { return combustibleRequerido; }

	@Override
	protected int getEnergiaRequerida() { return energiaRequerida; }

	@Override
	protected int getDesgasteGenerado() { return desgaste; }

	@Override
	protected void ejecutar() {
		AC.registrarEvento("M1 Intercepción: aproximando al objetivo");
		AC.ordenarConsumo(getCombustibleRequerido(), getEnergiaRequerida(), getDesgasteGenerado());
	}

	@Override
	protected ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	}
}
