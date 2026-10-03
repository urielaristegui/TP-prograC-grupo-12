package modelo.nave;

public final class NaveFactory {

    private NaveFactory() {
        // No crear objetos de esta clase.
    }

    public static Nave crearNave(TipoNave tipo, int id) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de nave no puede ser null");
        }

        return switch (tipo) {
            case EXPLORADORA -> new NaveExploradora(id);
            case CARGUERO -> new NaveCarguero(id);
            case COMBATE -> new NaveCombate(id);
        };
    }
}