package modelo.asistenteDeComandos;

import modelo.nave.Nave;
import modelo.bitacora.Bitacora;

public class AsistenteDeComandos {
	private Nave nave;
	private Bitacora bitacora;

	public AsistenteDeComandos(Nave nave, Bitacora bitacora) {
		setNave(nave);
		setBitacora(bitacora);
	}
	public void setBitacora(Bitacora bitacora) {
		this.bitacora=bitacora;
	}
	public void setNave(Nave nave) {
		this.nave=nave;
	}
	
	public boolean tieneRecursosDisponibles(int combustibleRequerido, int energiaRequerida) {
	    return nave.getCombustible() >= combustibleRequerido
	        && nave.getEnergia() >= energiaRequerida;
	}

	public void ordenarConsumo(int combustible, int energia, int desgaste) {
		this.nave.consumirRecursos(combustible, energia, desgaste);
	}
 
	// NUEVO: punto único para que las misiones dejen constancia de eventos,
	// en vez de imprimir por consola. Ajustá el nombre del método de Bitacora
	// si en tu clase se llama distinto a "registrar".
	public void registrarEvento(String evento) {
		bitacora.registrar(evento);
	}
	public void preparaSalto() {
		nave.prepararSalto();
	}
 
	public void salta() {
		nave.saltar();
	}

}
