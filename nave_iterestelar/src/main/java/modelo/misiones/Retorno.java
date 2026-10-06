package modelo.misiones;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.misiones.Mision;
import modelo.misiones.ResultadoMision;

public class Retorno extends Mision{
	private int energiaRequerida;
	private int combustibleRequerido;
	private int desgaste;
	public Retorno (AsistenteDeComandos AC, int combustibleRequerido, int energiaRequerida, int desgaste ){
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
	public void ejecutar() {
		AC.ordenarConsumo(this.getCombustibleRequerido(), this.getEnergiaRequerida(), this.getDesgasteGenerado());
	}
	@Override
	public ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	} 
}
