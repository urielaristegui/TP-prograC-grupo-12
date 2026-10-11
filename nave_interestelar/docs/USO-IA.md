#### **Registro 1: Patrón Decorator para Tripulantes:**

* **Herramienta utilizada:** Gemini 3.5  
* **Propósito de la consulta:** Revisar la correcta aplicación del patrón **Decorator** adaptado a la clase abstracta Tripulante sin romper el encapsulamiento.  
* **Componente afectado:** Paquete Tripulacion (Tripulante.java, DecoratorPlaneta.java, clases concretas de origen).  
* **Resultado aprovechado:** Propuesta de interfaz/clase base y la estructura para encadenar decoradores de haberes.  
* **Revisión / Modificación del equipo:** Se adaptó la solución sugerida para que herede correctamente y se implementó de forma manual en el código.  
* **Verificación del resultado:** Se comprobó mediante compilación limpia con Maven (mvn clean compile) y ejecución de pruebas de cálculo en el main de demostración.

#### **Registro 2: Simplificación de InformeMision:**

* **Herramienta utilizada:** Gemini (v3.5 / v3.6) y GPT 6  
* **Propósito de la consulta:** Corregir y simplificar la estructura del informe de misión manteniendo la compatibilidad con el resto del núcleo.  
* **Componente afectado:** Clase InformeMision y su método toString().  
* **Resultado aprovechado:** Formato simplificado del reporte basado en el flujo de la misión y los recursos consumidos.  
* **Revisión / Modificación del equipo:** Se validó en grupo que la solución fuera compatible con el requerimiento **E1-10** antes de escribirla en el repositorio.  
* **Verificación del resultado:** Se validó visualmente el formato de salida impreso por el flujo de ejecución de las misiones.

**Registro 3: generación de Pruebas.java:** 

* **Herramienta utilizada:** GPT 6  
* **Propósito de la consulta:** generar .java con metodo main para probar los métodos que seran utilizados en los distintos escenarios y revisar su correcta ejecución.  
* **Componente afectado:** carpeta pruebas.  
* **Resultado aprovechado:** ejecución de .java en las IDE.  
* **Revisión / Modificación del equipo:** agregado de pruebas.  
* **Verificación del resultado:** Se validó que se instancien correctamente las clases y el correcto llamado a los métodos de cada una.

