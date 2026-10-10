# Sistema de Nave Interestelar — TPG Programación C 2026

**Grupo 12 · Facultad de Ingeniería, Universidad Nacional de Mar del Plata · Entrega 1**

Proyecto desarrollado en **Java** con **Maven** que simula el software embarcado de una nave interestelar tripulada. Comprende la creación de naves, la administración de tripulación y recursos, un Motor Warp con estados, tres misiones, una bitácora, informes de misión y liquidación de haberes.

La primera entrega se concentra en el modelo orientado a objetos. La persistencia, el universo dinámico, la concurrencia y la interfaz gráfica pertenecen a etapas posteriores.

## 1. Requisitos de ejecución

- **JDK 26**, de acuerdo con las propiedades `maven.compiler.source` y `maven.compiler.target` del `pom.xml` actual.
- **Apache Maven**, instalado y disponible en la terminal (se recomienda Maven 3.9 o posterior).
- Un entorno para ejecutar Java, por ejemplo **IntelliJ IDEA** o una terminal.
- Conexión a Internet para descargar complementos de Maven en la primera compilación, si todavía no están en la caché local.

Comprobar las herramientas:

```bash
java -version
javac -version
mvn -version
```

Maven debe utilizar un **JDK 26**. Si utiliza otro, revisar la configuración del JDK de Maven y la variable `JAVA_HOME`.

Esta entrega **no requiere** base de datos, H2, archivos `.env`, claves de acceso ni servicios externos.

## 2. Estructura del repositorio

El archivo `pom.xml` está dentro de `nave_interestelar/`, no en la raíz. La carpeta `docs/` actual también está dentro de ese módulo.

```text
TP-prograC-grupo-12-main/
├── README.md
├── .gitignore
└── nave_interestelar/
    ├── pom.xml
    ├── docs/
    │   ├── Evidencias.md
    │   ├── patrones.md
    │   ├── USO-IA.md
    │   └── [diagrama UML por incorporar]
    └── src/
        └── main/
            └── java/
                ├── Prueba.java
                ├── Pruebas/
                │   ├── EscenarioA.java
                │   ├── EscenarioB.java
                │   ├── EscenarioC.java
                │   ├── EscenarioD.java
                │   └── PruebaHaberes.java
                └── modelo/
                    ├── nave/
                    ├── Tripulacion/
                    ├── motorWarp/
                    ├── misiones/
                    ├── bitacora/
                    └── asistenteDeComandos/
```

Los nombres de paquetes, incluidos `Pruebas` y `Tripulacion`, distinguen mayúsculas y minúsculas. Deben respetarse al ejecutar el proyecto.

## 3. Comandos Maven

Abrir una terminal **en la raíz del repositorio** y entrar al módulo Maven:

```bash
cd nave_interestelar
```

Comandos principales:

```bash
mvn clean           # Elimina los archivos generados en target/
mvn clean compile   # Compila el código fuente
mvn clean package   # Compila y genera el JAR en target/
```

También puede ejecutarse `mvn test`, pero **no inicia las demostraciones**: las verificaciones de este repositorio son clases Java con su propio método `main()`, no tests JUnit.

El `pom.xml` no configura una clase principal en el manifiesto del JAR. Por eso el modo de ejecución documentado es mediante `java -cp`, y no mediante `java -jar`.

## 4. Cómo iniciar el sistema

Desde `nave_interestelar/`, compilar primero:

```bash
mvn clean compile
```

Ejecutar la demostración general:

```bash
java -cp target/classes Prueba
```

Esta clase crea una nave mediante Factory, asigna tripulantes y muestra operaciones de recursos, consultas de tripulación, haberes y registro en bitácora.

**Desde IntelliJ IDEA:** abrir `nave_interestelar/` como proyecto Maven, seleccionar JDK 26, recargar Maven y ejecutar el método `main()` de `Prueba.java`.

## 5. Verificaciones reproducibles — Entrega 1

La entrega incluye **cinco demostraciones independientes**, que se ejecutan sin JUnit. Una vez completado `mvn clean compile`, iniciar cada una desde `nave_interestelar/`:

```bash
java -cp target/classes Pruebas.EscenarioA
java -cp target/classes Pruebas.EscenarioB
java -cp target/classes Pruebas.EscenarioC
java -cp target/classes Pruebas.EscenarioD
java -cp target/classes Pruebas.PruebaHaberes
```

| Demostración | Objetivo |
| --- | --- |
| **Escenario A** | Crear los tres tipos de nave mediante Factory, preparar una tripulación válida y ejecutar **M-01 Intercepción**, **M-02 Recolección** y **M-03 Retorno**, mostrando recursos, bitácora e informes. |
| **Escenario B** | Intentar una misión sin combustible suficiente y comprobar el rechazo, la ausencia de modificaciones parciales y el registro del motivo. |
| **Escenario C** | Recorrer la secuencia de estados del Motor Warp e intentar una transición inválida. |
| **Escenario D** | Probar límites de combustible y energía, consumo inválido y mantenimiento. |
| **PruebaHaberes** | Mostrar el cálculo de haberes para los cuatro cargos, sus adicionales y los tres subsidios por origen; revisar la aplicación de Decorator. |

En cada ejecución deben compararse los valores obtenidos con los esperados. Los mensajes **`[OK]`** confirman comprobaciones específicas; los mensajes **`[PENDIENTE]`** o **`ADVERTENCIA`** señalan requisitos que deben revisarse. Que un programa termine sin lanzar una excepción no garantiza, por sí solo, que todos los requisitos estén cumplidos.

El **Escenario A** realiza tres saltos y cada uno incluye una espera de 10 segundos durante el enfriamiento; por eso puede tardar **unos 30 segundos**.

Para evitar interferencias entre objetos Singleton, se recomienda ejecutar **cada demostración en un proceso Java separado**.

Las salidas y su correspondencia con los casos de verificación se documentan por separado en **[Evidencias.md](nave_interestelar/docs/Evidencias.md)**. Este README conserva las instrucciones necesarias para reproducirlas, sin duplicar sus resultados.

## 6. Configuración local

1. Clonar o descargar el repositorio.
2. Abrir `nave_interestelar/` como proyecto Maven en el IDE, o acceder a esa carpeta desde una terminal.
3. Configurar **JDK 26** tanto para el proyecto como para Maven.
4. Recargar el `pom.xml` y ejecutar `mvn clean compile`.
5. Ejecutar `Prueba` o las clases del paquete `Pruebas` con los comandos anteriores.

Los directorios y archivos generados, como `target/`, `*.class` y los datos locales de H2 (`*.mv.db`, `*.trace.db`), no deben agregarse al repositorio; las exclusiones correspondientes están previstas en los `.gitignore`.

## 7. Diseño y patrones aplicados

| Patrón | Clases principales | Uso |
| --- | --- | --- |
| **Factory (Simple Factory)** | `NaveFactory`, `TipoNave`, subclases de `Nave` | Creación centralizada de Exploradora, Carguero y Combate. |
| **State** | `MotorWarp`, `State`, `Disponible`, `PreparandoSalto`, `EnWarp`, `Enfriamento` | Control de estados y transiciones del motor. |
| **Template Method** | `Mision`, `Intercepcion`, `Recoleccion`, `Retorno` | Ciclo común: preparar → ejecutar → evaluar → cerrar. |
| **Decorator** | `Haber`, `DecoratorPlaneta`, `Terricola`, `Marciano`, `Vulcano` | Incorporación de subsidios por origen al cálculo de haberes. |
| **Singleton** (complementario) | `Bitacora`, `Capitan` | Acceso a instancias compartidas. |

La explicación de responsabilidades, funcionamiento y decisiones de diseño se encuentra en **[patrones.md](nave_interestelar/docs/patrones.md)**. Los contratos de los métodos y las invariantes del modelo se describen también mediante comentarios **Javadoc** en el código fuente.

## 8. Documentación de la entrega

- **[Patrones de diseño](nave_interestelar/docs/patrones.md):** explicación de las implementaciones y sus responsabilidades.
- **[Evidencias](nave_interestelar/docs/Evidencias.md):** ejecuciones, resultados y relación con los escenarios de verificación.
- **[Uso de IA](nave_interestelar/docs/USO-IA.md):** registro de las herramientas utilizadas y sus aplicaciones al proyecto.
- **Diagrama de clases UML:** **en preparación**; se incorporará a `nave_interestelar/docs/` y deberá corresponderse con el código de la entrega.
- **Contratos:** documentación Javadoc de las clases y métodos principales.

`database/schema.sql` corresponde a la **Entrega 2** y no se requiere en esta etapa.

### Observaciones para la revisión final

Las demostraciones incluidas en el ZIP todavía identifican dos puntos que el equipo debe revisar antes de considerar cerrada la matriz de casos:

- **E1-10 — Informes:** comprobar que cada informe incluya las acciones operativas de la misión, y no únicamente evaluación y cierre.
- **E1-08 — Haberes:** demostrar una composición de Decorators con **conceptos remunerativos distintos**; combinar subsidios de dos orígenes en un mismo tripulante no es una liquidación válida.

Además, el archivo existente `USO-IA.md` debería renombrarse a **`uso-ia.md`** si se requiere respetar exactamente la ruta `docs/uso-ia.md` indicada por la cátedra; en ese caso, actualizar también los enlaces de este README.

## 9. Identificación de la Entrega 1 en Git

La etiqueta oficial es **`entrega-e1`** y debe apuntar al commit exacto de la versión presentada.

Después de integrar el código, las demostraciones, el UML y la documentación, ejecutar **desde la raíz del repositorio**:

```bash
git status
git add .
git commit -m "Preparar entrega 1 - Grupo 12"
git tag -a entrega-e1 -m "Entrega 1 - Grupo 12"
git push origin HEAD
git push origin entrega-e1
```

Si la etiqueta `entrega-e1` ya existe, **no volver a crearla sin verificar a qué commit apunta**.

En Moodle se entrega la URL del repositorio GitHub del grupo. La versión evaluada es la señalada por la etiqueta `entrega-e1`.
