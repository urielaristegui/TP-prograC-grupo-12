
package Pruebas;

import modelo.UniversoAlterno.PlataformaDeCarga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Prueba automatica del monitor Productor-Consumidor.
 *
 * Verifica:
 * - Capacidad maxima de la plataforma.
 * - Depositos y retiros de baterias.
 * - Bloqueo de productores con plataforma llena.
 * - Bloqueo de consumidores con plataforma vacia.
 * - Desbloqueo mediante notifyAll().
 * - Operaciones concurrentes de multiples hilos.
 *
 * Utiliza directamente las operaciones del monitor
 * para evitar los tiempos aleatorios de simulacion.
 */
public final class PruebaMonitores {

    private PruebaMonitores() {
    }

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "=== PRUEBA DE MONITORES ==="
        );

        probarOperacionesBasicas();
        probarProductorBloqueado();
        probarConsumidorBloqueado();
        probarMultiplesHilos();

        System.out.println(
                "\n[OK] TODAS LAS PRUEBAS DE MONITORES PASARON"
        );
    }

    /**
     * Comprueba depositos, retiros y capacidad.
     */
    private static void probarOperacionesBasicas()
            throws InterruptedException {

        PlataformaDeCarga plataforma =
                new PlataformaDeCarga(3);

        exigir(
                plataforma.getCapacidadMaxima() == 3,
                "Capacidad maxima incorrecta"
        );

        exigir(
                plataforma.getBateriasDisponibles() == 0,
                "La plataforma debe comenzar vacia"
        );

        plataforma.depositarBateria("G1");
        plataforma.depositarBateria("G1");
        plataforma.depositarBateria("G1");

        exigir(
                plataforma.getBateriasDisponibles() == 3,
                "No se cargaron las tres baterias"
        );

        plataforma.retirarBateria("N1");
        plataforma.retirarBateria("N1");
        plataforma.retirarBateria("N1");

        exigir(
                plataforma.getBateriasDisponibles() == 0,
                "No se retiraron las tres baterias"
        );

        System.out.println(
                "[OK] Depositos, retiros y capacidad"
        );
    }

    /**
     * Comprueba que un productor espere cuando
     * la plataforma esta llena y continue cuando
     * un consumidor libera espacio.
     */
    private static void probarProductorBloqueado()
            throws Exception {

        PlataformaDeCarga plataforma =
                new PlataformaDeCarga(1);

        // Llenar la plataforma.
        plataforma.depositarBateria("G-inicial");

        CountDownLatch avisoEspera =
                new CountDownLatch(1);

        plataforma.registrarObservador(
                (evento, actual, capacidad) -> {

                    if (evento.contains(
                            "esperando a que se libere espacio"
                    )) {
                        avisoEspera.countDown();
                    }
                }
        );

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        try {

            Future<?> productor = executor.submit(
                    () -> {

                        plataforma.depositarBateria(
                                "G-bloqueada"
                        );

                        return null;
                    }
            );

            // Esperar a que el productor informe
            // que la plataforma esta llena.
            exigir(
                    avisoEspera.await(
                            5, TimeUnit.SECONDS
                    ),
                    "El productor no notifico "
                            + "que estaba esperando"
            );

            // No debe completar el deposito.
            exigir(
                    !productor.isDone(),
                    "El productor deposito estando "
                            + "llena la plataforma"
            );

            exigir(
                    plataforma.getBateriasDisponibles() == 1,
                    "Se supero la capacidad mientras "
                            + "el productor esperaba"
            );

            // Liberar espacio.
            plataforma.retirarBateria("N-libera");

            // El productor debe desbloquearse.
            productor.get(5, TimeUnit.SECONDS);

            exigir(
                    plataforma.getBateriasDisponibles() == 1,
                    "El productor no deposito al "
                            + "liberarse espacio"
            );

            System.out.println(
                    "[OK] Productor espera y se desbloquea"
            );

        } finally {

            executor.shutdownNow();
        }
    }

    /**
     * Comprueba que un consumidor espere cuando
     * la plataforma esta vacia y continue cuando
     * un productor deposita una bateria.
     */
    private static void probarConsumidorBloqueado()
            throws Exception {

        PlataformaDeCarga plataforma =
                new PlataformaDeCarga(1);

        CountDownLatch avisoEspera =
                new CountDownLatch(1);

        plataforma.registrarObservador(
                (evento, actual, capacidad) -> {

                    if (evento.contains(
                            "esperando por falta de baterias"
                    ) || evento.contains(
                            "esperando por falta de baterías"
                    )) {
                        avisoEspera.countDown();
                    }
                }
        );

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        try {

            Future<?> consumidor = executor.submit(
                    () -> {

                        plataforma.retirarBateria(
                                "N-bloqueada"
                        );

                        return null;
                    }
            );

            // Esperar a que detecte la plataforma vacia.
            exigir(
                    avisoEspera.await(
                            5, TimeUnit.SECONDS
                    ),
                    "El consumidor no notifico "
                            + "que estaba esperando"
            );

            // No puede retirar una bateria inexistente.
            exigir(
                    !consumidor.isDone(),
                    "El consumidor retiro sin "
                            + "baterias disponibles"
            );

            exigir(
                    plataforma.getBateriasDisponibles() == 0,
                    "La plataforma vacia cambio "
                            + "inesperadamente"
            );

            // Producir una bateria.
            plataforma.depositarBateria("G-libera");

            // El consumidor debe desbloquearse.
            consumidor.get(5, TimeUnit.SECONDS);

            exigir(
                    plataforma.getBateriasDisponibles() == 0,
                    "El consumidor no retiro "
                            + "la bateria nueva"
            );

            System.out.println(
                    "[OK] Consumidor espera y se desbloquea"
            );

        } finally {

            executor.shutdownNow();
        }
    }

    /**
     * Comprueba el funcionamiento concurrente
     * de cuatro productores y cuatro consumidores.
     *
     * Se realizan:
     * - 400 depositos.
     * - 400 retiros.
     *
     * La plataforma tiene capacidad maxima de 5.
     */
    private static void probarMultiplesHilos()
            throws Exception {

        final int productores = 4;
        final int consumidores = 4;
        final int repeticiones = 100;
        final int capacidad = 5;

        PlataformaDeCarga plataforma =
                new PlataformaDeCarga(capacidad);

        AtomicInteger depositos =
                new AtomicInteger();

        AtomicInteger retiros =
                new AtomicInteger();

        AtomicReference<String> errorObserver =
                new AtomicReference<>();

        // Observer para verificar todas las operaciones.
        plataforma.registrarObservador(
                (evento, actual, maximo) -> {

                    if (actual < 0 || actual > maximo) {

                        errorObserver.compareAndSet(
                                null,
                                "Capacidad fuera de rango: " + actual
                        );
                    }

                    if (evento.contains("depositada por")) {
                        depositos.incrementAndGet();
                    }

                    if (evento.contains(
                            "retirada y utilizada por"
                    )) {
                        retiros.incrementAndGet();
                    }
                }
        );

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        productores + consumidores
                );

        CountDownLatch inicio =
                new CountDownLatch(1);

        List<Future<?>> tareas =
                new ArrayList<>();

        try {

            // ===================================
            // PRODUCTORES
            // ===================================

            for (int p = 0; p < productores; p++) {

                final int id = p;

                tareas.add(
                        executor.submit(() -> {

                            inicio.await();

                            for (int i = 0;
                                 i < repeticiones;
                                 i++) {

                                plataforma.depositarBateria(
                                        "G" + id
                                );
                            }

                            return null;
                        })
                );
            }

            // ===================================
            // CONSUMIDORES
            // ===================================

            for (int c = 0; c < consumidores; c++) {

                final int id = c;

                tareas.add(
                        executor.submit(() -> {

                            inicio.await();

                            for (int i = 0;
                                 i < repeticiones;
                                 i++) {

                                plataforma.retirarBateria(
                                        "N" + id
                                );
                            }

                            return null;
                        })
                );
            }

            // Todos los hilos comienzan.
            inicio.countDown();

            // Esperar a que terminen.
            for (Future<?> tarea : tareas) {

                tarea.get(
                        15, TimeUnit.SECONDS
                );
            }

            // ===================================
            // VERIFICACIONES FINALES
            // ===================================

            exigir(
                    errorObserver.get() == null,
                    "El observador detecto un error: "
                            + errorObserver.get()
            );

            exigir(
                    depositos.get() == 400,
                    "Depositos registrados: "
                            + depositos.get()
                            + "/400"
            );

            exigir(
                    retiros.get() == 400,
                    "Retiros registrados: "
                            + retiros.get()
                            + "/400"
            );

            exigir(
                    plataforma.getBateriasDisponibles() == 0,
                    "El balance final debe ser cero"
            );

            System.out.println(
                    "[OK] 400 depositos, 400 retiros, 8 hilos"
            );

            System.out.println(
                    "[OK] Capacidad respetada "
                            + "y balance final: 0"
            );

        } finally {

            inicio.countDown();
            executor.shutdownNow();
        }
    }

    /**
     * Falla la prueba si una condicion no se cumple.
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
