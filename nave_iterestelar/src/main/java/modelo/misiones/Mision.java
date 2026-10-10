package modelo.misiones;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.misiones.ResultadoMision;

public abstract class Mision {
	protected AsistenteDeComandos AC;
	protected ResultadoMision resultado;
	protected InformeMision informe;

	public Mision(AsistenteDeComandos AC) {
		this.AC=AC;
	}

	public final boolean director() {
	    if (preparar() == false) {
	    	AC.registrarEvento(getClass().getSimpleName() + ": preparación fallida, misión cancelada");
			return false; // resultado queda en null: no llegó a evaluarse
		}
 
		ejecutar();
		resultado = evaluar();
		AC.registrarEvento(getClass().getSimpleName() + ": evaluación -> " + resultado);
 
		cerrar();
		return true;
	}

	
	public abstract void ejecutar();
	

	public abstract ResultadoMision evaluar();
	
	
	// En Mision (clase base)
	public boolean preparar() {
	    return AC.tieneRecursosDisponibles(getCombustibleRequerido(), getEnergiaRequerida());
	}
	
	public void cerrar() {
	    if (resultado == ResultadoMision.EXITOSA) {
	        AC.preparaSalto();
	        AC.salta();
	    }
	    AC.registrarEvento(getClass().getSimpleName() + ": cerrada, resultado " + resultado);
	    String recursos = "Combustible: " + getCombustibleRequerido()
						+ ", Energía: " + getEnergiaRequerida()
						+ ", Desgaste: " + getDesgasteGenerado();

	    informe = new InformeMision.Builder()
	    		
		.misionEjecutada(prefijo)
		.resultadoFinal(resultado.toString())
		.recursosConsumidos(recursos)
		.estadoNave(AC.snapshotEstadoNave())
		.eventos(AC.eventosDe(prefijo))
		.build();
	}
	
	public InformeMision getInforme() {
		return informe;
	}
	
	protected abstract int getCombustibleRequerido();
	protected abstract int getEnergiaRequerida();
	protected abstract int getDesgasteGenerado();
	
}
