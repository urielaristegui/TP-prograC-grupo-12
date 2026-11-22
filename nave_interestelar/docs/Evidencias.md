# Evidencias de pruebas — Sistema de Nave Interestelar

**Trabajo Práctico Grupal — Programación C — Grupo 12**  
**Partes 1 y 2 — Revisión del 22/11/2026**

## 1. Objetivo y alcance

Este documento reúne las verificaciones realizadas sobre el Sistema de Nave Interestelar, desarrollado como Trabajo Práctico Grupal de Programación C.

Su finalidad es documentar el funcionamiento de los módulos principales, los resultados obtenidos y los procedimientos necesarios para reproducir las pruebas.

Se distinguen tres categorías:

- **Pruebas automáticas:** ejecutan verificaciones y generan errores si alguna condición no se cumple.
- **Pruebas visuales y demostraciones auxiliares:** permiten observar el comportamiento del sistema, pero requieren inspección manual de los resultados.
- **Prueba integral:** ejecución de la aplicación completa mediante `MainApp`.

Las evidencias corresponden a la versión revisada del proyecto `TP-prograC-grupo-12-main (14).zip`.

## 2. Entorno de verificación

El proyecto está implementado en Java y utiliza Java Swing para la interfaz gráfica.

La revisión automática se realizó con las siguientes condiciones:

| Característica | Valor |
|---|---|
| Lenguaje | Java |
| JDK utilizado | OpenJDK 21 |
| Compilación | `javac --release 21 -encoding UTF-8` |
| Archivos Java compilados | 90 |
| Pruebas automáticas principales | 9 |
| Pruebas automáticas aprobadas | 9 |
| Framework de pruebas | Métodos `main()` y verificaciones propias |

La compilación finalizó sin errores.

**Consideración:** el archivo `pom.xml` de la versión revisada especifica Java 26. Las comprobaciones indicadas se realizaron directamente mediante `javac` con Java 21, no mediante Maven. Para compilar con Maven es necesario utilizar una versión de JDK compatible con la configuración del proyecto o actualizar dicha configuración.

## 3. Inventario completo de pruebas

Todas las pruebas están ubicadas en:

`nave_interestelar/src/main/java/Pruebas/`

### 3.1. Pruebas automáticas

| Clase | Funcionalidad principal | Resultado |
|---|---|---|
| `EscenarioC` | Motor Warp y patrón State | APROBADA |
| `EscenarioD` | Recursos, contratos y mantenimiento | APROBADA |
| `PruebaHaberes` | Haberes y patrón Decorator | APROBADA |
| `PruebaUniverso` | Tablero e interacciones | APROBADA |
| `Prueba_reglas` | Sensores y navegación | APROBADA |
| `PruebaMisiones` | M-01, M-02, M-03 e informes | APROBADA |
| `PruebaDoubleDispatch` | Nueve combinaciones de combate | APROBADA |
| `PruebaMonitores` | Productor–Consumidor y sincronización | APROBADA |
| `PruebaPersistenciaDAO` | DAO, DTO y autenticación | APROBADA |

Estas nueve pruebas fueron ejecutadas correctamente durante la revisión del proyecto.

### 3.2. Pruebas visuales y demostraciones auxiliares

| Clase | Tipo | Funcionalidad |
|---|---|---|
| `PruebaJPanelTablero` | Visual | Representación y actualización del tablero |
| `PruebaPanelMisiones` | Visual | Interfaz gráfica de misiones |
| `pruebaPanelConcurrencia` | Visual | Visor Recóndito y simulación concurrente |
| `PruebaJPanelTablero.PruebaPersistencia` | Visual | Registro e inicio de sesión |
| `PruebaPanelMisiones.Prueba` | Consola | Creación de nave, tripulación y operaciones básicas |

Las dos últimas son **clases internas**: no poseen un archivo Java independiente, sino que están declaradas dentro de `PruebaJPanelTablero.java` y `PruebaPanelMisiones.java`, respectivamente.

Estas demostraciones están disponibles en el repositorio, pero no forman parte de las nueve pruebas automáticas aprobadas.

### 3.3. Prueba integral

**Clase:** `MainApp`

**Ruta:** `nave_interestelar/src/main/java/MainApp.java`

Permite ejecutar el sistema completo, incluyendo autenticación, creación de naves, tripulación, navegación, misiones y concurrencia.

La prueba integral requiere una revisión manual de las pantallas y sus operaciones.

## 4. Evidencias de las pruebas automáticas

### 4.1. `EscenarioC` — Patrón State y Motor Warp

**Objetivo:** verificar el ciclo completo de funcionamiento del Motor Warp.

Se comprueba:

- Dos turnos de preparación sin desplazamiento ni consumo de energía.
- Ejecución del salto Warp en el tercer turno.
- Consumo de exactamente 5 unidades de energía.
- Ausencia de desgaste antes del enfriamiento.
- Aplicación de exactamente 3 puntos de desgaste durante el enfriamiento.
- Regreso al estado Disponible.
- Ejecución posterior de un salto normal con consumo de 2 unidades de combustible.

**Resultado registrado:**

`[OK] State, preparacion, Warp, enfriamiento y salto normal`

**Estado:** APROBADA.

### 4.2. `EscenarioD` — Contratos y mantenimiento

**Objetivo:** comprobar que las operaciones sobre recursos respeten sus restricciones.

Se verifican:

- Rechazo de cargas superiores a la capacidad.
- Rechazo de consumos cuando los recursos son insuficientes.
- Ausencia de modificaciones parciales ante operaciones inválidas.
- Funcionamiento del mantenimiento.
- Conservación de los recursos que no deben modificarse.

En el escenario de mantenimiento, una nave con desgaste 80 termina con desgaste 0 y conserva los valores de combustible y energía esperados.

**Resultado registrado:**

`[OK] Contratos, atomicidad y mantenimiento verificados`

**Estado:** APROBADA.

### 4.3. `PruebaHaberes` — Cálculo de salarios y Decorator

**Objetivo:** verificar los haberes de los distintos cargos y los subsidios según el origen planetario.

Se comprueban los siguientes haberes en el escenario definido:

| Cargo | Haber |
|---|---:|
| Capitán | 1400 PG |
| Consejero | 664 PG |
| Teniente | 424 PG |
| Alférez | 202 PG |

También se verifican las 12 combinaciones entre los cuatro cargos y los tres orígenes planetarios:

| Origen | Subsidio |
|---|---:|
| Terrícola | +20 PG |
| Marciano | +18 PG |
| Vulcano | +30 PG |

La prueba comprueba que:

- Cada decorador incorpora el subsidio correspondiente.
- `asignarOrigen()` reemplaza el origen anterior.
- No se acumulan subsidios de planetas diferentes.
- El cambio de origen no modifica el salario base del cargo.
- Se rechaza un origen nulo.

**Resultado registrado:**

`[OK] Cuatro cargos, doce subsidios y reemplazo de origen`

**Estado:** APROBADA.

**Alcance:** esta prueba demuestra el funcionamiento de Decorator aplicado al origen planetario. No constituye una prueba de composición de conceptos salariales diferentes, cuyo alcance debe contrastarse con el enunciado original de la Parte 1.

### 4.4. `PruebaUniverso` — Tablero e interacciones

**Objetivo:** verificar el comportamiento del universo dinámico y sus elementos.

Se comprueba:

- Creación del tablero de 20×20.
- Posición inicial de la nave.
- Sincronización entre posiciones del tablero y de las naves.
- Movimiento de elementos mediante `Destino`.
- Interacción con asteroides.
- Interacción con estaciones de carga.
- Recolección de gemas sin duplicación.
- Interacción con anomalías.
- Encuentros entre naves.
- Restricciones de movimiento en los límites del tablero.
- Destrucción de la nave ante un daño fatal.

**Resultados registrados:**

`OK - Posicion enemiga sincronizada tras el turno`

`OK - No permite salir del tablero`

`OK - Asteroide fatal destruye la nave`

`=== TODAS LAS PRUEBAS PASARON ===`

**Estado:** APROBADA.

### 4.5. `Prueba_reglas` — Sensores y navegación

**Objetivo:** verificar la integración entre sensores, reglas de navegación, asistente de comandos y tablero.

Se utiliza un escenario controlado para evitar que los resultados dependan del azar.

Se comprueba:

- Radar de corto alcance de 3×3.
- Radar de largo alcance de 5×5.
- Escaneos sin consumo de recursos ni movimiento de la nave.
- Planificación de una ruta libre de tres pasos.
- Tres movimientos con posiciones exactas.
- Consumo de 2 unidades de combustible por salto normal.
- Sincronización de posiciones entre nave y tablero.
- Ausencia de consumo adicional al alcanzar el objetivo.
- Funcionamiento de los sensores en los bordes.
- Registro de eventos de navegación y sensores en la bitácora.

**Resultados registrados:**

`[OK] Sensores 3x3 y 5x5 sin efectos secundarios`

`[OK] Ruta de tres pasos planificada con radar`

`[OK] Tres movimientos, posiciones y consumos exactos`

`[OK] Permanecer en destino no consume recursos`

`[OK] Ambos sensores funcionan en los bordes`

`[OK] Eventos del radar y ruta en Bitacora`

**Estado:** APROBADA.

### 4.6. `PruebaMisiones` — Template Method y misiones

**Objetivo:** verificar el funcionamiento de las tres misiones y sus informes.

Cada escenario utiliza su propia nave y tablero para evitar dependencias entre pruebas.

**M-01 — Intercepción**

Se verifica:

- Preparación correcta.
- Llegada a la posición de la nave aliada.
- Transferencia de 10 unidades de combustible.
- Transferencia de 10 unidades de energía.
- Generación de un informe exitoso.

**M-02 — Recolección**

Se verifica:

- Preparación y ejecución correctas.
- Incorporación de exactamente 50 gemas.
- Registro de la recompensa en el balance de recursos.
- Generación del informe exitoso.

**M-03 — Retorno**

Se verifica:

- Inicio desde una posición distinta de la base.
- Regreso a la base de origen.
- Combustible final en 100.
- Energía final en 100.
- Desgaste final en 0.
- Generación del informe exitoso.

**Preparación fallida**

Se verifica:

- Rechazo de una misión con combustible insuficiente.
- Ausencia de consumos parciales.
- Conservación de la posición.
- Generación de un informe fallido con el motivo correspondiente.

Además, la prueba comprueba la presencia de las acciones principales, los recursos, el resultado, el estado final y las observaciones de los informes.

**Resultados registrados:**

`[OK] M-01: llegada, transferencia e informe exitoso`

`[OK] M-02: 50 gemas incorporadas e informe exitoso`

`[OK] M-03: retorno, recarga total y mantenimiento`

`[OK] Preparacion rechazada sin efectos parciales`

`[OK] Todas las pruebas de misiones pasaron`

**Estado:** APROBADA.

Durante la revisión se ejecutó tres veces correctamente.

### 4.7. `PruebaDoubleDispatch` — Enfrentamientos entre naves

**Objetivo:** verificar las nueve combinaciones de combate mediante Double Dispatch.

Se utiliza la siguiente matriz desde la perspectiva de la nave propia:

| Nave propia / Rival | Exploradora | Carguero | Combate |
|---|---|---|---|
| Exploradora | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

Además de comprobar los resultados, se verifica que:

- `enfrentar()` no modifique directamente los recursos.
- Perder destruya la nave propia.
- La destrucción afecte los recursos y la tripulación.
- Ganar o pactar conserve el estado de la nave.
- Se rechacen encuentros con rivales nulos.
- Se rechace el autoenfrentamiento.
- Las naves destruidas no puedan participar en combates.

**Resultado registrado:**

`[OK] 9/9 enfrentamientos y validaciones correctas`

**Estado:** APROBADA.

### 4.8. `PruebaMonitores` — Productor–Consumidor

**Objetivo:** verificar la sincronización de la Plataforma de Carga mediante monitores de Java.

Se comprueba:

- Capacidad máxima de la plataforma.
- Depósitos y retiros básicos.
- Espera de productores cuando la plataforma está llena.
- Desbloqueo de productores cuando se libera espacio.
- Espera de consumidores cuando la plataforma está vacía.
- Desbloqueo de consumidores cuando se deposita una batería.
- Respeto de la capacidad durante las operaciones concurrentes.
- Balance final de baterías.

**Prueba concurrente:**

| Característica | Resultado |
|---|---:|
| Productores | 4 |
| Consumidores | 4 |
| Operaciones por hilo | 100 |
| Depósitos esperados y realizados | 400 |
| Retiros esperados y realizados | 400 |
| Capacidad de la plataforma | 5 |
| Balance final | 0 |

**Resultados registrados:**

`[OK] Depositos, retiros y capacidad`

`[OK] Productor espera y se desbloquea`

`[OK] Consumidor espera y se desbloquea`

`[OK] 400 depositos, 400 retiros, 8 hilos`

`[OK] Capacidad respetada y balance final: 0`

**Estado:** APROBADA.

Durante la revisión se ejecutó 13 veces correctamente.

Las repeticiones permiten aumentar la confianza en el comportamiento concurrente observado, aunque no constituyen una prueba formal de ausencia de errores para todas las posibles intercalaciones de hilos.

### 4.9. `PruebaPersistenciaDAO` — DAO, DTO y autenticación

**Objetivo:** verificar la persistencia de usuarios utilizando un archivo temporal independiente.

Se comprueba:

- Registro de usuarios.
- Rechazo de usuarios duplicados.
- Normalización de nombres.
- Búsqueda de usuarios registrados.
- Autenticación con credenciales correctas.
- Rechazo de contraseñas incorrectas.
- Rechazo de usuarios inexistentes.
- Validación de datos del DTO.
- Estructura de los registros almacenados.
- Persistencia después de crear otra instancia del DAO.
- Inicio y cierre de sesión.

La prueba crea un archivo temporal, realiza las operaciones y lo elimina al finalizar.

De esta manera, no modifica las credenciales utilizadas por la aplicación principal.

**Resultados registrados:**

`[OK] Registro de usuarios y rechazo de duplicados`

`[OK] Busqueda, credenciales y entradas invalidas`

`[OK] Archivo con dos registros y formato esperado`

`[OK] Persistencia entre instancias y cierre de sesion`

**Estado:** APROBADA.

**Consideración:** Base64 es una codificación reversible, no un método de cifrado ni de almacenamiento criptográficamente seguro de contraseñas. La prueba verifica la persistencia implementada dentro del alcance académico del proyecto.

## 5. Pruebas visuales y demostraciones auxiliares

Las siguientes pruebas forman parte del proyecto, pero requieren observar sus resultados o interactuar manualmente con sus interfaces.

Por esta razón, se documentan por separado de las pruebas automáticas.

### 5.1. `PruebaJPanelTablero`

**Archivo:** `PruebaJPanelTablero.java`

**Tipo:** prueba visual.

**Objetivo:** observar el funcionamiento del tablero del universo.

La prueba:

- Crea una nave Exploradora.
- Genera un universo de 20×20.
- Incorpora asteroides, estaciones de carga y naves enemigas.
- Selecciona un objetivo.
- Abre una ventana Swing con `JPanelTablero`.
- Ejecuta turnos de `Destino` automáticamente cada 600 milisegundos.

**Verificación manual esperada:** comprobar que el tablero se visualiza correctamente, que los elementos se desplazan y que la interfaz refleja las actualizaciones.

**Estado:** disponible para ejecución visual; pendiente de certificación manual completa.

### 5.2. `PruebaPanelMisiones`

**Archivo:** `PruebaPanelMisiones.java`

**Tipo:** prueba visual.

**Objetivo:** comprobar la apertura y visualización del módulo de misiones.

La prueba:

- Crea una nave Exploradora.
- Inicializa una bitácora.
- Construye un tablero de 7×7.
- Configura sensores y reglas de navegación.
- Instancia el asistente de comandos.
- Abre una ventana Swing con `PanelMisiones`.

**Verificación manual esperada:** comprobar que el panel se abre, que sus controles se muestran correctamente y que permite interactuar con las misiones según la configuración del escenario.

**Estado:** disponible para ejecución visual; pendiente de certificación manual completa.

### 5.3. `pruebaPanelConcurrencia`

**Archivo:** `pruebaPanelConcurrencia.java`

**Tipo:** prueba visual.

**Objetivo:** demostrar la integración de monitores, MVC y Observer en el Visor Recóndito.

La prueba:

- Crea una `PlataformaDeCarga` con capacidad para cinco baterías.
- Instancia `JPanelVisorRecondito`.
- Registra la vista como observadora del modelo.
- Crea `VisorReconditoController`.
- Abre una ventana Swing.
- Inicia una generadora y una nave consumidora.

**Verificación manual esperada:** observar la producción y el consumo de baterías, las actualizaciones automáticas de la interfaz y la incorporación de productores y consumidores adicionales.

**Estado:** disponible para ejecución visual; pendiente de certificación manual completa.

### 5.4. `PruebaJPanelTablero.PruebaPersistencia`

**Archivo:** `PruebaJPanelTablero.java`

**Tipo:** clase interna de prueba visual.

**Objetivo:** demostrar el funcionamiento gráfico del registro e inicio de sesión.

La clase utiliza:

- `ArchivoUsuarioDao`.
- `AutenticacionService`.
- `AutenticacionController`.
- `JPanelLogin`.
- `JPanelRegistro`.

El módulo utiliza un archivo de prueba ubicado en `target/prueba_usuarios.txt`, separado del archivo principal de la aplicación.

La prueba permite observar:

- Navegación entre login y registro.
- Registro de usuarios.
- Inicio de sesión.
- Notificaciones de autenticación.
- Presentación de mensajes de error.

**Verificación manual esperada:** registrar un usuario, iniciar sesión correctamente y comprobar los mensajes ante credenciales incorrectas.

**Estado:** disponible para ejecución visual; pendiente de certificación manual completa.

**Observación:** a diferencia de `PruebaPersistenciaDAO`, esta demostración no utiliza un archivo temporal que se elimine automáticamente.

### 5.5. `PruebaPanelMisiones.Prueba`

**Archivo:** `PruebaPanelMisiones.java`

**Tipo:** clase interna de demostración por consola.

**Objetivo:** ejecutar operaciones básicas de creación y administración de una nave.

La demostración:

- Crea una nave Exploradora.
- Crea un capitán, un consejero, un teniente y dos alféreces.
- Agrega los cinco tripulantes a la nave.
- Muestra la cantidad de tripulantes.
- Consulta la validez de la tripulación.
- Muestra el haber del capitán.
- Busca un tripulante por identificador.
- Ejecuta un consumo de combustible, energía y desgaste.
- Muestra los recursos restantes.
- Registra un evento en la bitácora.

**Verificación manual esperada:** revisar los valores impresos en consola y comprobar que sean coherentes con las operaciones realizadas.

**Estado:** disponible como demostración de consola.

**Observación:** esta clase imprime resultados, pero no utiliza aserciones que verifiquen automáticamente todas las condiciones. Por ello, no se incluye entre las nueve pruebas automáticas aprobadas.

## 6. Prueba integral — `MainApp`

**Archivo:** `nave_interestelar/src/main/java/MainApp.java`

**Objetivo:** verificar el funcionamiento conjunto de los módulos del sistema desde la aplicación principal.

La ejecución integral permite recorrer:

- Registro e inicio de sesión.
- Creación de naves.
- Administración de tripulación.
- Cálculo y visualización de haberes.
- Tablero y universo dinámico.
- Sensores y navegación.
- Ejecución de misiones.
- Visualización de informes.
- Bitácora y recursos.
- Visor Recóndito y concurrencia.

A diferencia de las pruebas automáticas, esta verificación depende de la interacción del usuario con las pantallas.

**Estado:** aplicación disponible para ejecución integral; pendiente de certificación manual completa.

## 7. Lista de verificación manual antes de entregar

- [ ] Ejecutar `MainApp` y verificar el inicio de la aplicación.
- [ ] Registrar un usuario e iniciar sesión.
- [ ] Crear los tres tipos de nave en escenarios independientes.
- [ ] Verificar una tripulación válida de cinco integrantes.
- [ ] Comprobar la visualización de haberes y subsidios por origen.
- [ ] Revisar el tablero dinámico y el movimiento de elementos.
- [ ] Comprobar las actualizaciones del panel de recursos.
- [ ] Ejecutar M-01 y verificar la transferencia a la nave aliada.
- [ ] Ejecutar M-02 y verificar la recolección de gemas.
- [ ] Ejecutar M-03 y verificar el retorno y la recarga.
- [ ] Revisar los informes e historial de misiones.
- [ ] Abrir el Visor Recóndito y agregar productores y consumidores.
- [ ] Verificar que la interfaz siga respondiendo durante las operaciones concurrentes.
- [ ] Compilar y ejecutar el proyecto en el equipo que se utilizará para la entrega o exposición.

## 8. Resumen final de resultados

| Categoría | Cantidad | Estado |
|---|---:|---|
| Pruebas automáticas | 9 | 9 aprobadas |
| Pruebas visuales independientes | 3 | Disponibles |
| Demostración visual interna | 1 | Disponible |
| Demostración de consola interna | 1 | Disponible |
| Aplicación principal (`MainApp`) | 1 | Disponible |

**Total: 14 pruebas y demostraciones identificadas, además de la aplicación principal.**

Las nueve pruebas automáticas principales fueron ejecutadas satisfactoriamente sobre la versión revisada.

Las cinco demostraciones adicionales están incluidas en el repositorio y permiten observar aspectos específicos del sistema, aunque sus verificaciones visuales y manuales deben completarse antes de la entrega.

## 9. Conclusión

La batería de pruebas permite verificar los principales componentes del Sistema de Nave Interestelar y ofrece evidencias reproducibles de su funcionamiento.

Los resultados automáticos cubren las operaciones de recursos, los patrones State, Decorator y Double Dispatch, el universo dinámico, los sensores, las reglas de navegación, las misiones, los monitores de concurrencia y la persistencia DAO/DTO.

Las demostraciones adicionales permiten revisar el funcionamiento visual del tablero, las misiones, el módulo de autenticación, las operaciones básicas de la nave y el universo concurrente.

La documentación distingue entre resultados comprobados automáticamente y verificaciones que requieren observación manual, evitando presentar como aprobados escenarios que todavía no fueron evaluados de forma integral.

**Resultado general:** las nueve pruebas automáticas principales aprobaron; no se detectaron errores de compilación en la revisión con Java 21.

Como última etapa, se recomienda completar la revisión manual de `MainApp` y comprobar la compilación en el entorno elegido para la entrega.