# Patrones de diseño del Sistema de Nave Interestelar

**Trabajo Práctico Grupal — Programación C — Grupo 12**

## 1. Introducción

El Sistema de Nave Interestelar utiliza diferentes patrones de diseño para distribuir las responsabilidades de sus componentes y reducir el acoplamiento entre las clases.

La implementación combina patrones de creación, estructurales, de comportamiento, de arquitectura y de acceso a datos. También incorpora un esquema de concurrencia basado en Productor–Consumidor.

A continuación se describen los patrones aplicados, su propósito, las clases participantes y su funcionamiento en el proyecto.

## 2. Factory — Creación de naves

### Problema

El sistema debe permitir crear distintos tipos de nave sin que las clases clientes necesiten conocer los constructores concretos de cada una.

### Implementación

La clase `NaveFactory`, ubicada en `modelo.nave`, concentra la creación de las naves.

Participan las siguientes clases:

- `Nave`: clase abstracta común.
- `NaveExploradora`: nave exploradora.
- `NaveCarguero`: nave carguera.
- `NaveCombate`: nave de combate.
- `TipoNave`: enumeración de tipos.
- `NaveFactory`: fábrica de instancias.

El método `crearNave(TipoNave tipo, int id)` recibe el tipo solicitado y devuelve una instancia de la subclase correspondiente.

Ejemplo de utilización:

`Nave nave = NaveFactory.crearNave(TipoNave.EXPLORADORA, 10);`

### Beneficios

- Centraliza la construcción de naves.
- Evita repetir la selección de subclases.
- Permite trabajar con referencias del tipo abstracto `Nave`.
- Facilita mantener las configuraciones iniciales en un solo punto de creación.

### Consideración

La implementación corresponde a una **Simple Factory** o fábrica simple. No utiliza una jerarquía de fábricas especializadas como el patrón GoF Factory Method.

## 3. Singleton — Instancias compartidas

### Problema

Ciertas entidades deben mantenerse como instancias únicas durante la ejecución.

### Implementación

**Clase `Bitacora`**

Administra los mensajes que registran las operaciones del sistema.

Posee un constructor privado y proporciona acceso mediante `getInstance()`. Los componentes comparten la misma bitácora y pueden registrar eventos sin crear registros independientes.

**Clase `Capitan`**

El sistema utiliza una única instancia de capitán, obtenida mediante `getInstancia(...)`.

El constructor es privado y la instancia se conserva mediante un atributo estático.

### Beneficios

- Evita la creación de múltiples objetos cuando se requiere una instancia compartida.
- Centraliza el registro de eventos.
- Mantiene una identidad única para el capitán dentro del proceso.

### Consideración

El Singleton de `Capitan` tiene alcance de proceso: no representa automáticamente un capitán diferente por cada usuario o sesión.

## 4. State — Motor Warp

### Problema

El comportamiento del motor depende de su estado actual.

Un motor disponible puede efectuar un salto normal, mientras que un motor preparando un Warp o enfriándose tiene restricciones de operación diferentes.

Implementar todas estas condiciones en una única clase mediante numerosos condicionales dificultaría el mantenimiento.

### Implementación

El patrón está formado por:

- `MotorWarp`: contexto del patrón.
- `State`: abstracción común de estados.
- `Disponible`: estado inicial.
- `PreparandoSalto`: preparación de la maniobra.
- `EnWarp`: ejecución del salto especial.
- `Enfriamento`: recuperación posterior al salto.

Las clases de estado controlan las transiciones, mientras que `MotorWarp` administra los turnos y consumos asociados.

Las operaciones públicas de vuelo son `saltar(Nave)` y `saltarWarp(Nave)`.

### Secuencia del salto Warp

| Turno | Operación | Resultado |
|---|---|---|
| 1 | Preparación | Sin desplazamiento |
| 2 | Preparación | Sin desplazamiento |
| 3 | Salto Warp | Avance de 2 casillas y consumo de 5 de energía |
| 4 | Enfriamiento | Sin desplazamiento; genera 3 de desgaste |

Un salto normal avanza una casilla y consume 2 de combustible.

`ReglasNavegacion` determina las coordenadas del movimiento, mientras que `MotorWarp` administra la maniobra y sus estados.

### Beneficios

- Separa las reglas de cada estado.
- Permite modificar las transiciones sin afectar toda la navegación.
- Evita ejecutar acciones no permitidas durante preparación o enfriamiento.
- Mantiene la responsabilidad del desplazamiento fuera del motor.

## 5. Template Method — Ciclo de misiones

### Problema

Las misiones M-01, M-02 y M-03 deben seguir el mismo ciclo general, pero sus objetivos, costos y operaciones finales son diferentes.

Sin un método plantilla, cada clase tendría que repetir la lógica común.

### Implementación

La clase abstracta `Mision` define el método `director()`, que establece el orden del algoritmo:

**Preparar → Ejecutar → Evaluar → Cerrar**

El método está declarado como `final`, por lo que las subclases no pueden modificar la estructura principal del ciclo.

Las clases concretas son:

- `Intercepcion` — M-01.
- `Recoleccion` — M-02.
- `Retorno` — M-03.

### Fases

**Preparar**

Verifica los recursos requeridos, determina el objetivo y almacena una ruta inicial.

La planificación incorpora información del radar de largo alcance.

Cuando la preparación falla, se cancela el intento sin aplicar el consumo de despegue y se genera un informe de fallo.

**Ejecutar**

Aplica los costos iniciales y ejecuta la navegación por turnos.

En cada iteración, la nave interactúa con los elementos de su posición, consulta sensores, se desplaza y permite que `Destino` actualice el tablero.

Incluye pausas entre turnos para permitir la observación de la travesía.

**Evaluar**

Determina el resultado de la misión y construye un informe con el balance de recursos, estado final y acciones relevantes.

**Cerrar**

Ejecuta las operaciones definitivas del tipo de misión:

- M-01: transferencia de recursos a la nave aliada.
- M-02: incorporación de gemas.
- M-03: recarga y mantenimiento.

### Puntos de especialización

Las subclases implementan métodos como:

- `getCombustibleRequerido()`.
- `getEnergiaRequerida()`.
- `getDesgasteGenerado()`.
- `definirObjetivo()`.
- `verificarEnDestino()`.
- `aplicarCierre()`.
- `describirCierre()`.

### Beneficios

- Evita duplicación del ciclo de ejecución.
- Mantiene una secuencia uniforme.
- Permite agregar nuevas misiones conservando la estructura general.
- Centraliza la gestión de informes y resultados.

## 6. Decorator — Cálculo de haberes

### Problema

Los tripulantes tienen salarios asociados a sus cargos y pueden recibir un subsidio adicional por planeta de origen.

Se necesita incorporar dicho adicional sin crear una subclase diferente para cada combinación de cargo y planeta.

### Implementación

El patrón utiliza:

- `Haber`: interfaz común de cálculo.
- `Tripulante`: clase base de los cargos.
- `Capitan`, `Consejero`, `Teniente`, `Alferez`: cargos concretos.
- `DecoratorPlaneta`: decorador abstracto.
- `Terricola`, `Marciano`, `Vulcano`: decoradores concretos.

Cada decorador conserva una referencia a otro objeto `Haber` y agrega un subsidio al resultado de `getSueldo()`.

### Ejemplo

Un teniente puede envolverse con un decorador Terrícola:

`Haber liquidacion = new Terricola(teniente);`

Al ejecutar `liquidacion.getSueldo()`, se obtiene el sueldo del cargo más el subsidio correspondiente.

Los subsidios implementados son:

- Terrícola: 20 PG.
- Marciano: 18 PG.
- Vulcano: 30 PG.

### Beneficios

- Permite incorporar adicionales por origen sin modificar los cargos concretos.
- Evita una jerarquía de clases por cada combinación.
- Respeta una interfaz común para el cálculo.

### Alcance actual

La estructura Decorator se utiliza para el subsidio por planeta.

La antigüedad y los adicionales específicos del cargo se calculan dentro de las clases de tripulantes, en lugar de implementarse como decoradores independientes.

Por lo tanto, el proyecto demuestra composición para el origen, pero no una composición independiente de todos los conceptos remunerativos posibles.

## 7. Double Dispatch — Enfrentamientos entre naves

### Problema

El resultado de un combate depende simultáneamente del tipo de la nave del jugador y del tipo de la nave enemiga.

La sobrecarga de métodos por sí sola no resuelve este problema cuando las referencias se manejan mediante la clase abstracta `Nave`.

### Implementación

El patrón se implementa en:

- `Nave`.
- `NaveExploradora`.
- `NaveCarguero`.
- `NaveCombate`.
- `ResultadoCombate`.

La resolución utiliza dos despachos.

Primero, la interacción invoca:

`naveJugador.enfrentar(enemiga);`

El método `enfrentar` de la subclase concreta del jugador delega en un segundo método de la nave rival, por ejemplo:

`rival.recibirEncuentroDe(this);`

Este segundo despacho selecciona el método sobrecargado según el tipo concreto de la nave propia.

Así, el resultado considera ambos tipos sin depender de una matriz de condicionales centralizada.

### Matriz de resultados

Los resultados se expresan desde la perspectiva de la nave del jugador.

| Nave propia / Enemiga | Exploradora | Carguero | Combate |
|---|---|---|---|
| Exploradora | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

Los resultados posibles están representados mediante `ResultadoCombate`.

Cuando la nave del jugador pierde, se destruye y la misión no puede continuar exitosamente.

### Beneficios

- Elimina la necesidad de consultar explícitamente los tipos de ambas naves mediante condicionales.
- Aprovecha el polimorfismo.
- Separa las reglas de combate entre los tipos concretos.
- Permite representar directamente la matriz solicitada por el enunciado.

## 8. Observer / Observable — Actualizaciones automáticas

### Problema

La interfaz gráfica debe reaccionar a los cambios del modelo sin consultar continuamente su estado.

Por ejemplo, cuando se mueve un elemento del tablero o se registra un evento, las vistas correspondientes deben actualizarse.

### Implementación

El proyecto utiliza Observer en diferentes módulos.

**Tablero**

La clase `Tablero` utiliza la infraestructura `Observable` y `Observador` suministrada por la cátedra.

Los observadores se registran mediante `agregarObservador()` y reciben notificaciones con `actualizar()`.

Esto permite actualizar la representación del universo.

**Bitácora**

`Bitacora` mantiene una colección de objetos `IObservadorBitacora`.

Cuando se registra un evento, invoca `actualizarBitacora(String evento)` en los observadores registrados.

De esta forma, los mensajes pueden mostrarse en la interfaz.

**Universo paralelo**

`PlataformaDeCarga` permite registrar objetos `PlataformaObserver`.

La interfaz recibe información sobre los eventos y la cantidad de baterías disponibles mediante `onPlataformaCambio(...)`.

### Beneficios

- Desacopla el modelo de las clases visuales.
- Facilita agregar nuevas vistas.
- Evita consultas permanentes al modelo.
- Permite actualizaciones automáticas del tablero, la bitácora y el visor.

Las actualizaciones de los componentes Swing se coordinan mediante el hilo de eventos de Swing cuando corresponde.

## 9. MVC — Modelo-Vista-Controlador

### Problema

La interfaz no debe contener directamente toda la lógica de negocio.

Se necesita separar la recepción de eventos del usuario de las operaciones sobre el modelo.

### Implementación

El proyecto divide las responsabilidades de la siguiente manera:

**Modelo**

Contiene las reglas del dominio y su estado.

Ejemplos: `Nave`, `Mision`, `MotorWarp`, `Universo`, `Tablero`, `PlataformaDeCarga`.

**Vista**

Presenta información y recibe interacciones del usuario.

Ejemplos:

- `JFrameMain`.
- `JPanelCreacionNave`.
- `JPanelTripulantes`.
- `JPanelTablero`.
- `PanelMisiones`.
- `PanelBitacoraYSensores`.
- `JPanelVisorRecondito`.

**Controlador**

Recibe acciones desde las vistas, invoca operaciones del modelo y coordina las respuestas.

Ejemplos:

- `AutenticacionController`.
- `CreacionNaveController`.
- `TripulantesController`.
- `MisionController`.
- `VisorReconditoController`.

### Ejemplo: ejecución de una misión

1. El usuario selecciona una misión en `PanelMisiones`.
2. La vista comunica la solicitud a `MisionController`.
3. El controlador valida los datos y crea la misión correspondiente.
4. La misión ejecuta la lógica mediante `AsistenteDeComandos`.
5. Los cambios se reflejan en el tablero y la bitácora.
6. Al finalizar, el controlador notifica el resultado a la vista.

La ejecución prolongada de las misiones se realiza fuera del hilo principal de eventos de Swing, evitando bloquear la interfaz.

### Beneficios

- Separa presentación y lógica.
- Facilita las pruebas independientes.
- Reduce el acoplamiento.
- Complementa Observer para las actualizaciones automáticas.

## 10. DAO — Acceso a datos

### Problema

El sistema necesita almacenar y recuperar las credenciales de los usuarios sin que los controladores conozcan los detalles del archivo utilizado.

### Implementación

Participan:

- `UsuarioDAO`: interfaz de operaciones de persistencia.
- `ArchivoUsuarioDao`: implementación mediante archivo local.
- `AutenticacionService`: servicio de autenticación que utiliza el DAO.

La interfaz `UsuarioDAO` declara operaciones para registrar, buscar y autenticar usuarios.

`ArchivoUsuarioDao` implementa estas operaciones utilizando lectura y escritura de archivos.

### Beneficios

- Separa la persistencia de la lógica de autenticación.
- Permite reemplazar el mecanismo de almacenamiento sin modificar los controladores.
- Centraliza las operaciones de lectura y escritura.

El sistema persiste únicamente credenciales, respetando el alcance establecido para la Parte 2.

## 11. DTO — Transferencia de datos

### Problema

Es necesario transportar los datos de un usuario entre las capas del sistema sin exponer detalles del almacenamiento.

### Implementación

La clase `UsuarioDTO` contiene:

- Nombre de usuario.
- Contraseña.

El DAO utiliza estos objetos para registrar y recuperar usuarios.

`AutenticacionService` también los utiliza para representar al usuario autenticado.

### Beneficios

- Agrupa la información transferida.
- Evita manejar múltiples parámetros sueltos.
- Separa los datos transportados de las operaciones de persistencia.

En la implementación académica, las contraseñas se codifican mediante Base64 al guardarse en el archivo. Esta codificación es reversible y no proporciona protección criptográfica equivalente a un hash de contraseñas.

## 12. Productor–Consumidor con monitores

### Problema

Múltiples productores y consumidores comparten una plataforma de capacidad limitada.

Las generadoras deben esperar cuando la plataforma está llena, mientras que las naves consumidoras deben esperar cuando se encuentra vacía.

Se requiere evitar accesos simultáneos que alteren incorrectamente la cantidad de baterías.

### Implementación

Las clases involucradas son:

- `GeneradoraDeEnergia`: productor.
- `NaveConsumidora`: consumidor.
- `PlataformaDeCarga`: monitor y recurso compartido.
- `PlataformaObserver`: notificación de eventos.

`GeneradoraDeEnergia` y `NaveConsumidora` implementan `Runnable` y se ejecutan mediante hilos.

`PlataformaDeCarga` mantiene la cantidad disponible y la capacidad máxima.

Sus operaciones principales son:

- `depositarBateria(String generadoraId)`.
- `retirarBateria(String naveId)`.

Ambos métodos están declarados como `synchronized`.

Cuando una operación no puede continuar, el hilo ejecuta `wait()` dentro de un bucle `while`.

Después de depositar o retirar una batería, se utiliza `notifyAll()` para despertar a los hilos que esperan.

### Condiciones de sincronización

| Situación | Comportamiento |
|---|---|
| Plataforma con espacio | El productor deposita una batería |
| Plataforma llena | El productor espera |
| Plataforma con baterías | El consumidor retira una batería |
| Plataforma vacía | El consumidor espera |
| Cambio de disponibilidad | Se notifica a los hilos en espera |

La interfaz del Visor Recóndito permite agregar nuevos productores y consumidores mientras la simulación está activa.

### Beneficios

- Garantiza exclusión mutua sobre el recurso compartido.
- Evita superar la capacidad de la plataforma.
- Evita retirar baterías inexistentes.
- Permite que múltiples hilos coordinen sus operaciones.
- Integra concurrencia con Observer para representar los eventos visualmente.

Productor–Consumidor no es un patrón de diseño GoF, sino un patrón clásico de coordinación concurrente implementado mediante monitores.

## 13. Relación entre patrones

Los patrones no funcionan de manera aislada, sino que colaboran en distintas partes de la aplicación.

| Operación | Patrones involucrados |
|---|---|
| Crear una nave | Factory |
| Mantener una bitácora compartida | Singleton |
| Calcular haberes | Decorator |
| Navegar con Warp | State |
| Ejecutar una misión | Template Method |
| Resolver una batalla | Double Dispatch |
| Actualizar automáticamente una vista | Observer |
| Procesar acciones del usuario | MVC |
| Registrar o autenticar usuarios | DAO y DTO |
| Coordinar baterías en el visor | Productor–Consumidor y Observer |

Un ejemplo de integración es la ejecución de M-01:

La interfaz recibe la solicitud a través de MVC; el controlador crea una `Intercepcion`; Template Method coordina su ciclo; `AsistenteDeComandos` utiliza las reglas de navegación y State para mover la nave; Double Dispatch resuelve posibles encuentros; Observer actualiza el tablero y la bitácora; finalmente se genera el informe correspondiente.

## 14. Consideraciones de diseño

Las decisiones principales del proyecto son:

- Mantener las reglas de negocio separadas de la interfaz.
- Centralizar la creación de naves mediante una fábrica.
- Utilizar estados explícitos para controlar el Motor Warp.
- Reutilizar el ciclo de misión mediante Template Method.
- Aprovechar polimorfismo para los enfrentamientos.
- Mantener la infraestructura de `Tablero` y `Destino` proporcionada por la cátedra.
- Persistir únicamente credenciales, no los objetos del dominio.
- Utilizar monitores para proteger la Plataforma de Carga.
- Mantener los informes de misión en memoria durante la ejecución.

El código también conserva documentación Javadoc de métodos, precondiciones, postcondiciones e invariantes en las clases principales.

## 15. Conclusión

El Sistema de Nave Interestelar integra patrones de diseño de distintas categorías para resolver los requerimientos de creación, navegación, misiones, combate, persistencia, interfaz gráfica y concurrencia.

Factory centraliza la creación; Singleton comparte instancias; State organiza los estados del Warp; Template Method define el ciclo común de misiones; Decorator incorpora subsidios; Double Dispatch resuelve encuentros entre tipos concretos; MVC y Observer separan las acciones del usuario de las actualizaciones del modelo; DAO y DTO organizan la persistencia; y Productor–Consumidor coordina el acceso concurrente a la plataforma.

Esta combinación permite dividir el proyecto en componentes con responsabilidades definidas y constituye la base del diseño orientado a objetos utilizado en ambas partes del trabajo práctico.