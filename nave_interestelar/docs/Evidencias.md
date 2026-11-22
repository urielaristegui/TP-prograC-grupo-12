# Evidencias de pruebas — Sistema de Nave Interestelar

**Trabajo Práctico Grupal — Programación C — Grupo 12**  
**Partes 1 y 2 — revisión del 22/11/2026**

## 1. Objetivo y alcance

Este documento reúne las comprobaciones realizadas sobre la versión del proyecto incluida en `TP-prograC-grupo-12-main (14).zip`. El objetivo es demostrar el comportamiento de los módulos principales y permitir la reproducción de las pruebas antes de la entrega.

Se distingue entre **pruebas automáticas ejecutadas**, **demostraciones visuales disponibles** y **verificaciones manuales aún no certificadas**. Las salidas de versiones anteriores y los avisos de pendientes correspondientes a aquellas versiones se reemplazan por los resultados actuales.

## 2. Entorno y procedimiento de verificación

- **Lenguaje:** Java.
- **Entorno utilizado para esta auditoría:** OpenJDK 21, compilación con `javac --release 21 -encoding UTF-8`.
- **Archivos compilados:** 90 archivos `.java` de `nave_interestelar/src/main/java`.
- **Compilación:** correcta, sin errores.
- **Ejecución:** clases del paquete `Pruebas`, cada una mediante su método `main()`; sin JUnit.
- **Resultado global:** 9 de 9 pruebas automáticas principales finalizaron correctamente.

Para reproducirlas en IntelliJ IDEA: abrir el proyecto, configurar un JDK compatible, compilar y ejecutar el método `main()` de cada clase indicada en la tabla siguiente. Cada prueba utiliza comprobaciones que lanzan excepciones o errores si se incumplen sus condiciones; un texto `[OK]` por sí solo no reemplaza esas comprobaciones.

**Nota de entorno:** el `pom.xml` incluido en esta versión especifica compilación con Java 26. La verificación indicada arriba se realizó directamente con `javac` de Java 21, **no con Maven**. Para reproducir la compilación mediante Maven hay que utilizar un JDK compatible con `pom.xml` o actualizar esa configuración.

## 3. Resumen de pruebas automáticas

Todas las clases se encuentran en `nave_interestelar/src/main/java/Pruebas/`.

| Clase | Aspectos verificados | Resultado |
|---|---|---|
| `EscenarioC` | State; dos turnos de preparación; Warp con consumo de 5 de energía; desgaste de 3 durante el enfriamiento; regreso a salto normal | **APROBADA** |
| `EscenarioD` | Límites de combustible y energía; rechazo de consumos inválidos sin efectos parciales; mantenimiento | **APROBADA** |
| `PruebaHaberes` | Cuatro cargos; 12 combinaciones de subsidios planetarios; reemplazo de origen; rechazo de origen nulo | **APROBADA** |
| `PruebaUniverso` | Tablero 20×20; sincronización de posiciones; asteroides, estaciones, gemas y anomalías; límites; destrucción | **APROBADA** |
| `Prueba_reglas` | Sensores 3×3 y 5×5; planificación de ruta; movimientos y consumos exactos; bordes; bitácora | **APROBADA** |
| `PruebaMisiones` | M-01, M-02 y M-03; recursos, objetivos, operaciones de cierre e informes; rechazo por recursos insuficientes | **APROBADA** |
| `PruebaDoubleDispatch` | Matriz completa de 9 enfrentamientos; efectos de derrota; encuentros inválidos | **APROBADA** |
| `PruebaMonitores` | Capacidad del recurso compartido; productor/consumidor bloqueados y desbloqueados; 400 depósitos y 400 retiros | **APROBADA** |
| `PruebaPersistenciaDAO` | Registro, duplicados, búsqueda, autenticación, DTO, persistencia tras reabrir y cierre de sesión | **APROBADA** |

## 4. Evidencias por módulo

### 4.1. Motor Warp — `EscenarioC`

La prueba confirma que los dos primeros turnos preparan el salto sin consumir energía; en el tercero se ejecuta el Warp y se descuentan **5 unidades de energía**, sin desgaste inmediato; en el cuarto se procesa el **enfriamiento y se suman exactamente 3 puntos de desgaste**. A continuación, el motor vuelve a permitir un salto normal, que consume 2 unidades de combustible.

**Salida final registrada:**

```text
[OK] State, preparacion, Warp, enfriamiento y salto normal
```

### 4.2. Contratos y recursos — `EscenarioD`

Se verificaron el rechazo de cargas por encima de la capacidad, la ausencia de cambios parciales al intentar consumir recursos insuficientes y la operación de mantenimiento: con desgaste 80, el desgaste vuelve a 0 y el combustible y la energía permanecen en 55 y 75, respectivamente.

**Salida final registrada:**

```text
[OK] Contratos, atomicidad y mantenimiento verificados
```

### 4.3. Haberes y Decorator — `PruebaHaberes`

Se comprobaron los haberes de los cuatro cargos con los siguientes valores en el escenario preparado por la prueba:

| Cargo | Haber comprobado |
|---|---:|
| Capitán | 1400 PG |
| Consejero | 664 PG |
| Teniente | 424 PG |
| Alférez | 202 PG |

Se verificaron las **12 combinaciones** de los cuatro cargos con los tres orígenes: Terrícola (+20 PG), Marciano (+18 PG) y Vulcano (+30 PG). Además, se comprobó que `asignarOrigen()` **reemplaza** el subsidio previo en lugar de acumular orígenes incompatibles, y que se rechaza un origen nulo.

**Salida final registrada:**

```text
[OK] Cuatro cargos, doce subsidios y reemplazo de origen
=== FIN DE PRUEBA DE HABERES ===
```

Esta prueba demuestra el comportamiento de los cálculos y del Decorator por origen. No se usa como prueba de composición de decoradores de conceptos salariales diferentes; ese alcance debe contrastarse con el enunciado original de la Parte 1 si fuera necesario.

### 4.4. Universo e interacciones — `PruebaUniverso`

Se verificaron el tablero de 20×20, la posición inicial del jugador, la sincronización de las posiciones enemigas antes y después de un turno, los efectos de asteroides y anomalías, la recuperación de energía y combustible en estaciones, la recolección de gemas sin duplicación, un encuentro entre Exploradora y Carguero, los límites del tablero y la destrucción por asteroide fatal.

**Salida final registrada:**

```text
OK - Posicion enemiga sincronizada tras el turno
OK - No permite salir del tablero
OK - Asteroide fatal destruye la nave

=== TODAS LAS PRUEBAS PASARON ===
```

### 4.5. Sensores y navegación — `Prueba_reglas`

Sobre un escenario controlado se comprobaron las dimensiones de los radares (**3×3 y 5×5**), el escaneo sin consumo de recursos, la planificación de una ruta libre de tres pasos, tres movimientos con las posiciones y consumos esperados, la coincidencia entre posición de la nave y tablero, la ausencia de gasto adicional estando en el destino, las lecturas cerca de los bordes y el registro de eventos en la bitácora.

**Salida final registrada:**

```text
[OK] Sensores 3x3 y 5x5 sin efectos secundarios
[OK] Ruta de tres pasos planificada con radar
[OK] Tres movimientos, posiciones y consumos exactos
[OK] Permanecer en destino no consume recursos
[OK] Ambos sensores funcionan en los bordes
[OK] Eventos del radar y ruta en Bitacora

[OK] TODAS LAS PRUEBAS DE SENSORES Y NAVEGACION PASARON
```

### 4.6. Misiones y Template Method — `PruebaMisiones`

Las misiones se ejecutaron en **contextos independientes** para evitar que el gasto de una afecte a otra.

- **M-01 Intercepción:** llegada a la posición de la nave aliada; transferencia de 10 de combustible y 10 de energía; comprobación del informe exitoso.
- **M-02 Recolección:** incorporación de exactamente 50 gemas; comprobación del balance y del informe exitoso.
- **M-03 Retorno:** regreso a la base; combustible y energía en 100; desgaste en 0; comprobación del informe exitoso.
- **Preparación fallida:** se rechaza la misión por combustible insuficiente; no hay consumo ni movimiento parcial; se genera un informe de resultado fallido.

También se verifican datos relevantes de los informes: nombre y resultado, acciones principales, balance de recursos, estado final y observaciones.

**Salida final registrada:**

```text
[OK] M-01: llegada, transferencia e informe exitoso
[OK] M-02: 50 gemas incorporadas e informe exitoso
[OK] M-03: retorno, recarga total y mantenimiento
[OK] Preparacion rechazada sin efectos parciales

[OK] Todas las pruebas de misiones pasaron
```

La clase se ejecutó **3 veces correctamente** durante esta auditoría (una ejecución inicial y dos repeticiones).

### 4.7. Enfrentamientos — `PruebaDoubleDispatch`

Se verificaron todas las combinaciones de naves, desde la perspectiva de la nave propia:

| Nave propia / Rival | Exploradora | Carguero | Combate |
|---|---|---|---|
| Exploradora | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

También se comprobó que la derrota destruye la nave propia, que ganar o pactar conserva los recursos y la tripulación, y que se rechazan rivales nulos, el autoenfrentamiento y la participación de naves destruidas.

**Salida final registrada:**

```text
[OK] 9/9 enfrentamientos y validaciones correctas
```

### 4.8. Productor–Consumidor — `PruebaMonitores`

Se verificó directamente el monitor `PlataformaDeCarga`: capacidad máxima, depósitos y retiros, espera de un productor con la plataforma llena y su desbloqueo al retirar una batería, espera de un consumidor con la plataforma vacía y su desbloqueo al depositar una batería.

La prueba concurrente ejecutó **4 productores y 4 consumidores**, con 100 operaciones por hilo: **400 depósitos, 400 retiros**, respeto del rango de capacidad y balance final de 0 baterías.

**Salida final registrada:**

```text
[OK] Depositos, retiros y capacidad
[OK] Productor espera y se desbloquea
[OK] Consumidor espera y se desbloquea
[OK] 400 depositos, 400 retiros, 8 hilos
[OK] Capacidad respetada y balance final: 0

[OK] TODAS LAS PRUEBAS DE MONITORES PASARON
```

La clase se ejecutó **13 veces correctamente** durante esta auditoría (una ejecución inicial y doce repeticiones). Esto respalda el comportamiento observado, aunque no constituye una demostración formal de ausencia de fallas bajo cualquier intercalación de hilos.

### 4.9. Persistencia y autenticación — `PruebaPersistenciaDAO`

Se utilizó un **archivo temporal independiente**, sin modificar los datos de usuarios de la aplicación. Se comprobó el registro de dos usuarios, rechazo de nombres duplicados, búsquedas, contraseñas correctas e incorrectas, validación del DTO, estructura del archivo, persistencia al crear una segunda instancia del DAO y limpieza del usuario activo al cerrar sesión. El archivo temporal se elimina al finalizar.

**Salida final registrada:**

```text
[OK] Registro de usuarios y rechazo de duplicados
[OK] Busqueda, credenciales y entradas invalidas
[OK] Archivo con dos registros y formato esperado
[OK] Persistencia entre instancias y cierre de sesion

[OK] TODAS LAS PRUEBAS DE PERSISTENCIA PASARON
```

**Consideración de seguridad:** la codificación Base64 de las credenciales es reversible y no equivale a cifrado ni a un hash de contraseñas. Las pruebas comprueban el mecanismo implementado, no su idoneidad para un sistema productivo.

## 5. Demostraciones gráficas disponibles

El repositorio incluye clases ejecutables para revisar visualmente las interfaces:

| Clase | Propósito |
|---|---|
| `PruebaJPanelTablero` | Abrir el tablero y observar los elementos y turnos de `Destino` |
| `PruebaPanelMisiones` | Abrir la interfaz de misiones |
| `pruebaPanelConcurrencia` | Observar productores y consumidores desde el visor |
| `PruebaJPanelTablero.PruebaPersistencia` | Demostración visual de registro e inicio de sesión (clase interna) |
| `MainApp` | Recorrido completo de la aplicación |

**Estado de la evidencia:** la compilación de estas clases está verificada, pero este documento **no certifica** el uso manual de todos los controles, la navegación por las pantallas ni la validación visual completa. Esas verificaciones deben realizarse en la computadora de presentación.

## 6. Lista de verificación manual antes de entregar

- [ ] Abrir `MainApp` y completar registro e inicio de sesión.
- [ ] Crear cada tipo de nave y comprobar la tripulación mínima de cinco integrantes.
- [ ] Revisar la visualización de haberes y subsidios por origen.
- [ ] Confirmar que el tablero, los sensores y la bitácora se actualizan durante la navegación.
- [ ] Ejecutar M-01, M-02 y M-03 desde la interfaz, revisar sus informes y comprobar los recursos finales.
- [ ] Verificar que el panel de recursos muestre correctamente combustible, energía, desgaste y gemas.
- [ ] Abrir el Visor Recóndito, incorporar productores y consumidores y comprobar la respuesta de la interfaz.
- [ ] Confirmar el JDK y la compilación del proyecto en el equipo que utilizarán para entregar o defender.

## 7. Conclusión

En la versión auditada, **9 de 9 pruebas automáticas principales pasaron**, y no se detectaron errores durante la compilación con Java 21. Las verificaciones cubren contratos de recursos, State, Decorator por origen, universo, sensores, navegación, misiones, Double Dispatch, monitores y persistencia DAO/DTO.

Los antiguos registros de los escenarios A y B, así como sus mensajes de `PENDIENTE`, no se mantienen porque reflejaban una implementación y unos casos de prueba anteriores. Su eliminación documental no implica afirmar que todos los requisitos históricos hayan quedado demostrados: las afirmaciones de este archivo se limitan a los comportamientos efectivamente comprobados.

La **prueba manual integral de la interfaz y la compilación con Maven usando el JDK de entrega** siguen siendo verificaciones finales recomendadas.