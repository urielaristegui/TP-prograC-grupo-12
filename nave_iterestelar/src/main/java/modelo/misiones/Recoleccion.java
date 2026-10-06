package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.misiones.ResultadoMision;

public class Recoleccion extends Mision{ 
	private int energiaRequerida;
	private int combustibleRequerido;
	private int desgaste;
	public Recoleccion(AsistenteDeComandos AC, int combustibleRequerido, int energiaRequerida, int desgaste ){
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
		AC.registrarEvento("M2 Recolección: recolectando muestra en el punto de interés");
		AC.ordenarConsumo(getCombustibleRequerido(), getEnergiaRequerida(), getDesgasteGenerado());
	}
	
	protected ResultadoMision evaluar() {
		return ResultadoMision.EXITOSA;
	} 
}
