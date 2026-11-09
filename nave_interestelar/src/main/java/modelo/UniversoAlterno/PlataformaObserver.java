package modelo.UniversoAlterno;

public interface PlataformaObserver {
    /**
     * Notifica un evento ocurrido en la plataforma de carga.
     * @param evento Mensaje descriptivo del suceso.
     * @param bateriasActuales Cantidad actual de baterías almacenadas.
     * @param capacidadMaxima Capacidad total de la plataforma.
     */
    void onPlataformaCambio(String evento, int bateriasActuales, int capacidadMaxima);
}