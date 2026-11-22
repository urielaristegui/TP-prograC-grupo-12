
package Pruebas;

import java.util.ArrayList;

import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.misiones.InformeMision;
import modelo.misiones.Intercepcion;
import modelo.misiones.Mision;
import modelo.misiones.Recoleccion;
import modelo.misiones.ResultadoMision;
import modelo.misiones.Retorno;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.sensores.ReglasNavegacion;
import modelo.sensores.Sensores;

/**
 * Verifica M-01, M-02, M-03 y el rechazo por
 * recursos insuficientes.
 *
 * Cada escenario dispone de una nave y un
 * tablero propios para evitar dependencias
 * entre las pruebas.
 */
public final class PruebaMisiones {

    private PruebaMisiones() {
    }

    public static void main(String[] args) {

        System.out.println(
                "=== PRUEBAS AUTOMATICAS DE MISIONES ==="
        );

        probarIntercepcion();
        probarRecoleccion();
        probarRetorno();
        probarRecursosInsuficientes();

        System.out.println(
                "\n[OK] Todas las pruebas de misiones pasaron"
        );
    }

    /**
     * M-01: verifica la entrega de recursos
     * a una nave aliada.
     */
    private static void probarIntercepcion() {

        Contexto c = crearContexto(1001);

        Intercepcion mision =
                new Intercepcion(c.asistente, 10, 10);

        Nave aliada = mision.getNaveAliada();

        int combustibleAliada =
                aliada.getCombustible();

        int energiaAliada =
                aliada.getEnergia();

        int combustiblePropio =
                c.nave.getCombustible();

        int energiaPropia =
                c.nave.getEnergia();

        exigir(
                mision.director(),
                "M-01 debe superar la preparacion"
        );

        verificarInforme(
                mision,
                "Intercepcion",
                ResultadoMision.EXITOSA
        );

        exigir(
                c.asistente.posicionNave()
                        .equals(aliada.getPosicion()),
                "M-01: la nave no llego a la aliada"
        );

        exigir(
                aliada.getCombustible()
                        == combustibleAliada + 10,
                "M-01: la aliada no recibio "
                        + "10 de combustible"
        );

        exigir(
                aliada.getEnergia()
                        == energiaAliada + 10,
                "M-01: la aliada no recibio "
                        + "10 de energia"
        );

        exigir(
                c.nave.getCombustible()
                        <= combustiblePropio - 14,
                "M-01: no se descontaron el "
                        + "despegue y la transferencia "
                        + "de combustible"
        );

        exigir(
                c.nave.getEnergia()
                        <= energiaPropia - 15,
                "M-01: no se descontaron el "
                        + "despegue y la transferencia "
                        + "de energia"
        );

        System.out.println(
                "[OK] M-01: llegada, transferencia "
                        + "e informe exitoso"
        );
    }

    /**
     * M-02: comprueba la recompensa de gemas.
     */
    private static void probarRecoleccion() {

        Contexto c = crearContexto(1002);

        int gemasAntes = c.nave.getGemas();

        Recoleccion mision =
                new Recoleccion(c.asistente, 50);

        exigir(
                mision.director(),
                "M-02 debe superar la preparacion"
        );

        verificarInforme(
                mision,
                "Recoleccion",
                ResultadoMision.EXITOSA
        );

        exigir(
                c.nave.getGemas() == gemasAntes + 50,
                "M-02: no se agregaron "
                        + "exactamente 50 gemas"
        );

        exigir(
                mision.getInforme()
                        .getRecursosConsumidos()
                        .contains("Gemas +50"),
                "M-02: el balance no incluye "
                        + "la recompensa de gemas"
        );

        System.out.println(
                "[OK] M-02: 50 gemas incorporadas "
                        + "e informe exitoso"
        );
    }

    /**
     * M-03: verifica regreso a la base,
     * recarga total y mantenimiento.
     */
    private static void probarRetorno() {

        Contexto c = crearContexto(1003);

        Posicion base =
                c.asistente.posicionBase();

        // Comenzar a una casilla de la base.
        Posicion fueraDeBase = new Posicion(
                (base.getX() + 1) % 7,
                base.getY()
        );

        c.tablero.moverNave(fueraDeBase);
        c.nave.setPosicion(fueraDeBase);

        // Simular recursos usados.
        c.asistente.ordenarConsumo(
                10, 20, 15
        );

        exigir(
                !c.asistente.posicionNave().equals(base),
                "M-03: debe comenzar fuera de la base"
        );

        Retorno mision =
                new Retorno(c.asistente);

        exigir(
                mision.director(),
                "M-03 debe superar la preparacion"
        );

        verificarInforme(
                mision,
                "Retorno",
                ResultadoMision.EXITOSA
        );

        exigir(
                c.asistente.posicionNave().equals(base),
                "M-03: no regreso a la base"
        );

        exigir(
                c.nave.getCombustible() == 100,
                "M-03: combustible distinto de 100"
        );

        exigir(
                c.nave.getEnergia() == 100,
                "M-03: energia distinta de 100"
        );

        exigir(
                c.nave.getDesgaste() == 0,
                "M-03: desgaste distinto de 0"
        );

        System.out.println(
                "[OK] M-03: retorno, recarga total "
                        + "y mantenimiento"
        );
    }

    /**
     * Comprueba que una preparacion rechazada
     * no modifique los recursos ni la posicion.
     */
    private static void probarRecursosInsuficientes() {

        Contexto c = crearContexto(1004);

        // Exploradora: 60 de combustible inicial.
        // Dejamos 3; M-01 exige 4 para despegar.
        c.asistente.ordenarConsumo(
                57, 0, 0
        );

        int combustibleAntes =
                c.nave.getCombustible();

        int energiaAntes =
                c.nave.getEnergia();

        int desgasteAntes =
                c.nave.getDesgaste();

        int gemasAntes =
                c.nave.getGemas();

        Posicion posicionAntes =
                c.asistente.posicionNave();

        Intercepcion mision =
                new Intercepcion(c.asistente, 0, 0);

        exigir(
                !mision.director(),
                "Debe rechazar combustible insuficiente"
        );

        verificarInforme(
                mision,
                "Intercepcion",
                ResultadoMision.FALLIDA
        );

        exigir(
                c.nave.getCombustible()
                        == combustibleAntes,
                "Fallo: modifico combustible"
        );

        exigir(
                c.nave.getEnergia() == energiaAntes,
                "Fallo: modifico energia"
        );

        exigir(
                c.nave.getDesgaste() == desgasteAntes,
                "Fallo: modifico desgaste"
        );

        exigir(
                c.nave.getGemas() == gemasAntes,
                "Fallo: modifico gemas"
        );

        exigir(
                c.asistente.posicionNave()
                        .equals(posicionAntes),
                "Fallo: movio la nave"
        );

        exigir(
                mision.getInforme()
                        .getObservaciones()
                        .contains("recursos insuficientes"),
                "El informe no explica el motivo"
        );

        System.out.println(
                "[OK] Preparacion rechazada "
                        + "sin efectos parciales"
        );
    }

    /**
     * Verifica que el informe tenga los campos
     * requeridos y el resultado esperado.
     */
    private static void verificarInforme(
            Mision mision,
            String nombre,
            ResultadoMision esperado) {

        InformeMision informe =
                mision.getInforme();

        exigir(
                informe != null,
                nombre + ": no genero informe"
        );

        exigir(
                nombre.equals(informe.getMisionEjecutada()),
                nombre + ": nombre incorrecto"
        );

        exigir(
                informe.getResultado() == esperado,
                nombre + ": resultado incorrecto: "
                        + informe.getResultado()
        );

        exigir(
                informe.getAccionesPrincipales() != null
                        && !informe.getAccionesPrincipales().isEmpty(),
                nombre + ": no registro acciones"
        );

        String balance =
                informe.getRecursosConsumidos();

        exigir(
                balance != null
                        && balance.contains("Combustible")
                        && balance.contains("Energia")
                        && balance.contains("Desgaste")
                        && balance.contains("Gemas"),
                nombre + ": balance incompleto"
        );

        exigir(
                informe.getEstadoNave() != null
                        && !informe.getEstadoNave().isBlank(),
                nombre + ": estado final ausente"
        );

        exigir(
                informe.getObservaciones() != null
                        && !informe.getObservaciones().isBlank(),
                nombre + ": observaciones vacias"
        );

        if (esperado == ResultadoMision.EXITOSA) {

            exigir(
                    informe.getObservaciones()
                            .contains("Cierre completado"),
                    nombre + ": cierre no reflejado "
                            + "en el informe"
            );

            exigir(
                    informe.getAccionesPrincipales()
                            .stream()
                            .anyMatch(a ->
                                    a.contains("cierre aplicado")
                            ),
                    nombre + ": no registro "
                            + "la accion de cierre"
            );
        }
    }

    /**
     * Crea un contexto independiente para cada prueba.
     * Usa un tablero sin obstaculos para comprobar
     * la logica de las misiones de forma controlada.
     */
    private static Contexto crearContexto(int id) {

        try {

            Nave nave = NaveFactory.crearNave(
                    TipoNave.EXPLORADORA,
                    id
            );

            Tablero tablero = new Tablero(
                    7,
                    7,
                    new ArrayList<ElementoDelUniverso>()
            );

            AsistenteDeComandos asistente =
                    new AsistenteDeComandos(
                            nave,
                            Bitacora.getInstance(),
                            tablero
                    );

            asistente.setReglasNavegacion(
                    new ReglasNavegacion(
                            new Sensores(tablero)
                    )
            );

            return new Contexto(
                    nave,
                    tablero,
                    asistente
            );

        } catch (
                DimensionesDeTableroInconsistentesException
                | CantidadDeElementosSuperaCasillaException e) {

            throw new IllegalStateException(
                    "No se pudo preparar el tablero de pruebas",
                    e
            );
        }
    }

    /**
     * Falla la prueba cuando una condicion
     * no se cumple.
     */
    private static void exigir(
            boolean condicion,
            String mensaje) {

        if (!condicion) {
            throw new AssertionError(
                    "ERROR: " + mensaje
            );
        }
    }

    /**
     * Agrupa los objetos propios de cada escenario.
     */
    private static final class Contexto {

        final Nave nave;
        final Tablero tablero;
        final AsistenteDeComandos asistente;

        Contexto(
                Nave nave,
                Tablero tablero,
                AsistenteDeComandos asistente) {

            this.nave = nave;
            this.tablero = tablero;
            this.asistente = asistente;
        }
    }
}
