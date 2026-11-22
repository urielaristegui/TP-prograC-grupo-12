# Registro de utilización de herramientas de Inteligencia Artificial

**Trabajo Práctico Grupal — Programación C — Grupo 12**

## 1. Objetivo del documento

Este documento registra la utilización de herramientas de inteligencia artificial generativa durante el desarrollo de las Partes 1 y 2 del Sistema de Nave Interestelar.

Se emplearon como herramientas complementarias para analizar problemas de implementación, revisar patrones de diseño, proponer alternativas, detectar errores, preparar pruebas y mejorar la documentación.

Las respuestas obtenidas no se consideraron automáticamente correctas. Las propuestas incorporadas al proyecto fueron revisadas y adaptadas según los requisitos de la cátedra y la arquitectura existente.

## 2. Herramientas utilizadas

Durante el desarrollo, el equipo utilizó herramientas como ChatGPT, Gemini, Claude y herramientas de consulta de documentación asistidas por IA.


## 3. Parte 1 — Modelo y patrones fundamentales

### Registro 1. Revisión del patrón Decorator

**Objetivo:** analizar la aplicación de Decorator en el cálculo de haberes de la tripulación, particularmente para incorporar adicionales por origen planetario.

**Componentes relacionados:** `Tripulante`, `Haber`, `DecoratorPlaneta` y las clases concretas de origen.

**Aporte de la IA:** propuestas para separar el cálculo base del cargo de los subsidios adicionales y conservar una interfaz común de remuneración.

**Intervención del equipo:** revisión y adaptación de las propuestas a la jerarquía de clases existente, manteniendo las reglas de los cargos y las restricciones del dominio.

**Verificación:** compilación y ejecución de `PruebaHaberes`, que comprueba los cuatro cargos y las combinaciones con los tres orígenes.

**Observación:** la implementación actual utiliza Decorator para el concepto de origen. La composición de conceptos salariales adicionales debe contrastarse con el alcance exacto del requisito E1-08.

### Registro 2. Revisión de los informes de misión

**Objetivo:** mejorar la representación de los resultados de las misiones y el balance de recursos.

**Componentes relacionados:** `Mision`, `InformeMision` y clases concretas de misión.

**Aporte de la IA:** alternativas para estructurar la información de los informes, registrar las acciones y presentar los resultados de forma legible.

**Intervención del equipo:** adaptación de la estructura a los datos disponibles y al ciclo de vida establecido por Template Method.

**Verificación:** ejecución de los escenarios de misión y revisión de los informes generados.

### Registro 3. Preparación de pruebas

**Objetivo:** construir escenarios de comprobación independientes que permitieran verificar contratos y comportamientos de los módulos.

**Componentes relacionados:** clases del paquete `Pruebas`.

**Aporte de la IA:** sugerencias de casos de prueba, secuencias de ejecución y condiciones que debían comprobarse.

**Intervención del equipo:** incorporación de pruebas ejecutables mediante métodos `main()` y adaptación de los casos a las clases implementadas.

**Verificación:** ejecución de escenarios de recursos, Motor Warp, haberes y operaciones principales.

## 4. Parte 2 — Integración y nuevas funcionalidades

### Registro 4. Navegación, sensores y Motor Warp

**Objetivo:** integrar los movimientos de la nave, los radares y las restricciones del Motor Warp durante la ejecución por turnos.

**Componentes relacionados:** `MotorWarp`, `ReglasNavegacion`, `Sensores` y `AsistenteDeComandos`.

**Aporte de la IA:** análisis de la secuencia de preparación, ejecución y enfriamiento, propuestas de corrección de errores y sugerencias para verificar consumos de recursos.

**Intervención del equipo:** revisión de los cambios frente al anexo de la cátedra, adaptación al patrón State existente y selección de modificaciones puntuales.

**Verificación:** compilación, ejecución de `EscenarioC` y comprobaciones adicionales sobre la secuencia del Warp.

### Registro 5. Misiones y universo dinámico

**Objetivo:** integrar M-01, M-02 y M-03 con el tablero y los elementos del universo.

**Componentes relacionados:** `Mision`, `Intercepcion`, `Recoleccion`, `Retorno`, `Universo`, `Destino` y `AsistenteDeComandos`.

**Aporte de la IA:** propuestas para organizar el flujo de misiones, analizar las interacciones del entorno, mantener coherentes las posiciones y revisar los informes.

**Intervención del equipo:** evaluación de las propuestas según el enunciado, conservando la estructura Template Method y las clases proporcionadas por la cátedra cuando correspondía.

**Verificación:** ejecución de `PruebaMisiones`, `PruebaUniverso` y pruebas manuales mediante la interfaz gráfica.

### Registro 6. Interfaz gráfica, MVC y Observer

**Objetivo:** integrar los módulos existentes en una aplicación Java Swing con actualización automática de las vistas.

**Componentes relacionados:** `JFrameMain`, paneles visuales, controladores, `Tablero`, `Observable`, `Observador` y `Bitacora`.

**Aporte de la IA:** sugerencias para separar responsabilidades, coordinar controladores y actualizar componentes gráficos a partir de eventos del modelo.

**Intervención del equipo:** integración de los paneles, conexión con los controladores existentes y adaptación de las actualizaciones al hilo de eventos de Swing.

**Verificación:** compilación y ejecución de las pantallas, además de pruebas manuales sobre el tablero, las misiones y el panel de recursos.

### Registro 7. Persistencia y concurrencia

**Objetivo:** implementar el registro de usuarios mediante DAO/DTO y el universo paralelo mediante Productor–Consumidor.

**Componentes relacionados:** `UsuarioDAO`, `UsuarioDTO`, `ArchivoUsuarioDao`, `AutenticacionService`, `PlataformaDeCarga`, `GeneradoraDeEnergia` y `NaveConsumidora`.

**Aporte de la IA:** consultas sobre organización de capas, validaciones de autenticación y uso correcto de monitores de Java.

**Intervención del equipo:** implementación y revisión de las operaciones de registro y acceso, así como de la sincronización entre productores y consumidores.

**Verificación:** pruebas de persistencia de credenciales y ejecución concurrente de depósitos y retiros en la plataforma compartida.

### Registro 8. Auditoría del proyecto y documentación

**Objetivo:** identificar diferencias entre la implementación y los requerimientos, mejorar las pruebas y actualizar la documentación.

**Componentes relacionados:** código del proyecto, README, documentación de patrones, evidencias e informes.

**Aporte de la IA:** revisión de código, identificación de posibles defectos, comparación con requisitos y propuestas de redacción.

**Intervención del equipo:** revisión de los resultados, selección de correcciones relevantes y descarte de modificaciones que no se consideraron necesarias para el alcance del trabajo.

**Verificación:** compilación, ejecución de pruebas existentes, preparación de pruebas adicionales y revisión de la aplicación gráfica.

## 5. Criterio de utilización y validación

El equipo consideró las respuestas de IA como propuestas de asistencia y no como evidencia suficiente de corrección.

Para incorporar cambios se tomaron como referencia:

- Los enunciados y anexos de la cátedra.
- Las interfaces y responsabilidades establecidas en el proyecto.
- Los contratos, invariantes y restricciones de cada clase.
- Los resultados de compilación y pruebas.
- La integración con los demás módulos del sistema.

También se analizaron alternativas que finalmente no se incorporaron, cuando no representaban un requerimiento necesario o introducían complejidad adicional.

La utilización de IA no reemplazó la necesidad de revisar el comportamiento del programa ni de justificar los patrones y las decisiones de diseño durante la defensa.

## 6. Alcance del registro

Este documento resume las consultas y actividades relevantes de asistencia con IA.

Cuando se dispone de los intercambios originales, pueden conservarse enlaces, fechas o fragmentos de prompts como evidencia complementaria. Las descripciones anteriores representan los objetivos de las consultas y no deben interpretarse como transcripciones literales de prompts.

El equipo asume la responsabilidad por la versión final del código, la documentación presentada y la comprobación de los resultados.