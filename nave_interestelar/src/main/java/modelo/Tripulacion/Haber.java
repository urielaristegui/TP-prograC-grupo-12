package modelo.Tripulacion;

/**
 * Interfaz haber principal para el diseño de los tripulantes con el patron decorator.
 */
public interface Haber{
    /**
     * Método getSueldo que será redefinido en los tripulantes y decoradores.
     * @return sueldo valido > 0
     */
    public double getSueldo();
}