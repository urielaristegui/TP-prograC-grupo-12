package modelo.asistenteDeComandos;

import modelo.nave.Nave;
import modelo.bitacora.Bitacora;

import java.util.List;
import java.util.stream.Collectors;

public class AsistenteDeComandos {
	private Nave nave;
	private Bitacora bitacora;

	public AsistenteDeComandos(Nave nave, Bitacora bitacora) {
		setNave(nave);
		setBitacora(bitacora);
	}

	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}

	public void setNave(Nave nave) {
		this.nave = nave;
	}

	public boolean tieneRecursosDisponibles(int combustibleRequerido, int energiaRequerida) {
		return nave.getCombustible() >= combustibleRequerido
			&& nave.getEnergia() >= energiaRequerida;
	}

	public void ordenarConsumo(int combustible, int energia, int desgaste) {
		this.nave.consumirRecursos(combustible, energia, desgaste);
	}

	// Punto único para que las misiones dejen constancia de eventos,
	// en vez de imprimir por consola.
	public void registrarEvento(String evento) {
		bitacora.registrar(evento);
	}

	public void preparaSalto() {
		nave.prepararSalto();
	}

	public void salta() {
		nave.saltar();
	}

	// Snapshot de texto del estado de la nave, para InformeMision.
	public String snapshotEstadoNave() {
		return "Combustible: " + nave.getCombustible()
				+ ", Energía: " + nave.getEnergia()
				+ ", Desgaste: " + nave.getDesgaste()
				+ (nave.requiereMantenimiento() ? " (requiere mantenimiento)" : "");
	}
	// Filtra los eventos de la Bitácora que correspondan a una misión,
	// buscando el prefijo (nombre de clase) que Mision usa al registrar.
	// LIMITACIÓN: no distingue entre dos instancias del mismo tipo de misión.
	public List<String> eventosDe(String prefijo) {
		return bitacora.getRegistro().stream()
				.filter(evento -> evento.contains(prefijo))
				.collect(Collectors.toList());
	}
}