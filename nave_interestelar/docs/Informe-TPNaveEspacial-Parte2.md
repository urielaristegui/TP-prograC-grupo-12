# Trabajo Práctico Grupal
# Sistema de Nave Interestelar
## Informe de continuación — Entrega 2

**Asignatura:** Programación C  
**Carrera:** Ingeniería Informática  
**Institución:** Facultad de Ingeniería — Universidad Nacional de Mar del Plata  
**Año:** 2026  
**Grupo:** 12

**Docentes a cargo:**
- Leonel Domingo Guccione
- Lucas de Lellis
- Micaela Rasso

**Integrantes:**
- Jeremías Joaquín Bernardi
- Nestor Uriel Aristegui
- Santiago Martinez
- Agustin Veca

---

## 1. Introducción

El presente informe constituye la continuación del trabajo práctico *Sistema de Nave Interestelar*, desarrollado para la asignatura Programación C.

Durante la primera etapa se construyó el núcleo orientado a objetos del sistema. Se implementaron las distintas clases de naves, la administración de sus recursos, el modelo de tripulación, el cálculo de haberes, el Motor Warp y la estructura general de las misiones. Esta implementación permitió establecer una base modular sobre la cual incorporar las funcionalidades de la segunda entrega.

El objetivo de la Parte 2 fue transformar aquel modelo inicial en una aplicación capaz de simular una travesía espacial dentro de un universo dinámico, incorporando navegación por turnos, sensores, interacciones con elementos del entorno, persistencia de usuarios, concurrencia e interfaz gráfica.

Para alcanzar estos objetivos, el grupo integró las clases desarrolladas anteriormente con los nuevos componentes exigidos por la cátedra, manteniendo los principios de programación orientada a objetos y extendiendo el uso de patrones de diseño.

El resultado es una aplicación Java que permite registrar operadores, crear y configurar una nave, administrar su tripulación, ejecutar misiones sobre un tablero, consultar informes y observar una simulación concurrente de productores y consumidores.

## 2. Objetivos de la segunda entrega

El objetivo principal fue ampliar la funcionalidad del sistema conservando el diseño orientado a objetos establecido en la Parte 1.

Los objetivos específicos fueron:

1. Incorporar un mecanismo de registro e inicio de sesión con persistencia de credenciales mediante los patrones DAO y DTO.
2. Integrar un universo dinámico representado mediante un tablero de 20 × 20 posiciones.
3. Implementar elementos espaciales capaces de interactuar con la nave y modificar sus recursos.
4. Incorporar sensores de corto y largo alcance y reglas de navegación.
5. Extender las misiones para que se ejecuten sobre el tablero mediante desplazamientos reales.
6. Resolver los enfrentamientos entre distintos tipos de naves utilizando Double Dispatch.
7. Integrar los estados del Motor Warp con la navegación por turnos.
8. Implementar el problema de Productor–Consumidor mediante monitores de Java.
9. Desarrollar una interfaz gráfica utilizando MVC y Observer para la actualización automática.
10. Incorporar pruebas que permitan verificar los comportamientos principales y documentar sus resultados.

El desarrollo se realizó de forma incremental, reutilizando los componentes existentes y agregando las responsabilidades necesarias para esta segunda etapa.

## 3. Arquitectura general del sistema

La aplicación se organiza en módulos con responsabilidades diferenciadas.

El **modelo** contiene las entidades del dominio, sus recursos y reglas de funcionamiento. La **vista** está implementada mediante Java Swing y permite operar el sistema y observar su estado. Los **controladores** coordinan las acciones del usuario y las operaciones del modelo.

Esta separación permite evitar que las reglas de negocio dependan directamente de los componentes gráficos.

### 3.1. Organización de componentes

| Componente | Responsabilidad |
|---|---|
| `modelo.nave` | Tipos de naves, recursos, creación y enfrentamientos |
| `modelo.Tripulacion` | Cargos, haberes y subsidios por origen |
| `modelo.motorWarp` | Estados y operaciones del Motor Warp |
| `modelo.misiones` | Ciclo de misiones, especializaciones e informes |
| `modelo.universo` | Integración del tablero con la nave y los elementos |
| `modelo.destinoytablero` | Posiciones, tablero, movimiento de elementos y observadores |
| `modelo.elementos` | Asteroides, estaciones de carga, gemas y anomalías |
| `modelo.sensores` | Sensores y reglas de navegación |
| `modelo.asistenteDeComandos` | Coordinación de las operaciones de la nave |
| `modelo.bitacora` | Registro y notificación de eventos |
| `modelo.Persistencia` | Usuarios, autenticación, DAO y DTO |
| `modelo.UniversoAlterno` | Plataforma de carga, productores y consumidores |
| `Controlador` | Coordinación entre las vistas y los modelos |
| `vista` | Interfaz gráfica de la aplicación |
| `Pruebas` | Escenarios de validación y demostraciones |

La clase `MainApp` funciona como punto de entrada de la aplicación.

### 3.2. Comunicación entre módulos

Una de las decisiones principales fue utilizar `AsistenteDeComandos` como intermediario entre las misiones y los componentes de navegación.

Las misiones no necesitan manipular directamente el tablero ni implementar sus propios movimientos. Solicitan operaciones al asistente, que coordina la nave, los sensores, las reglas de navegación y los efectos del universo.

De esta manera se conserva una separación entre **qué debe hacer una misión** y **cómo se realizan sus operaciones de navegación**.

La interfaz gráfica se comunica con el modelo a través de controladores y mecanismos de observación, permitiendo reflejar los cambios sin incorporar la lógica de negocio en las vistas.

## 4. Universo dinámico, elementos e interacciones

### 4.1. Representación del universo

El universo principal se representa mediante un tablero de 20 × 20 posiciones.

Se integraron las clases `Tablero` y `Destino` proporcionadas por la cátedra, conservando su función de representar el entorno y actualizar las posiciones de sus elementos.

La clase `Universo` coordina el tablero y las entidades que contiene. A su vez, `JPanelTablero` se encarga de representar gráficamente su estado.

Durante la ejecución de una misión, cada turno comprende operaciones de la nave y una actualización del tablero. Esto permite que el entorno cambie mientras la nave intenta alcanzar su objetivo.

Una dificultad surgida durante la integración fue mantener coherentes las posiciones almacenadas por las naves enemigas y las posiciones que ocupaban en el tablero después de los movimientos de `Destino`.

Para resolverla se incorporó una sincronización de posiciones luego de actualizar el entorno, evitando que un mismo elemento conservara coordenadas distintas en el modelo.

### 4.2. Elementos del universo

Los elementos espaciales implementan la interfaz `ElementoDelUniverso`, permitiendo que el sistema trate distintos tipos de entidades mediante una abstracción común.

Entre los elementos incorporados se encuentran:

- **Asteroides:** representan obstáculos capaces de reducir la energía de la nave.
- **Estaciones de carga:** permiten recuperar combustible y energía.
- **Gemas:** representan recursos recolectables.
- **Anomalías:** incorporan interacciones adicionales al entorno.
- **Naves enemigas:** pueden generar encuentros y enfrentamientos con la nave principal.

Cada elemento concentra su comportamiento de interacción, evitando que el asistente de comandos deba conocer los detalles internos de todas las entidades.

### 4.3. Enfrentamientos mediante Double Dispatch

La Parte 2 incorporó enfrentamientos cuyo resultado depende exclusivamente de la combinación de tipos de las naves participantes.

Para resolver esta situación se utilizó el patrón **Double Dispatch**, mediante métodos polimórficos que permiten seleccionar el comportamiento a partir de ambos tipos concretos.

La matriz implementada es:

| Nave propia / Nave enemiga | Exploradora | Carguero | Combate |
|---|---|---|---|
| Exploradora | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

El resultado de una derrota implica la destrucción de la nave propia y la pérdida de su tripulación.

Esta solución evita depender de un conjunto centralizado de comparaciones de tipos y aprovecha el polimorfismo de las subclases de `Nave`.

## 5. Sensores, navegación y Motor Warp

### 5.1. Sistema de sensores

Para permitir que la nave evalúe el entorno se desarrolló el módulo `Sensores`, que incorpora dos mecanismos de exploración:

- **Radar de corto alcance:** inspecciona una región de 3 × 3 posiciones alrededor de la nave.
- **Radar de largo alcance:** inspecciona una región de 5 × 5 posiciones y se utiliza durante la preparación de las misiones.

El radar de largo alcance proporciona información inicial para calcular una ruta hacia el objetivo. En la implementación, las posiciones fuera de su alcance se consideran desconocidas y se utiliza una planificación provisional.

Por este motivo, la ruta inicialmente calculada no necesariamente coincide con el recorrido real. Los obstáculos, los movimientos del tablero y las maniobras Warp pueden modificar las decisiones tomadas durante la travesía.

### 5.2. Reglas de navegación

La clase `ReglasNavegacion` determina los movimientos a realizar a partir de la posición actual, el objetivo y la información de los sensores.

Cuando el camino está despejado, la nave puede realizar un salto normal, avanzando una posición y consumiendo 2 unidades de combustible.

Cuando se detecta un obstáculo en la siguiente posición, las reglas pueden solicitar una maniobra Warp, que permite avanzar dos posiciones después de completar su preparación.

La navegación utiliza operaciones del `MotorWarp` para determinar si un movimiento está habilitado. Esto permite mantener separadas las decisiones de desplazamiento y la administración de los estados del motor.

### 5.3. Integración del patrón State

El Motor Warp había sido desarrollado durante la primera entrega mediante el patrón State.

En esta segunda etapa se integró su funcionamiento con la navegación real sobre el tablero, manteniendo los estados:

- `Disponible`.
- `PreparandoSalto`.
- `EnWarp`.
- `Enfriamento`.

La secuencia operativa implementada es:

| Turno | Operación | Consecuencia |
|---|---|---|
| 1 | Preparación Warp | Sin desplazamiento |
| 2 | Preparación Warp | Sin desplazamiento |
| 3 | Ejecución del salto | Avanza dos posiciones y consume 5 de energía |
| 4 | Enfriamiento | No avanza y genera 3 de desgaste |

Durante las pruebas de integración se revisó especialmente el momento en que debía aplicarse el desgaste.

Se corrigió el comportamiento para que los tres puntos correspondientes a la maniobra se generen **durante el enfriamiento posterior**, evitando aplicarlos de forma anticipada o duplicada.

También se contempló el caso en que el salto Warp termina exactamente en la coordenada objetivo de una misión, de manera que el enfriamiento pendiente pueda procesarse antes de finalizar su ejecución.

## 6. Ejecución de misiones mediante Template Method

### 6.1. Extensión de las misiones

En la Parte 1 se había definido la estructura de las misiones mediante el patrón Template Method.

La Parte 2 amplió este comportamiento para incorporar una travesía real por el universo, en lugar de limitar la misión a operaciones sobre recursos.

La clase abstracta `Mision` conserva el método final `director()`, que establece el orden:

**Preparar → Ejecutar → Evaluar → Cerrar**

Las clases `Intercepcion`, `Recoleccion` y `Retorno` especializan los comportamientos correspondientes a cada tipo de misión.

### 6.2. Preparación

La preparación verifica que la nave disponga de los recursos mínimos exigidos para iniciar la operación.

Una vez superada esta verificación, se determina la coordenada objetivo y se calcula una ruta inicial utilizando el radar de largo alcance.

Si la nave no tiene recursos suficientes, el intento se rechaza sin aplicar consumos parciales y se genera un informe que registra el motivo del fallo.

### 6.3. Ejecución

La fase de ejecución comienza aplicando los costos iniciales de la misión.

Luego se desarrolla un ciclo por turnos en el que la nave inspecciona su posición, interactúa con los elementos presentes, consulta los sensores y solicita el siguiente movimiento.

Posteriormente, `Destino` actualiza los elementos del tablero.

Se incorporó una pausa de aproximadamente un segundo entre turnos mediante `Thread.sleep()`, permitiendo observar visualmente el avance.

Para evitar recorridos indefinidos se estableció un límite de turnos. La misión también finaliza cuando se alcanza el destino, se produce una condición de destrucción o se detecta un fallo.

### 6.4. Costos iniciales

De acuerdo con el anexo, las misiones aplican los siguientes costos de despegue:

| Misión | Combustible | Energía | Desgaste |
|---|---:|---:|---:|
| M-01 — Intercepción | 4 | 5 | +4 |
| M-02 — Recolección | 4 | 10 | +4 |
| M-03 — Retorno | 4 | 0 | +4 |

Estos valores corresponden únicamente al costo inicial. Durante el recorrido también pueden producirse consumos por navegación o cambios derivados de interacciones con el entorno.

### 6.5. M-01 — Intercepción y asistencia

El objetivo de M-01 es acudir a una posición donde se encuentra una nave aliada que necesita suministros.

Para representar la asistencia se incorporó una instancia concreta de nave aliada, diferenciada de la nave que controla el jugador.

La misión permite solicitar entre 0 y 50 unidades de combustible y/o energía.

La transferencia se efectúa cuando ambas naves coinciden en la coordenada definida y se verifica que la nave principal conserve los recursos necesarios para realizarla.

### 6.6. M-02 — Recolección

M-02 consiste en trasladarse hasta una coordenada objetivo para obtener gemas.

La recompensa permitida se encuentra entre 20 y 100 gemas.

Al completar correctamente el recorrido se incorporan las gemas a los recursos de la nave y se registra la ganancia en el informe.

### 6.7. M-03 — Retorno a punto seguro

M-03 tiene como objetivo regresar a la base de origen.

Una vez alcanzada dicha posición, el cierre permite restablecer:

- Combustible: 100.
- Energía: 100.
- Desgaste: 0.

Esta misión permite representar una operación de reabastecimiento y mantenimiento luego de una travesía.

### 6.8. Evaluación, cierre e informes

La fase de evaluación determina el resultado de la travesía y construye un informe con la información disponible.

Posteriormente, el cierre aplica las acciones definitivas correspondientes a la misión: transferencia de suministros, incorporación de gemas o recuperación de recursos.

Una vez realizado el cierre, el informe se actualiza para reflejar el balance definitivo.

`InformeMision` conserva información sobre:

- Misión ejecutada.
- Resultado exitoso o fallido.
- Acciones principales.
- Balance de recursos.
- Estado final de la nave.
- Observaciones.

Los informes también se incorporan a un historial administrado por `MisionController`, que permanece disponible durante la ejecución del programa.

## 7. Interfaz gráfica, MVC y Observer

### 7.1. Organización de la interfaz

La segunda entrega incorpora una interfaz gráfica Java Swing que permite operar los distintos módulos desde una aplicación principal.

Se desarrollaron secciones para:

1. Registro e inicio de sesión.
2. Creación de nave y tripulación.
3. Consulta de tripulantes y haberes.
4. Control de nave, tablero, sensores y recursos.
5. Ejecución de misiones, informes e historial.
6. Visor Recóndito de concurrencia.

La aplicación utiliza una ventana principal (`JFrameMain`) que coordina las distintas secciones.

### 7.2. Separación mediante MVC

Se utilizó el patrón Modelo-Vista-Controlador para separar la interacción con el usuario de las operaciones del dominio.

Por ejemplo, cuando se solicita ejecutar una misión, la vista recibe la acción y la comunica a `MisionController`.

El controlador realiza las validaciones necesarias, instancia el tipo de misión seleccionado y coordina su ejecución.

Una vez finalizada, comunica el resultado a la vista correspondiente.

Este mecanismo reduce el acoplamiento entre los componentes visuales y la lógica de negocio.

### 7.3. Actualización mediante Observer

Para reflejar automáticamente los cambios del modelo se utilizó Observer.

El tablero mantiene observadores que reciben notificaciones cuando cambia su estado. Asimismo, `Bitacora` notifica los mensajes registrados a los componentes interesados.

El panel de control muestra el estado de los recursos de la nave, incluyendo combustible, energía, desgaste y gemas.

La interfaz utiliza mecanismos de Java Swing para procesar sus actualizaciones visuales en el hilo de eventos correspondiente.

### 7.4. Ejecución de misiones sin bloquear la interfaz

Las misiones requieren pausas entre turnos para permitir la observación de los movimientos.

Si toda su ejecución se realizara directamente en el hilo de eventos de Swing, la interfaz podría quedar bloqueada durante la travesía.

Para evitarlo, `MisionController` ejecuta las misiones en un hilo independiente y coordina las notificaciones visuales posteriores.

También se restringe el inicio simultáneo de múltiples misiones desde el mismo controlador.

## 8. Persistencia de usuarios mediante DAO y DTO

### 8.1. Objetivo

La Parte 2 incorporó un mecanismo de autenticación para que los operadores puedan registrarse e iniciar sesión.

De acuerdo con el alcance establecido por la cátedra, únicamente se persisten las credenciales necesarias para autenticar usuarios.

No se almacenan de manera persistente las naves, los tripulantes ni los resultados de las misiones.

### 8.2. Implementación

El módulo de persistencia está compuesto principalmente por:

| Clase | Responsabilidad |
|---|---|
| `UsuarioDTO` | Transportar los datos de usuario y contraseña |
| `UsuarioDAO` | Definir las operaciones de acceso a datos |
| `ArchivoUsuarioDao` | Implementar la persistencia mediante un archivo local |
| `AutenticacionService` | Coordinar el registro y la autenticación |
| `AutenticacionController` | Integrar las operaciones con las vistas |

La utilización de una interfaz DAO permite separar la lógica de autenticación del mecanismo concreto de almacenamiento.

La implementación utiliza un archivo local, sin necesidad de incorporar una base de datos externa.

### 8.3. Alcance y consideraciones

Se implementaron validaciones de usuarios duplicados, credenciales incorrectas y datos de entrada inválidos.

Las credenciales se almacenan mediante una codificación Base64. Esta técnica permite representar los datos, pero **no constituye un mecanismo de cifrado seguro ni un hash de contraseñas**.

Por lo tanto, el módulo se considera adecuado para demostrar los patrones y las operaciones solicitadas dentro del alcance académico, pero no representa una solución de seguridad destinada a un sistema de producción.

## 9. Universo paralelo — Concurrencia mediante monitores

### 9.1. Descripción

La Parte 2 incorpora un módulo independiente de la navegación principal denominado Visor Recóndito.

En esta simulación, varias Generadoras de Energía producen baterías, mientras que distintas Naves Consumidoras intentan retirarlas de una Plataforma de Carga compartida.

La plataforma tiene capacidad limitada, por lo que las operaciones deben coordinarse para evitar depósitos cuando está llena o retiros cuando está vacía.

### 9.2. Productor–Consumidor

Se implementó el patrón clásico Productor–Consumidor mediante monitores de Java.

Los principales componentes son:

- `GeneradoraDeEnergia`: productor ejecutado en un hilo.
- `NaveConsumidora`: consumidor ejecutado en un hilo.
- `PlataformaDeCarga`: monitor que administra el recurso compartido.

Los métodos `depositarBateria()` y `retirarBateria()` están sincronizados para impedir modificaciones simultáneas incorrectas sobre la cantidad de baterías.

Cuando un productor encuentra la plataforma llena, ejecuta `wait()` hasta que exista capacidad disponible.

De forma equivalente, cuando un consumidor encuentra la plataforma vacía, espera hasta que se incorpore una batería.

Las condiciones se comprueban mediante bucles `while`, y después de una operación se utiliza `notifyAll()` para notificar a los hilos en espera.

### 9.3. Integración con la interfaz

El Visor Recóndito permite incorporar nuevas generadoras y consumidoras durante la ejecución.

Cada entidad trabaja en su propio hilo y utiliza la misma plataforma compartida.

Los eventos producidos se comunican a la interfaz mediante observadores, permitiendo visualizar depósitos, retiros y situaciones de espera.

El módulo combina así dos mecanismos: monitores para la sincronización de la lógica concurrente y Observer para la actualización visual de los eventos.

## 10. Pruebas y validación del proyecto

### 10.1. Estrategia de pruebas

Para validar el comportamiento de la aplicación se prepararon pruebas independientes, implementadas como clases Java con métodos `main()`.

Las pruebas automáticas comprueban condiciones concretas y generan errores cuando un resultado no coincide con lo esperado.

También se conservaron demostraciones visuales para inspeccionar el funcionamiento de los paneles de la aplicación.

### 10.2. Resultados automáticos

En la revisión de la versión final se compiló el proyecto con Java 21 y se ejecutaron las nueve pruebas automáticas principales.

| Prueba | Funcionalidad evaluada | Resultado |
|---|---|---|
| `EscenarioC` | State, preparación, Warp y enfriamiento | Aprobada |
| `EscenarioD` | Recursos, restricciones y mantenimiento | Aprobada |
| `PruebaHaberes` | Salarios y Decorator por origen | Aprobada |
| `PruebaUniverso` | Tablero e interacciones | Aprobada |
| `Prueba_reglas` | Sensores y navegación | Aprobada |
| `PruebaMisiones` | M-01, M-02 y M-03 | Aprobada |
| `PruebaDoubleDispatch` | Nueve combinaciones de enfrentamientos | Aprobada |
| `PruebaMonitores` | Productor–Consumidor y monitores | Aprobada |
| `PruebaPersistenciaDAO` | DAO, DTO y autenticación | Aprobada |

**Resultado:** las nueve pruebas automáticas finalizaron correctamente.

En particular, `PruebaDoubleDispatch` comprobó las nueve combinaciones de combate solicitadas en el enunciado.

`PruebaMonitores` verificó 400 depósitos y 400 retiros mediante ocho hilos concurrentes, manteniendo la capacidad de la plataforma y un balance final de cero baterías. Esta prueba también se repitió para comprobar la estabilidad del comportamiento observado.

Las pruebas de misiones verificaron los resultados de M-01, M-02 y M-03, además del rechazo de una misión cuando los recursos iniciales son insuficientes.

### 10.3. Demostraciones adicionales

El proyecto conserva pruebas visuales y demostraciones independientes para:

- Tablero y actualización del universo.
- Interfaz de misiones.
- Visor Recóndito.
- Interfaz de autenticación.
- Operaciones básicas de nave y tripulación.

La aplicación principal `MainApp` permite realizar una comprobación integral de los módulos.

Estas demostraciones no se incluyen entre las nueve pruebas automáticas, dado que algunas requieren observación o interacción manual.

### 10.4. Alcance de la validación

La compilación verificada durante la revisión se realizó con `javac --release 21`.

El archivo `pom.xml` de la versión revisada tiene configurada la compilación para Java 26, por lo que la ejecución de Maven debe comprobarse con un JDK compatible o ajustando dicha configuración.

La validación automática proporciona evidencia sobre los escenarios cubiertos, pero no reemplaza una inspección manual integral de la interfaz ni garantiza el comportamiento de todas las combinaciones posibles de ejecución.

El detalle de las comprobaciones, las salidas registradas y las demostraciones disponibles se encuentra en `docs/Evidencias.md`.

## 11. Dificultades encontradas y decisiones de diseño

Durante el desarrollo de la segunda entrega surgieron distintos problemas relacionados con la integración de los componentes.

### 11.1. Integración de módulos desarrollados por separado

Uno de los desafíos principales fue conectar clases provenientes de distintos módulos sin duplicar responsabilidades.

Se utilizó `AsistenteDeComandos` como coordinador de operaciones y se incorporaron controladores para comunicar las vistas con los modelos.

Esta decisión permitió conservar buena parte de la lógica previamente implementada y reducir el impacto de los cambios sobre el resto del sistema.

### 11.2. Movimiento dinámico y consistencia de posiciones

La actualización aleatoria del tablero introdujo la necesidad de sincronizar las coordenadas de los elementos móviles con su representación interna.

Se revisaron los puntos donde `Destino` modifica posiciones y se incorporaron operaciones de sincronización para mantener consistentes los datos utilizados por la navegación y los encuentros entre naves.

### 11.3. Coordinación del Motor Warp

La integración del Motor Warp con la navegación implicó distinguir claramente los turnos dedicados a preparación, desplazamiento y enfriamiento.

Se corrigieron las operaciones de consumo y desgaste para que los costos se aplicaran en la fase correspondiente.

Las pruebas del patrón State permitieron identificar discrepancias entre el funcionamiento esperado y las comprobaciones de versiones anteriores.

### 11.4. Ejecución de misiones e informes

La extensión de las misiones requirió incorporar rutas, movimientos y condiciones de fallo sin alterar la secuencia establecida por Template Method.

También se distinguió entre el informe generado durante la evaluación y el balance definitivo obtenido después del cierre.

Esto permitió reflejar las recompensas y entregas de recursos realizadas al finalizar exitosamente cada misión.

### 11.5. Concurrencia y actualización gráfica

El universo paralelo presentó un problema diferente al de la navegación: múltiples hilos debían operar sobre una única plataforma de capacidad limitada.

La solución consistió en utilizar un monitor para proteger las operaciones del recurso compartido y un mecanismo de observación para informar los eventos a la interfaz.

La separación entre sincronización y visualización facilitó las pruebas del modelo sin necesidad de depender de la ejecución completa de Swing.

## 12. Consideraciones y alcance de la implementación

El proyecto fue desarrollado de acuerdo con el alcance académico de las dos entregas.

Entre las principales características y limitaciones de la implementación se destacan:

- La planificación inicial utiliza información del radar, pero el recorrido real puede variar debido al universo dinámico.
- El historial de informes se conserva en memoria y no se persiste entre ejecuciones, conforme al alcance de persistencia solicitado.
- El mecanismo de autenticación utiliza almacenamiento local y codificación Base64, no una solución de seguridad preparada para producción.
- La instancia Singleton de `Capitan` se comparte dentro del proceso, por lo que no representa automáticamente un capitán independiente para cada nueva sesión de usuario.
- La bitácora proporciona un registro compartido y notificaciones de eventos, pero no implementa una sincronización general de escrituras concurrentes.
- El patrón Decorator se utiliza específicamente para el subsidio por origen planetario; los demás componentes del sueldo se calculan en las clases de tripulantes.

Estas consideraciones permiten delimitar con precisión el comportamiento implementado y diferenciar los requisitos académicos de posibles extensiones futuras.

## 13. Uso de herramientas de inteligencia artificial

Durante el desarrollo del trabajo se utilizaron herramientas de inteligencia artificial generativa como apoyo para consultas técnicas, análisis de implementaciones, revisión de patrones, identificación de posibles errores y preparación de pruebas y documentación.

Las propuestas obtenidas fueron contrastadas con los enunciados, los contratos del modelo y el comportamiento observado mediante las pruebas.

No todas las sugerencias fueron incorporadas. Se priorizaron las modificaciones necesarias para el funcionamiento y los requisitos del proyecto, evitando cambios estructurales que no aportaran beneficios concretos.

El registro detallado de las consultas, los objetivos, las intervenciones del equipo y las verificaciones correspondientes se encuentra en `docs/USO-IA.md`.

## 14. Conclusiones

La segunda entrega permitió evolucionar el núcleo orientado a objetos desarrollado en la Parte 1 hacia una aplicación con navegación sobre un universo dinámico, controles gráficos, persistencia de usuarios y simulación concurrente.

El desarrollo integró patrones previamente implementados, como Factory, Singleton, State, Template Method y Decorator, con nuevos mecanismos y patrones necesarios para esta etapa: Double Dispatch, MVC, Observer, DAO, DTO y Productor–Consumidor mediante monitores.

Una de las principales dificultades fue lograr que los módulos funcionaran de manera coordinada, especialmente durante la ejecución de misiones, donde intervienen simultáneamente los recursos de la nave, el Motor Warp, los sensores, las reglas de navegación, los elementos del universo y la actualización del tablero.

La separación de responsabilidades y la incorporación de pruebas independientes permitieron detectar errores de integración y verificar los comportamientos principales.

En la revisión realizada, las nueve pruebas automáticas principales finalizaron satisfactoriamente, proporcionando evidencia de funcionamiento de los módulos centrales.

El resultado final constituye una implementación académica integrada del Sistema de Nave Interestelar, que permite aplicar conceptos de programación orientada a objetos, patrones de diseño, arquitectura MVC, persistencia y programación concurrente.

## 15. Documentación complementaria

El informe se complementa con los siguientes archivos del repositorio:

| Documento | Contenido |
|---|---|
| `docs/Informe-TPNaveEspacial_1.0.pdf` | Informe correspondiente a la primera entrega |
| `docs/README.md` | Descripción general, instalación y ejecución |
| `docs/patrones.md` | Justificación y explicación de patrones implementados |
| `docs/Evidencias.md` | Inventario y resultados de pruebas |
| `docs/USO-IA.md` | Registro del uso de herramientas de inteligencia artificial |
| `docs/uml/` | Diagramas UML del proyecto |

Este documento se presenta como continuación del informe de la Parte 1, incorporando las funcionalidades, decisiones de diseño y resultados propios de la segunda entrega.