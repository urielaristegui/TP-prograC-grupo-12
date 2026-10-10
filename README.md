# TPG Programación C 2026 — Sistema de Nave Interestelar

**Grupo 12 · Facultad de Ingeniería · UNMdP · Entrega 1**

Proyecto desarrollado en **Java y Maven** para modelar el software embarcado de una nave interestelar tripulada. Incluye naves de distintos tipos, tripulación, liquidación de haberes, administración de recursos, Motor Warp, bitácora y misiones simuladas.

La Entrega 1 se enfoca en el núcleo orientado a objetos. No requiere base de datos, interfaz gráfica ni integración con el universo dinámico de la Entrega 2.

## 1. Requisitos de ejecución

- **JDK 26**, configurado como SDK del proyecto y como JDK utilizado por Maven. El archivo `nave_iterestelar/pom.xml` declara `maven.compiler.source` y `maven.compiler.target` en `26`.
- **Apache Maven**, preferentemente versión 3.9 o superior, accesible desde una terminal o desde el IDE.
- Un IDE con soporte de Maven y Java, por ejemplo IntelliJ IDEA, o una terminal.
- No se requieren bibliotecas externas, servicios, cuentas, variables secretas ni conexión a una base de datos para ejecutar esta entrega. La primera compilación de Maven puede requerir Internet para descargar plugins.

Comprobar la instalación:

```bash
java -version
javac -version
mvn -version
```

Las versiones de Java utilizadas por `java`, `javac` y Maven deben ser compatibles con **Java 26**. Si Maven selecciona otro JDK, configurar `JAVA_HOME` para apuntar al JDK 26 y volver a ejecutar `mvn -version`.

> **Importante:** aunque el código fuente se ha compilado también con `javac` 21 en una comprobación local, la configuración **vigente del proyecto exige JDK 26** al utilizar Maven. No se debe presentar una comprobación con `javac` 21 como si fuera una compilación Maven con JDK 26.

## 2. Ubicación del proyecto y estructura

El archivo `pom.xml` **no está en la raíz del repositorio**, sino dentro de `nave_iterestelar/`. Todos los comandos Maven que siguen deben ejecutarse en esa carpeta.

```text
TP-prograC-grupo-12-main/
├── README.md                        # Este documento
├── .gitignore
└── nave_iterestelar/
    ├── pom.xml                      # Configuración Maven
    └── src/
        └── main/
            └── java/
                ├── Prueba.java      # Demostración general
                ├── Pruebas/
                │   ├── EscenarioA.java
                │   ├── EscenarioB.java
                │   ├── EscenarioC.java
                │   ├── EscenarioD.java
                │   └── PruebaHaberes.java
                └── modelo/
                    ├── nave/                  # Nave, subclases, Factory
                    ├── Tripulacion/           # Cargos, identidad y haberes
                    ├── motorWarp/             # Motor y estados (State)
                    ├── misiones/              # Misiones e informes
                    ├── asistenteDeComandos/   # Coordinación de operaciones
                    └── bitacora/             # Registro de eventos
```

**Atención a las mayúsculas:** los paquetes `Pruebas` y `modelo.Tripulacion` aparecen así en el código fuente; deben conservarse al ejecutar las clases desde sistemas que distinguen mayúsculas y minúsculas.

## 3. Comandos Maven necesarios

Desde la raíz del repositorio:

```bash
cd nave_iterestelar
```

| Comando | Propósito |
| --- | --- |
| `mvn clean` | Elimina archivos compilados previamente en `target/`. |
| `mvn clean compile` | Compila las clases de producción desde cero. |
| `mvn clean package` | Compila y genera el artefacto en `target/`. |
| `mvn test` | Ejecuta pruebas detectadas por Maven, **pero no ejecuta los cinco `main()` de demostración**. |

El `pom.xml` actual no declara una clase principal en el manifiesto del JAR. Por ello, para iniciar el sistema se recomienda utilizar los comandos de la siguiente sección; **no** se presupone que `java -jar target/*.jar` sea ejecutable.

## 4. Cómo iniciar el sistema

Compilar primero desde `nave_iterestelar/`:

```bash
mvn clean compile
```

Ejecutar la demostración general (`Prueba.java`, en el paquete por defecto):

```bash
java -cp target/classes Prueba
```

Este programa crea una nave Exploradora con su tripulación y muestra comprobaciones básicas de haberes, recursos, búsqueda de tripulantes y bitácora.

También es posible ejecutar cada clase desde IntelliJ IDEA mediante la acción **Run** sobre su método `main`. Al importar el proyecto, abrir `nave_iterestelar/pom.xml` como proyecto Maven y seleccionar **JDK 26**.

## 5. Cómo ejecutar las verificaciones reproducibles

Las verificaciones se implementan como **programas Java independientes con `main()`**, sin dependencia de JUnit. Se ejecutan sobre las clases compiladas del mismo proyecto.

Después de `mvn clean compile`, ejecutar desde `nave_iterestelar/`:

```bash
# Escenario A: creación por Factory y misiones M-01, M-02 y M-03
java -cp target/classes Pruebas.EscenarioA

# Escenario B: misión rechazada por falta de recursos
java -cp target/classes Pruebas.EscenarioB

# Escenario C: transiciones válidas e inválidas del Motor Warp
java -cp target/classes Pruebas.EscenarioC

# Escenario D: contratos de recursos, rechazo y mantenimiento
java -cp target/classes Pruebas.EscenarioD

# Haberes: cuatro cargos, tres orígenes y demostración del Decorator
java -cp target/classes Pruebas.PruebaHaberes
```

| Clase | Qué demuestra | Resultado que debe comprobarse |
| --- | --- | --- |
| `Pruebas.EscenarioA` | Factory, tripulación válida, ciclo de Intercepción (M-01), Recolección (M-02) y Retorno (M-03), informes y bitácora. | Tres misiones `EXITOSA`, consumos correctos y estados finales coherentes. |
| `Pruebas.EscenarioB` | Intento de misión sin suficiente combustible. | La preparación devuelve `false`, no se genera informe y los recursos no se modifican; el motivo queda registrado. |
| `Pruebas.EscenarioC` | Patrón State en el Motor Warp. | Secuencia `Disponible → PreparandoSalto → EnWarp → Enfriamento → Disponible` y rechazo explícito de una transición inválida. |
| `Pruebas.EscenarioD` | Precondiciones, límites de capacidad, ausencia de consumos parciales y mantenimiento. | Operaciones fuera de rango rechazadas; los recursos permanecen válidos y el mantenimiento restablece el desgaste a cero. |
| `Pruebas.PruebaHaberes` | Cálculos por cargo, antigüedad y origen mediante los decoradores existentes. | Valores de sueldo correctos para los cuatro cargos y los tres subsidios; **la composición válida de conceptos distintos continúa pendiente**. |

Los escenarios lanzan `IllegalStateException` ante varias condiciones inesperadas; una ejecución que termine sin excepción es un indicador útil, **pero no implica por sí sola que estén resueltos todos los requisitos**. Además de los mensajes `[OK]`, revisar los mensajes `[PENDIENTE]` y `ADVERTENCIA`.

### Datos de referencia: Escenario A

| Tipo de nave | Combustible inicial | Energía inicial | Desgaste inicial |
| --- | ---: | ---: | ---: |
| Exploradora | 60 | 80 | 0 |
| Carguero | 100 | 60 | 0 |
| Combate | 80 | 100 | 0 |

La demostración utiliza una Exploradora y ejecuta las tres misiones con costos simplificados de la Entrega 1:

| Etapa | Combustible restante | Energía restante | Desgaste acumulado |
| --- | ---: | ---: | ---: |
| Inicio | 60 | 80 | 0 |
| Después de M-01 (−4 combustible, −5 energía, +4 desgaste) | 56 | 75 | 4 |
| Después de M-02 (−4, −5, +4) | 52 | 70 | 8 |
| Después de M-03 (−4, −0, +4) | 48 | 70 | 12 |

El `EscenarioA` incluye **tres saltos Warp** y `Nave.saltar()` incorpora un retardo de 10 segundos por salto. Por ello, esta demostración tarda aproximadamente **30 segundos**, además del tiempo de inicio del proceso.

### Evidencias de ejecución

Para guardar los resultados y asociarlos con la matriz de casos se recomienda crear los siguientes archivos de texto **tras ejecutar la versión final**:

```text
docs/
└── evidencias/
    ├── matriz-e1.md
    ├── escenario-A.txt
    ├── escenario-B.txt
    ├── escenario-C.txt
    ├── escenario-D.txt
    └── haberes.txt
```

Ejemplo de captura desde una terminal compatible con Bash:

```bash
java -cp target/classes Pruebas.EscenarioB > ../docs/evidencias/escenario-B.txt 2>&1
```

Crear previamente la carpeta `docs/evidencias/` en la raíz del repositorio. En Windows PowerShell, también es posible ejecutar los escenarios desde IntelliJ y guardar la salida de consola manualmente. Una evidencia debe indicar **qué escenario se ejecutó, cuál era el resultado esperado, cuál fue el obtenido y si se cumplió**.

## 6. Configuración local necesaria

1. Clonar o descargar el repositorio.
2. Abrir la carpeta `nave_iterestelar/` como proyecto Maven.
3. Seleccionar JDK 26 tanto para el proyecto como para Maven y recargar el `pom.xml`.
4. Situarse en la carpeta del `pom.xml` antes de compilar.
5. Ejecutar los `main()` **en procesos independientes**. Los componentes Singleton, como Capitán y Bitácora, conservan estado durante un mismo proceso Java; esto puede alterar los resultados si se reutiliza ese proceso para varias demostraciones.

No se requiere un archivo `.env`, configuración de credenciales, JDBC, H2 ni un servicio externo en la Entrega 1. Los archivos generados (`target/`, `*.class`, `*.log`) y los datos locales de H2 (`*.mv.db`, `*.trace.db`) están contemplados en los `.gitignore` del repositorio.

## 7. Componentes principales y patrones

| Componente | Responsabilidad / patrón |
| --- | --- |
| `Nave`, `NaveExploradora`, `NaveCarguero`, `NaveCombate` | Representación y validación de recursos y tripulación. |
| `NaveFactory` | **Simple Factory**: centraliza la creación de los tres tipos de nave. |
| `MotorWarp` y sus estados | **State**: define qué transiciones son válidas según el estado. |
| `Mision`, `Intercepcion`, `Recoleccion`, `Retorno` | **Template Method**: ciclo común `preparar → ejecutar → evaluar → cerrar`. |
| `AsistenteDeComandos` | Coordina acciones sobre la nave y registra eventos. |
| `Bitacora` | Registro de eventos, compartido mediante Singleton. |
| `InformeMision` | Resultado de la misión, acciones, recursos, estado final y observaciones. |
| `Tripulante`, cargos, `Haber`, `DecoratorPlaneta` y orígenes | Cálculo de haberes y aplicación del patrón **Decorator** para subsidios por origen. |

## 8. Estado de las demostraciones y observaciones conocidas

En una comprobación local del contenido de este ZIP se compilaron los **37 archivos Java** con `javac` de Java 21 y se ejecutaron individualmente las cinco clases de `Pruebas/` sin fallos de proceso. **Esta comprobación no sustituye ejecutar `mvn clean compile` y los escenarios con el JDK 26 indicado en el `pom.xml`.**

Los programas contienen advertencias que deben distinguirse de un resultado aprobado:

- **E1-10 — Informe de misión:** `EscenarioA` muestra que las tres misiones finalizan correctamente, pero los informes todavía no reflejan todas las acciones operativas; los mensajes `[PENDIENTE]` señalan esa diferencia.
- **E1-08 — Haberes / Decorator:** `PruebaHaberes` comprueba los importes y demuestra que pueden anidarse dos decoradores de origen, pero **dos orígenes en un mismo tripulante no representan una liquidación válida**. Falta demostrar una composición de conceptos remunerativos diferentes.

Estas observaciones se documentan para evitar confundir una ejecución sin excepción con el cumplimiento integral de la matriz de evaluación.

## 9. Documentación y elementos de la entrega

El repositorio debe incluir, además del código y las demostraciones, la documentación requerida por la cátedra. **El ZIP utilizado para redactar este README aún no incluye la carpeta `docs/`**, por lo que estos elementos deben incorporarse antes de etiquetar la entrega:

- Diagrama UML de clases consistente con el código.
- Documentación de contratos (precondiciones, postcondiciones e invariantes) y justificación de patrones.
- Evidencias de ejecución asociadas con la matriz de casos.
- `docs/uso-ia.md` con el uso de herramientas de inteligencia artificial, cuando corresponda.

`database/schema.sql` se solicita **recién para la Entrega 2** y no forma parte de las obligaciones de la primera entrega.

## 10. Etiqueta Git de Entrega 1

La etiqueta oficial correspondiente a esta entrega es **`entrega-e1`**. Debe apuntar al **commit exacto** que se presenta para evaluación.

Desde la raíz del repositorio, después de integrar el código, las pruebas, el README y las evidencias:

```bash
git status
git add .
git commit -m "Preparar Entrega 1 del TPG"
git tag -a entrega-e1 -m "Entrega 1 - Grupo 12"
git push origin main
git push origin entrega-e1
```

> Estos comandos asumen que la rama de entrega es `main`, que aún no se creó la etiqueta y que existe algún cambio por confirmar. Si ya existe el commit o la etiqueta, **no** los recreen automáticamente. Comprobar el estado antes de ejecutar cada paso.

En Moodle se entrega la **URL del repositorio GitHub**, de acuerdo con las indicaciones de la cátedra. Debe poder reconstruirse y ejecutarse el commit marcado por `entrega-e1` siguiendo estas instrucciones.

---

**Entrega 1 — Grupo 12 · Programación C · 2026**
