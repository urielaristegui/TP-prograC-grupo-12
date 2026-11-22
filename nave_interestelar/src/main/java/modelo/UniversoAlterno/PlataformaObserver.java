package modelo.UniversoAlterno;

public interface PlataformaObserver {
    /**
     * notifica un evento ocurrido en la plataforma de carga.<br>
     * <b>pre:</b> evento != null, bateriasActuales >= 0 y capacidadMaxima > 0.<br>
     * <b>post:</b> se informa al observador sobre el evento y el estado actual de baterias en la plataforma.<br>
     *
     * @param evento mensaje descriptivo del suceso. evento != null.
     * @param bateriasActuales cantidad actual de baterias almacenadas. bateriasActuales >= 0.
     * @param capacidadMaxima capacidad total de la plataforma. capacidadMaxima > 0.
     */
    void onPlataformaCambio(String evento, int bateriasActuales, int capacidadMaxima);
}