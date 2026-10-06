package modelo.misiones;


public class Retorno extends Mision{
	private int energiaRequerida;
	private int combustibleRequerido;
	private int desgaste;
	public Retorno(AsistenteDeComandos AC, int combustibleRequerido, int energiaRequerida, int desgaste ){
	    super(AC);
	    this.combustibleRequerido = combustibleRequerido;
	    this.energiaRequerida = energiaRequerida;
	    this.desgaste=desgaste;
	}
	@Override
	protected int getCombustibleRequerido() { return combustibleRequerido; }

	@Override
	protected int getEnergiaRequerida() { return energiaRequerida; }
	protected int getDesgasteGenerado() { return desgaste; }

	@Override
	protected void ejecutar() {
		AC.ordenarConsumo(this.getCombustibleRequerido(), this.getEnergiaRequerida(), this.getDesgasteGenerado());
	}
	@Override

	protected ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	} 
}
