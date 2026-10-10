# Patrones de diseño — TP Grupal de Programación C

**Grupo 12 · Entrega 1 · Sistema de nave interestelar**

## 1. Objetivo y alcance

Este documento explica los patrones de diseño presentes en el código del proyecto, sus responsabilidades, las clases que participan, las decisiones de diseño y la manera de demostrar su funcionamiento. Se corresponde con la versión revisada del repositorio **`TP-prograC-grupo-12-main (5)`**. Si el código cambia después, se deberá actualizar la documentación.

La Entrega 1 exige aplicar cuatro patrones al dominio: **Factory** para crear naves, **State** para administrar el Motor Warp, **Template Method** para coordinar las misiones y **Decorator** para calcular haberes. El proyecto también utiliza **Singleton** para compartir ciertas instancias.

| Patrón | Propósito dentro del sistema | Punto principal de implementación | Demostración |
|---|---|---|---|
| Factory (fábrica simple) | Crear los tres tipos de nave sin instanciarlos desde el cliente | `modelo.nave.NaveFactory` | `Pruebas.EscenarioA` |
| State | Cambiar el comportamiento permitido del motor según su estado | `modelo.motorWarp.MotorWarp` y `State` | `Pruebas.EscenarioC` |
| Template Method | Mantener un ciclo común para las tres misiones | `modelo.misiones.Mision.director()` | `Pruebas.EscenarioA` y `EscenarioB` |
| Decorator | Agregar subsidios de origen al cálculo de haberes | `Haber`, `DecoratorPlaneta` y decoradores de origen | `Pruebas.PruebaHaberes` |
| Singleton (complementario) | Mantener un punto de acceso a una instancia compartida | `Bitacora` y `Capitan` | Utilizado en escenarios A, B y C |

> **Alcance verificado:** la implementación actual de Decorator permite añadir subsidios de origen, pero todavía no representa con decoradores independientes todos los conceptos remunerativos solicitados. Se detalla en el apartado 5 para distinguir lo implementado de lo pendiente.

## 2. Factory — Creación de naves

### 2.1. Problema que resuelve

El cliente necesita obtener naves **exploradoras, cargueras o de combate**, cada una con una configuración inicial determinada. Si todas las clases clientes construyeran directamente las subclases concretas, quedarían acopladas a sus constructores y a la selección de tipos.

### 2.2. Clases y responsabilidades

- **`Nave`**: clase abstracta que concentra recursos, tripulación, mantenimiento y Motor Warp.
- **`NaveExploradora`, `NaveCarguero` y `NaveCombate`**: especializaciones concretas de `Nave`.
- **`TipoNave`**: enumeración `EXPLORADORA`, `CARGUERO`, `COMBATE`.
- **`NaveFactory`**: concentra la decisión de qué subclase construir y devuelve una referencia de tipo `Nave`.

Los constructores de las tres naves concretas tienen acceso de paquete. Esto permite que la fábrica —ubicada en `modelo.nave`— las cree y evita su construcción directa desde paquetes clientes diferentes.

### 2.3. Implementación y ejemplo

La fábrica ofrece un método estático y un constructor privado, por lo que no es necesario instanciarla:

```java
Nave exploradora = NaveFactory.crearNave(TipoNave.EXPLORADORA, 1);
Nave carguero    = NaveFactory.crearNave(TipoNave.CARGUERO, 2);
Nave combate     = NaveFactory.crearNave(TipoNave.COMBATE, 3);
```

El método `crearNave(TipoNave tipo, int id)` rechaza `tipo == null` mediante `IllegalArgumentException` y utiliza un `switch` para elegir la subclase.

| Tipo | Combustible inicial | Energía inicial | Desgaste inicial |
|---|---:|---:|---:|
| Exploradora | 60 | 80 | 0 |
| Carguero | 100 | 60 | 0 |
| Combate | 80 | 100 | 0 |

**Beneficio:** el cliente depende de la abstracción `Nave` y de una única interfaz de creación. Incorporar otro tipo exige actualizar la fábrica y el tipo enumerado, en lugar de dispersar la selección de constructores.

**Denominación técnica:** esta implementación es una **Simple Factory** o *fábrica simple*. Satisface el objetivo de creación centralizada identificado como «Factory» en el TP; no debe confundirse con la variante GoF *Factory Method*, basada en un método de fábrica redefinido mediante herencia.

**Evidencia:** `Pruebas.EscenarioA` crea los tres tipos, consulta sus recursos y comprueba los valores iniciales esperados. La creación inválida con tipo `null` puede verificarse por separado.

## 3. State — Estados del Motor Warp

### 3.1. Problema que resuelve

El Motor Warp no puede ejecutar cualquier operación en cualquier momento. Por ejemplo, no debería **entrar en warp desde Disponible** sin preparar previamente el salto. El patrón State encapsula estas reglas en objetos que representan estados, en lugar de mantener una gran cadena de condiciones dentro del motor.

### 3.2. Participantes

- **`MotorWarp` (contexto)**: mantiene una referencia al estado actual (`State`) y delega a ese objeto las órdenes que recibe.
- **`State` (interfaz)**: declara `prepararSalto()`, `entrarEnWarp()`, `iniciarEnfriamiento()` y `volverADisponible()`.
- **`Disponible`, `PreparandoSalto`, `EnWarp` y `Enfriamento` (estados concretos)**: deciden si cada orden provoca una transición o registra un rechazo.
- **`Bitacora`**: almacena mensajes asociados a los cambios y errores.

El nombre **`Enfriamento`** se conserva aquí exactamente como aparece en el código fuente (aunque el nombre conceptual del estado sea «Enfriamiento»).

### 3.3. Flujo de estados

```text
Disponible
    │ prepararSalto()
    ▼
PreparandoSalto
    │ entrarEnWarp()
    ▼
EnWarp
    │ iniciarEnfriamiento()
    ▼
Enfriamento
    │ volverAlDisponible()
    ▼
Disponible
```

`MotorWarp` comienza en `Disponible`. Sus métodos públicos se limitan a **delegar** en el objeto de estado actual. Las transiciones válidas crean el siguiente estado y lo asignan con `setState(...)`.

### 3.4. Manejo de transiciones inválidas

Las transiciones no permitidas **no modifican el estado**. En el código revisado, los estados registran el rechazo en la Bitácora en lugar de lanzar sistemáticamente una excepción. Por ejemplo, `Disponible.entrarEnWarp()` registra:

```text
ERROR: no se puede entrar en warp sin preparar el salto
```

Por lo tanto, la evidencia correcta consiste en observar **que el estado permanece igual y que existe un registro de error**. No debe afirmarse que todas las transiciones inválidas lanzan `IllegalStateException`, porque el código no lo implementa así.

**Beneficio:** las reglas de transición están localizadas en las clases de estado y el contexto `MotorWarp` no necesita conocer todas las condiciones particulares.

**Evidencia:** `Pruebas.EscenarioC` intenta una transición inválida desde `Disponible`, verifica que se registra el error y recorre luego la secuencia válida completa.

## 4. Template Method — Ciclo de las misiones

### 4.1. Problema que resuelve

Las misiones M-01 (Intercepción y asistencia), M-02 (Recolección) y M-03 (Retorno seguro) comparten las mismas etapas generales, pero su acción particular es diferente. Duplicar el ciclo completo en cada clase complicaría mantener el orden y las verificaciones comunes.

### 4.2. Participantes

- **`Mision` (clase abstracta)**: define la estructura común del ciclo y sus puntos de extensión.
- **`Mision.director()` (Template Method)**: método `final` que establece el orden de la ejecución.
- **`Intercepcion`, `Recoleccion` y `Retorno`**: especializaciones que implementan la acción y la evaluación.
- **`AsistenteDeComandos`**: colaborador utilizado para consultar recursos, ordenar consumos, registrar eventos y solicitar saltos.
- **`InformeMision`**: representación del resultado generado al cierre.

### 4.3. Orden fijo del algoritmo

```text
Mision.director()
   ├── preparar()
   │     └── si falla, registra cancelación y devuelve false
   ├── ejecutar()       [implementado por cada misión]
   ├── evaluar()        [implementado por cada misión]
   └── cerrar()         [implementación común]
          ├── salto Warp si el resultado es EXITOSA
          ├── registro del cierre en Bitácora
          └── creación de InformeMision
```

La firma `public final boolean director()` impide que las subclases redefinan el orden del algoritmo. `preparar()` y `cerrar()` son métodos concretos reutilizados, mientras que `ejecutar()`, `evaluar()` y los métodos de costos son abstractos.

### 4.4. Diferencias entre las misiones

| Subclase | Acción de `ejecutar()` en la versión revisada | Evaluación actual |
|---|---|---|
| `Intercepcion` | Registra aproximación al objetivo y ordena el consumo | `EXITOSA` |
| `Recoleccion` | Registra recolección de muestra y ordena el consumo | `EXITOSA` |
| `Retorno` | Ordena el consumo del regreso | `EXITOSA` |

En la versión revisada, las implementaciones de `evaluar()` devuelven `EXITOSA` sin condiciones adicionales; la verificación previa de disponibilidad se hace en `preparar()`.

**Casos de cancelación:** si `preparar()` no encuentra combustible o energía suficientes, `director()` devuelve `false`, registra el motivo y no llega a `ejecutar()`, `evaluar()` ni `cerrar()`. En ese caso, la misión no genera un informe final por la ruta normal de cierre.

**Beneficio:** se reutiliza una única estructura de ejecución y se restringe el margen para que una misión concreta altere el ciclo obligatorio.

**Evidencias:** `Pruebas.EscenarioA` ejecuta las tres misiones con recursos suficientes; `Pruebas.EscenarioB` comprueba el rechazo por insuficiencia de recursos y que no se produzcan cambios parciales.

**Observación de implementación:** `Mision.cerrar()` obtiene las acciones del informe filtrando la Bitácora con `AsistenteDeComandos.eventosDe(nombreDeClase)`. Como algunas acciones se registran con prefijos como `M1 Intercepción` o `M2 Recolección`, pueden no incorporarse a ese filtro. Además, el filtro no distingue dos ejecuciones del mismo tipo de misión. Estos detalles afectan la completitud del informe, **no la existencia del patrón Template Method**.

## 5. Decorator — Liquidación de haberes

### 5.1. Problema que resuelve

El haber mensual combina una remuneración según el cargo con adicionales. El patrón Decorator permite **envolver** un objeto que implementa `Haber` y sumar un concepto sin crear una subclase para cada combinación posible de cargo y origen.

### 5.2. Participantes actuales

- **`Haber` (componente)**: interfaz con `double getSueldo()`.
- **`Tripulante` (componente base abstracto)**: implementa `Haber`.
- **`Capitan`, `Consejero`, `Teniente`, `Alferez` (componentes concretos)**: calculan básico y antigüedad; el consejero incluye además el importe por consejos.
- **`DecoratorPlaneta` (decorador abstracto)**: también implementa `Haber` y conserva una referencia a otro `Haber`.
- **`Terricola`, `Marciano`, `Vulcano` (decoradores concretos)**: delegan el cálculo al objeto envuelto y agregan un subsidio fijo.

### 5.3. Ejemplo real del código

```java
Teniente teniente = new Teniente(new Identidad("Tripulante A"), 2);
Haber liquidacion = new Terricola(teniente);
System.out.println(liquidacion.getSueldo()); // 444.0 PG
```

En este ejemplo, el componente `Teniente` calcula **400 PG de básico + 24 PG por dos años de antigüedad**. El decorador `Terricola` añade **20 PG**, dando **444 PG**.

| Concepto | Importe |
|---|---:|
| Teniente: sueldo básico | 400 PG |
| Teniente: antigüedad, 3 % por año × 2 años | 24 PG |
| Terrícola: subsidio de origen | 20 PG |
| **Total** | **444 PG** |

Los tres decoradores de origen suman: **Terricola +20 PG, Marciano +18 PG y Vulcano +30 PG**.

### 5.4. Alcance real y límite de cumplimiento

**Implementado:** existe una estructura genuina de Decorator para **el subsidio por origen**: los decoradores y los componentes comparten la interfaz `Haber`, y un decorador contiene otro `Haber`.

**Pendiente respecto del enunciado E1-08:** la antigüedad se calcula dentro de cada clase concreta de cargo; no hay todavía un `AdicionalAntiguedad` que pueda componerse como decorador independiente. Del mismo modo, el importe de los consejos se suma dentro de `Consejero`.

Aunque la estructura permite anidar dos decoradores de origen:

```java
Haber ejemploTecnico = new Marciano(new Terricola(alferez));
```

ese ejemplo **no representa una liquidación válida**, porque atribuye dos orígenes al mismo tripulante. Por eso no demuestra correctamente una composición de **conceptos diferentes**. También falta que el resultado de la liquidación exponga cada aporte como dato estructurado: `getSueldo()` devuelve únicamente el total.

**Mejora necesaria para cumplir plenamente el objetivo de composición:** separar al menos otro concepto (por ejemplo, `AdicionalAntiguedad`) en un decorador y evitar duplicar su cálculo en el cargo. La presente documentación describe el código existente; no afirma que esa refactorización ya esté hecha.

**Beneficio del patrón presente:** se pueden añadir subsidios de origen sin modificar las clases `Teniente`, `Consejero`, etc., ni multiplicar la jerarquía de cargos.

**Evidencia:** `Pruebas.PruebaHaberes` verifica los cuatro cargos y los tres subsidios de origen. Su demostración de dos decoradores de planeta es **solo técnica** y la propia clase señala la limitación de dominio.

## 6. Singleton — Patrón complementario

No forma parte de los cuatro patrones obligatorios E1, pero aparece en el proyecto:

- **`Bitacora`**: tiene constructor privado, una referencia estática `instance` y un método `getInstance()` que devuelve la instancia compartida. Se utiliza para que componentes diferentes escriban en un mismo registro.
- **`Capitan`**: tiene constructor privado, referencia estática `instancia` y método `getInstancia(...)`. En la versión revisada, el patrón establece **un solo capitán para todo el proceso**, no uno por nave: después de la primera creación, los parámetros de llamadas posteriores ya no se utilizan para construir otro capitán.

Esta decisión debe ser conocida durante la defensa. Las demostraciones se diseñaron para ejecutarse como programas independientes, lo cual evita compartir accidentalmente el mismo `Capitan` entre distintos escenarios dentro de una sola JVM.

El singleton de `Bitacora` es de inicialización perezosa y no incorpora sincronización para accesos concurrentes; esto es compatible con el alcance simplificado, no concurrente, de E1.

## 7. Trazabilidad con demostraciones de la Entrega 1

Las siguientes clases poseen `main()` y están ubicadas en `nave_iterestelar/src/main/java/Pruebas/`. No utilizan JUnit.

| Clase ejecutable | Qué permite verificar | Patrones relacionados |
|---|---|---|
| `EscenarioA` | Crear tres tipos de nave; ejecutar y cerrar M-01, M-02 y M-03 | Factory, Template Method, State |
| `EscenarioB` | Intentar una misión con recursos insuficientes y verificar cancelación sin consumo parcial | Template Method |
| `EscenarioC` | Recorrer transiciones válidas y rechazar/registrar una inválida | State |
| `EscenarioD` | Comprobar precondiciones, invariantes y mantenimiento | Contratos del dominio; no es un patrón de diseño |
| `PruebaHaberes` | Calcular haberes por cargo y aplicar subsidios de los tres orígenes | Decorator |

Desde la carpeta `nave_iterestelar/`, y con un JDK compatible con el `pom.xml` (en el ZIP revisado se configura Java 26), se puede compilar el proyecto y ejecutar cada demostración:

```bash
mvn compile
java -cp target/classes Pruebas.EscenarioA
java -cp target/classes Pruebas.EscenarioB
java -cp target/classes Pruebas.EscenarioC
java -cp target/classes Pruebas.EscenarioD
java -cp target/classes Pruebas.PruebaHaberes
```

Estas instrucciones reproducen los `main()`; las salidas verificadas y la correspondencia con la matriz de casos deben conservarse por separado en `docs/evidencias/`.

## 8. Conclusión

El sistema utiliza **Factory** para desacoplar la creación de naves, **State** para distribuir las transiciones del motor entre estados concretos y **Template Method** para imponer un orden común de ejecución de misiones. La implementación de **Decorator** es funcional para los subsidios de origen, pero debe ampliarse si se quiere demostrar la composición válida de distintos conceptos remunerativos requerida por E1-08. **Singleton** aparece como una decisión adicional para `Bitacora` y `Capitan`.

El propósito de esta documentación es facilitar la comprensión, la verificación y la defensa del diseño, reflejando las responsabilidades efectivamente presentes en el código de la versión indicada.
