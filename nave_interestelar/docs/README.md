# Sistema de Nave Interestelar
### Trabajo Práctico Grupal — Programación C

**Grupo 12 — Ingeniería Informática — Universidad Nacional de Mar del Plata**

## 1. Descripción general

El proyecto consiste en el desarrollo de un sistema de control y administración de una nave interestelar, implementado en Java mediante programación orientada a objetos.

El sistema permite registrar usuarios, crear y configurar distintos tipos de naves, administrar tripulantes y sus haberes, navegar por un universo dinámico, ejecutar misiones y observar una simulación concurrente de un universo paralelo.

La aplicación cuenta con una interfaz gráfica desarrollada mediante Java Swing y utiliza diferentes patrones de diseño para organizar las responsabilidades y facilitar el mantenimiento del código.

El trabajo integra los requerimientos de la Parte 1 y la Parte 2.

## 2. Tecnologías utilizadas

- **Lenguaje:** Java.
- **Interfaz gráfica:** Java Swing.
- **Gestión del proyecto:** Apache Maven.
- **Arquitectura de interfaz:** Modelo-Vista-Controlador (MVC).
- **Persistencia:** archivos de texto mediante DAO y DTO.
- **Concurrencia:** hilos de Java, monitores, `synchronized`, `wait()` y `notifyAll()`.
- **Documentación:** Javadoc, diagramas UML y documentación Markdown.

El archivo `pom.xml` establece actualmente Java 26 como versión de compilación. Para utilizar Maven sin modificar esa configuración se necesita un JDK compatible.

## 3. Funcionalidades principales

### 3.1. Registro e inicio de sesión

El sistema dispone de un módulo de autenticación que permite registrar usuarios e iniciar sesión mediante nombre de usuario y contraseña.

Las credenciales se almacenan en un archivo local mediante el patrón DAO. El intercambio de información se realiza mediante objetos DTO.

Únicamente se persisten las credenciales: las naves, los tripulantes y los resultados de las misiones permanecen en memoria durante la ejecución.

### 3.2. Creación de naves

Se pueden crear tres tipos de nave mediante una fábrica centralizada:

| Tipo | Combustible inicial | Energía inicial |
|---|---:|---:|
| Exploradora | 60 | 80 |
| Carguero | 100 | 60 |
| Combate | 80 | 100 |

Cada nave dispone de:

- Identificador único dentro de su contexto de creación.
- Combustible y energía.
- Desgaste mecánico.
- Motor Warp.
- Tripulación.
- Gemas recolectadas.
- Posición dentro del universo.

Las naves deben contar con una tripulación válida de al menos cinco integrantes, incluyendo un capitán, para iniciar misiones desde la interfaz principal.

### 3.3. Tripulación y haberes

El sistema permite administrar tripulantes de cuatro cargos:

- Capitán.
- Consejero.
- Teniente.
- Alférez.

Cada cargo posee su cálculo de haberes correspondiente, incluyendo los conceptos definidos para su rol.

Se utiliza el patrón Decorator para incorporar subsidios según el planeta de origen:

| Origen | Subsidio |
|---|---:|
| Terrícola | 20 PG |
| Marciano | 18 PG |
| Vulcano | 30 PG |

La sección de tripulantes permite consultar la información y el salario de los integrantes registrados.

### 3.4. Universo dinámico

El universo se representa mediante un tablero de 20 × 20 posiciones.

Durante la navegación pueden encontrarse:

- Asteroides, que provocan pérdida de energía.
- Estaciones de carga, que permiten recuperar recursos.
- Gemas y anomalías, como elementos del universo.
- Naves enemigas Exploradoras, Cargueras y de Combate.

La clase `Destino`, proporcionada por la cátedra, administra los movimientos aleatorios de los elementos del tablero.

Las interacciones entre naves se resuelven mediante Double Dispatch, utilizando exclusivamente los tipos de las naves participantes para determinar el resultado del enfrentamiento.

### 3.5. Sensores y navegación

La nave dispone de dos modos de escaneo:

- **Radar de corto alcance (3 × 3):** inspecciona el entorno inmediato durante la navegación.
- **Radar de largo alcance (5 × 5):** se utiliza durante la preparación para obtener información del entorno y planificar una ruta inicial.

`ReglasNavegacion` determina cómo avanzar hacia el objetivo. Cuando encuentra un obstáculo en la siguiente posición, puede solicitar una maniobra Warp.

El `AsistenteDeComandos` coordina las operaciones entre la nave, las reglas de navegación, los sensores y el tablero.

### 3.6. Motor Warp

El Motor Warp utiliza el patrón State.

Sus estados son:

1. `Disponible`.
2. `PreparandoSalto`.
3. `EnWarp`.
4. `Enfriamento`.

La navegación contempla:

| Operación | Comportamiento |
|---|---|
| Salto normal | Avanza una casilla y consume 2 de combustible |
| Preparación Warp | Requiere 2 turnos |
| Salto Warp | Avanza 2 casillas y consume 5 de energía |
| Enfriamiento | Requiere 1 turno posterior y genera 3 de desgaste |

Durante la preparación y el enfriamiento, la nave no efectúa un desplazamiento normal.

### 3.7. Misiones espaciales

El sistema implementa tres tipos de misión:

**M-01 — Intercepción y asistencia**

La nave debe llegar hasta la posición de una nave aliada y entregarle los recursos de combustible y/o energía solicitados.

**M-02 — Recolección**

La nave se dirige hasta un objetivo y recolecta una cantidad de gemas comprendida entre 20 y 100.

**M-03 — Retorno a punto seguro**

La nave regresa a su base de origen para reabastecer combustible y energía hasta 100 y reducir su desgaste a 0.

Todas las misiones siguen el ciclo definido mediante Template Method:

**Preparar → Ejecutar → Evaluar → Cerrar**

Durante la ejecución, la nave avanza por turnos, interactúa con el entorno y registra eventos en la bitácora.

Al finalizar se genera un `InformeMision` con el resultado, las acciones principales, el balance de recursos, el estado final de la nave y las observaciones relevantes.

La interfaz conserva un historial de informes durante la ejecución de la aplicación.

### 3.8. Visor Recóndito — Concurrencia

Este módulo representa un universo paralelo en el que múltiples productores y consumidores comparten una Plataforma de Carga.

Sus componentes son:

- `GeneradoraDeEnergia`: produce baterías.
- `NaveConsumidora`: retira y consume baterías.
- `PlataformaDeCarga`: recurso compartido de capacidad limitada.

Cada generadora y nave consumidora se ejecuta mediante un hilo.

La coordinación se realiza mediante monitores de Java, con métodos `synchronized`, condiciones de espera en bucles `while`, `wait()` y `notifyAll()`.

Desde la interfaz gráfica se pueden agregar productores y consumidores mientras la simulación está activa, observando los eventos automáticamente.

## 4. Arquitectura del proyecto

El código se organiza en los siguientes componentes principales:

| Paquete | Responsabilidad |
|---|---|
| `modelo.nave` | Naves, fábrica, recursos y enfrentamientos |
| `modelo.Tripulacion` | Tripulantes, haberes y Decorator |
| `modelo.motorWarp` | Estados y funcionamiento del Warp |
| `modelo.misiones` | Ciclo de misiones e informes |
| `modelo.destinoytablero` | Tablero, posiciones y Destino |
| `modelo.universo` | Integración del universo |
| `modelo.elementos` | Elementos e interacciones del entorno |
| `modelo.sensores` | Radares y reglas de navegación |
| `modelo.asistenteDeComandos` | Coordinación de operaciones |
| `modelo.bitacora` | Registro y notificación de eventos |
| `modelo.Persistencia` | DAO, DTO y autenticación |
| `modelo.UniversoAlterno` | Productores, consumidores y monitores |
| `Controlador` | Coordinación entre modelos y vistas |
| `vista` | Interfaz gráfica Swing |
| `Pruebas` | Escenarios y verificaciones del sistema |

La clase `MainApp` es el punto de entrada de la aplicación gráfica.

## 5. Patrones de diseño implementados

| Patrón | Aplicación |
|---|---|
| Factory / Simple Factory | Creación de naves |
| Singleton | Capitán y bitácora |
| State | Motor Warp |
| Template Method | Ciclo de las misiones |
| Decorator | Subsidios de origen de los tripulantes |
| Double Dispatch | Resolución de enfrentamientos entre naves |
| Observer / Observable | Actualización del tablero y la bitácora |
| MVC | Separación entre interfaz y lógica del sistema |
| DAO | Acceso a las credenciales persistidas |
| DTO | Transporte de datos de usuarios |
| Productor–Consumidor | Simulación concurrente mediante monitores |

La explicación de los patrones, las clases participantes y sus responsabilidades se encuentra en `nave_interestelar/docs/patrones.md`.

## 6. Ejecución del proyecto

### Requisitos

- JDK compatible con la versión configurada en `pom.xml`.
- Apache Maven.
- IntelliJ IDEA, Eclipse o un IDE equivalente.

### Desde IntelliJ IDEA

1. Abrir la carpeta `nave_interestelar` como proyecto Maven.
2. Configurar el JDK del proyecto.
3. Esperar a que se carguen las dependencias.
4. Localizar `src/main/java/MainApp.java`.
5. Ejecutar su método `main()`.

### Desde una terminal

Ubicarse en la carpeta que contiene `pom.xml`:

`cd nave_interestelar`

Compilar el proyecto:

`mvn clean compile`

Ejecutar la aplicación:

`java -cp target/classes MainApp`

El sistema mostrará primero las pantallas de registro e inicio de sesión.

### Archivo de usuarios

La aplicación principal utiliza el archivo `target/usuarios.txt`, relativo al directorio desde el que se ejecuta.

Este archivo se crea automáticamente si no existe.

Al ejecutar `mvn clean`, Maven elimina `target/` y, por lo tanto, también los usuarios almacenados en esa ubicación.

## 7. Uso de la aplicación

Una vez iniciada la aplicación:

1. Registrar un usuario o iniciar sesión.
2. Ingresar a **Configuración & Creación** y seleccionar un tipo de nave.
3. Crear la tripulación requerida.
4. Consultar los haberes desde **Tripulantes & Haberes**.
5. Acceder a **Control de Nave & Tablero** para observar el universo, sus movimientos, los sensores y los recursos.
6. Acceder a **Misiones**, seleccionar M-01, M-02 o M-03 y ejecutar la operación.
7. Consultar el informe y el historial de misiones.
8. Acceder al **Visor Recóndito (Concurrencia)** para agregar generadoras y naves consumidoras.

El tablero y las vistas de control permiten observar el desarrollo de las operaciones sin detener la interfaz.

## 8. Pruebas disponibles

El proyecto incluye pruebas y demostraciones mediante clases con método `main()`.

### Pruebas de consola

| Clase | Verificación |
|---|---|
| `Prueba` | Creación, tripulación y operaciones básicas |
| `Pruebas.EscenarioC` | State y Motor Warp |
| `Pruebas.EscenarioD` | Recursos y restricciones |
| `Pruebas.PruebaHaberes` | Cálculo de salarios y Decorator |
| `Pruebas.PruebaUniverso` | Tablero, elementos e interacciones |
| `Pruebas.Prueba_reglas` | Sensores y reglas de navegación |
| `Pruebas.PruebaMisiones` | Ejecución de misiones y resultados |

### Pruebas de interfaz

| Clase | Verificación |
|---|---|
| `PruebaPersistencia` | Acceso y registro de usuarios |
| `Pruebas.PruebaJPanelTablero` | Representación del tablero |
| `Pruebas.PruebaPanelMisiones` | Interfaz de misiones |
| `Pruebas.pruebaPanelConcurrencia` | Interfaz del universo paralelo |

Las clases de prueba son ejecutables independientes y no forman una suite JUnit.

Para verificar el funcionamiento integral del sistema debe ejecutarse `MainApp`.

## 9. Documentación

El repositorio incluye:

- `README.md`: descripción, instalación, uso y estructura del proyecto.
- `nave_interestelar/docs/patrones.md`: explicación de patrones de diseño.
- `nave_interestelar/docs/Evidencias.md`: evidencias y verificaciones.
- `nave_interestelar/docs/USO-IA.md`: documentación del uso de herramientas de inteligencia artificial.
- Archivos `uml*.puml`: diagramas del diseño del sistema.
- `nave_interestelar/docs/Informe-TPNaveEspacial_1.0.pdf`: informe correspondiente a la primera parte del trabajo.

## 10. Alcance y consideraciones

El proyecto es una implementación académica del sistema solicitado.

El historial de misiones permanece en memoria y no se almacena de forma persistente, de acuerdo con el alcance de la Parte 2.

La bitácora utiliza una instancia compartida y notificaciones a sus observadores. Su implementación actual no incorpora sincronización general para escrituras concurrentes.

La autenticación utiliza un archivo local con credenciales codificadas en Base64. Esta codificación no equivale a cifrado seguro ni a almacenamiento mediante hash criptográfico, por lo que el mecanismo está orientado al alcance académico del trabajo.

La documentación y las pruebas permiten consultar tanto las decisiones de diseño como las operaciones principales implementadas.

## 11. Integrantes

**Grupo 12 — Programación C — UNMdP.**

Trabajo desarrollado de forma grupal, con responsabilidades distribuidas entre los integrantes para los módulos de naves y tripulación, navegación y universo, misiones, interfaz gráfica y concurrencia.