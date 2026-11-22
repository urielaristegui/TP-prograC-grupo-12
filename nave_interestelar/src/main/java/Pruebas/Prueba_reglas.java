
package Pruebas;

import java.util.ArrayList;
import java.util.List;

import modelo.Tripulacion.Alferez;
import modelo.Tripulacion.Capitan;
import modelo.Tripulacion.Consejero;
import modelo.Tripulacion.Identidad;
import modelo.Tripulacion.Teniente;
import modelo.asistenteDeComandos.AsistenteDeComandos;
import modelo.bitacora.Bitacora;
import modelo.destinoytablero.CantidadDeElementosSuperaCasillaException;
import modelo.destinoytablero.DimensionesDeTableroInconsistentesException;
import modelo.destinoytablero.Posicion;
import modelo.destinoytablero.Tablero;
import modelo.elementos.ElementoDelUniverso;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.sensores.ReglasNavegacion;
import modelo.sensores.Sensores;

/**
 * Verifica la integracion de Sensores, ReglasNavegacion,
 * AsistenteDeComandos, Tablero y Bitacora.
 *
 * Utiliza un tablero vacio para que los movimientos
 * sean deterministas y no dependan del azar.
 */
public final class Prueba_reglas {

    private Prueba_reglas() {
    }

    public static void main(String[] args) {

        System.out.println(
                "=== PRUEBA AUTOMATICA: SENSORES Y NAVEGACION ==="
        );

        try {

            // =====================================
            // 1. CREAR ESCENARIO CONTROLADO
            // =====================================

            Tablero tablero = new Tablero(
                    10,
                    10,
                    new ArrayList<ElementoDelUniverso>()
            );

            Nave nave = NaveFactory.crearNave(
                    TipoNave.EXPLORADORA,
                    7002
            );

            Bitacora bitacora = Bitacora.getInstance();

            int inicioBitacora =
                    bitacora.getRegistro().size();

            AsistenteDeComandos asistente =
                    new AsistenteDeComandos(
                            nave,
                            bitacora,
                            tablero
                    );

            // Tripulacion minima obligatoria.

            nave.agregarTripulante(
                    Capitan.getInstancia(
                            new Identidad("Capitan Navegacion"),
                            10,
                            asistente
                    )
            );

            nave.agregarTripulante(
                    new Teniente(
                            new Identidad("Data"), 9
                    )
            );

            nave.agregarTripulante(
                    new Consejero(
                            new Identidad("Troi"), 7
                    )
            );

            nave.agregarTripulante(
                    new Alferez(
                            new Identidad("Crusher"), 5
                    )
            );

            nave.agregarTripulante(
                    new Alferez(
                            new Identidad("La Forge"), 5
                    )
            );

            exigir(
                    nave.tieneTripulacionValida(),
                    "La tripulacion debe ser valida"
            );

            Sensores sensores = new Sensores(tablero);

            ReglasNavegacion reglas =
                    new ReglasNavegacion(sensores);

            asistente.setReglasNavegacion(reglas);

            Posicion inicio = new Posicion(5, 5);
            Posicion destino = new Posicion(5, 8);

            exigir(
                    nave.getPosicion().equals(inicio),
                    "La nave debe comenzar en (5,5)"
            );

            exigir(
                    tablero.getPosicionNave().equals(inicio),
                    "Tablero y nave deben coincidir al inicio"
            );

            int combustibleInicial =
                    nave.getCombustible();

            int energiaInicial =
                    nave.getEnergia();

            int desgasteInicial =
                    nave.getDesgaste();

            // =====================================
            // 2. COMPROBAR LOS RADARES
            // =====================================

            ElementoDelUniverso[][] corto =
                    sensores.escanearCortoAlcance();

            ElementoDelUniverso[][] largo =
                    sensores.escanearLargoAlcance();

            verificarDimensiones(
                    corto, 3, "corto alcance"
            );

            verificarDimensiones(
                    largo, 5, "largo alcance"
            );

            exigir(
                    corto[1][1] == null,
                    "El centro del radar corto debe estar vacio"
            );

            exigir(
                    largo[2][2] == null,
                    "El centro del radar largo debe estar vacio"
            );

            exigir(
                    nave.getPosicion().equals(inicio),
                    "Escanear no debe mover la nave"
            );

            exigir(
                    nave.getCombustible() == combustibleInicial
                            && nave.getEnergia() == energiaInicial
                            && nave.getDesgaste() == desgasteInicial,
                    "Los escaneos no deben consumir recursos"
            );

            System.out.println(
                    "[OK] Sensores 3x3 y 5x5 "
                            + "sin efectos secundarios"
            );

            // =====================================
            // 3. PLANIFICACION CON RADAR
            // =====================================

            List<Posicion> ruta =
                    asistente.planificarRutaConRadar(
                            destino
                    );

            exigir(
                    ruta.size() == 3,
                    "La ruta libre debe tener tres pasos"
            );

            exigir(
                    ruta.get(0).equals(
                            new Posicion(5, 6)
                    )
                            && ruta.get(1).equals(
                            new Posicion(5, 7)
                    )
                            && ruta.get(2).equals(destino),
                    "La ruta calculada es incorrecta"
            );

            exigir(
                    nave.getPosicion().equals(inicio),
                    "Planificar no debe mover la nave"
            );

            exigir(
                    nave.getCombustible() == combustibleInicial,
                    "Planificar no debe consumir combustible"
            );

            System.out.println(
                    "[OK] Ruta de tres pasos planificada "
                            + "con radar"
            );

            // =====================================
            // 4. NAVEGACION Y CONSUMOS
            // =====================================

            for (int paso = 1; paso <= 3; paso++) {

                asistente.avanzarHacia(destino);

                Posicion esperada = new Posicion(
                        5,
                        5 + paso
                );

                exigir(
                        nave.getPosicion().equals(esperada),
                        "Posicion incorrecta en paso " + paso
                );

                exigir(
                        tablero.getPosicionNave().equals(esperada),
                        "Nave y tablero desincronizados "
                                + "en paso " + paso
                );

                exigir(
                        nave.getCombustible()
                                == combustibleInicial - 2 * paso,
                        "Consumo incorrecto de combustible "
                                + "en paso " + paso
                );

                exigir(
                        nave.getEnergia() == energiaInicial,
                        "El salto normal no debe consumir energia"
                );

                exigir(
                        nave.getDesgaste() == desgasteInicial,
                        "El salto normal no debe generar desgaste"
                );
            }

            System.out.println(
                    "[OK] Tres movimientos, posiciones "
                            + "y consumos exactos"
            );

            // =====================================
            // 5. NO AVANZAR SI YA LLEGO
            // =====================================

            asistente.avanzarHacia(destino);

            exigir(
                    nave.getPosicion().equals(destino),
                    "La nave no debe moverse si ya llego"
            );

            exigir(
                    nave.getCombustible()
                            == combustibleInicial - 6,
                    "No debe consumir combustible adicional"
            );

            System.out.println(
                    "[OK] Permanecer en destino "
                            + "no consume recursos"
            );

            // =====================================
            // 6. SENSORES EN LOS BORDES
            // =====================================

            Posicion esquina = new Posicion(0, 0);

            // Reubicacion manual para preparar
            // un escenario de prueba de sensores.

            tablero.moverNave(esquina);
            nave.setPosicion(esquina);

            ElementoDelUniverso[][] cortoBorde =
                    sensores.escanearCortoAlcance();

            ElementoDelUniverso[][] largoBorde =
                    sensores.escanearLargoAlcance();

            verificarDimensiones(
                    cortoBorde, 3, "corto en borde"
            );

            verificarDimensiones(
                    largoBorde, 5, "largo en borde"
            );

            exigir(
                    cortoBorde[0][0] == null
                            && largoBorde[0][0] == null,
                    "Fuera del tablero el radar debe devolver null"
            );

            System.out.println(
                    "[OK] Ambos sensores funcionan "
                            + "en los bordes"
            );

            // =====================================
            // 7. EVENTOS EN BITACORA
            // =====================================

            List<String> eventos =
                    bitacora.getRegistro();

            List<String> eventosDeEstaPrueba =
                    eventos.subList(
                            inicioBitacora,
                            eventos.size()
                    );

            exigir(
                    eventosDeEstaPrueba.stream().anyMatch(
                            e -> e.contains(
                                    "Sensor: escaneo de corto alcance"
                            )
                    ),
                    "Falta el evento del radar corto"
            );

            exigir(
                    eventosDeEstaPrueba.stream().anyMatch(
                            e -> e.contains(
                                    "Sensor: escaneo de largo alcance"
                            )
                    ),
                    "Falta el evento del radar largo"
            );

            exigir(
                    eventosDeEstaPrueba.stream().anyMatch(
                            e -> e.contains(
                                    "ruta inicial planificada"
                            )
                    ),
                    "Falta el evento de planificacion"
            );

            System.out.println(
                    "[OK] Eventos del radar "
                            + "y ruta en Bitacora"
            );

            // =====================================
            // RESULTADO FINAL
            // =====================================

            System.out.println(
                    "\n[OK] TODAS LAS PRUEBAS "
                            + "DE SENSORES Y NAVEGACION PASARON"
            );

        } catch (
                DimensionesDeTableroInconsistentesException
                | CantidadDeElementosSuperaCasillaException e
        ) {

            throw new IllegalStateException(
                    "No se pudo preparar el escenario",
                    e
            );
        }
    }

    /**
     * Verifica las dimensiones de un radar.
     *
     * Pre: ninguna.
     * Post: falla si la matriz no tiene
     *       las dimensiones esperadas.
     */
    private static void verificarDimensiones(
            ElementoDelUniverso[][] matriz,
            int tamanio,
            String descripcion) {

        exigir(
                matriz != null
                        && matriz.length == tamanio,
                "Filas incorrectas en radar " + descripcion
        );

        for (ElementoDelUniverso[] fila : matriz) {

            exigir(
                    fila != null
                            && fila.length == tamanio,
                    "Columnas incorrectas en radar "
                            + descripcion
            );
        }
    }

    /**
     * Falla la prueba si la condicion no se cumple.
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
}
