

\=== ESCENARIO A: EJECUCION CORRECTA \===

\--- 1\. CREACION DE NAVES \---  
Exploradora: combustible=60, energia=80  
Carguero: combustible=100, energia=60  
Combate: combustible=80, energia=100

\--- 2\. TRIPULACION \---  
Tripulantes: 5  
Tripulacion valida: true  
Estado inicial: Combustible: 60, Energía: 80, Desgaste: 0

\--- 3\. M-01: INTERCEPCION Y ASISTENCIA \---  
Mision ejecutada: Intercepcion  
Resultado: EXITOSA  
Recursos consumidos: Combustible: 4, Energía: 5, Desgaste: 4  
Estado final (informe): Combustible: 56, Energía: 75, Desgaste: 4  
Acciones principales:  
  \- Intercepcion: evaluación \-\> EXITOSA  
  \- Intercepcion: cerrada, resultado EXITOSA  
Observaciones: (sin observaciones)  
\[OK\] M-01: resultado, recursos e informe coherentes  
\[PENDIENTE\] No figura la acción operativa: M1 Intercepción

\--- 4\. M-02: RECOLECCION \---  
Mision ejecutada: Recoleccion  
Resultado: EXITOSA  
Recursos consumidos: Combustible: 4, Energía: 5, Desgaste: 4  
Estado final (informe): Combustible: 52, Energía: 70, Desgaste: 8  
Acciones principales:  
  \- Recoleccion: evaluación \-\> EXITOSA  
  \- Recoleccion: cerrada, resultado EXITOSA  
Observaciones: (sin observaciones)  
\[OK\] M-02: resultado, recursos e informe coherentes  
\[PENDIENTE\] No figura la acción operativa: M2 Recolección

\--- 5\. M-03: RETORNO SEGURO \---  
Mision ejecutada: Retorno  
Resultado: EXITOSA  
Recursos consumidos: Combustible: 4, Energía: 0, Desgaste: 4  
Estado final (informe): Combustible: 48, Energía: 70, Desgaste: 12  
Acciones principales:  
  \- Retorno: evaluación \-\> EXITOSA  
  \- Retorno: cerrada, resultado EXITOSA  
Observaciones: (sin observaciones)  
\[OK\] M-03: resultado, recursos e informe coherentes  
\[PENDIENTE\] No figura la acción operativa: M3 Retorno

\--- 6\. BITACORA COMPLETA \---  
M1 Intercepción: aproximando al objetivo  
Intercepcion: evaluación \-\> EXITOSA  
preparando salto  
entrando en warp  
iniciando enfriamiento  
volviendo a disponible  
Intercepcion: cerrada, resultado EXITOSA  
M2 Recolección: recolectando muestra en el punto de interés  
Recoleccion: evaluación \-\> EXITOSA  
preparando salto  
entrando en warp  
iniciando enfriamiento  
volviendo a disponible  
Recoleccion: cerrada, resultado EXITOSA  
Retorno: evaluación \-\> EXITOSA  
preparando salto  
entrando en warp  
iniciando enfriamiento  
volviendo a disponible  
Retorno: cerrada, resultado EXITOSA

\--- 7\. BALANCE DE LA DEMOSTRACION \---  
\[OK\] Las tres misiones finalizaron EXITOSAS con recursos correctos  
\[PENDIENTE E1-10\] El informe no refleja todas las acciones operativas de alguna misión. Revisar Mision.cerrar(), AsistenteDeComandos.eventosDe() y Retorno.ejecutar().  
\=== FIN DEL ESCENARIO A \===

\=== DEMOSTRACION DE HABERES (DECORATOR) \===

\--- 1\. CUATRO CARGOS \---  
Capitan: base \+ antiguedad (+ consejos en el Consejero) \= 1400.0 PG (esperado: 1400.0)  
Consejero: base \+ antiguedad (+ consejos en el Consejero) \= 664.0 PG (esperado: 664.0)  
Teniente: base \+ antiguedad (+ consejos en el Consejero) \= 424.0 PG (esperado: 424.0)  
Alferez: base \+ antiguedad (+ consejos en el Consejero) \= 202.0 PG (esperado: 202.0)

\--- 2\. ORIGENES (12 COMBINACIONES) \---  
Capitan | Terricola (+20): 1420.0 | Marciano (+18): 1418.0 | Vulcano (+30): 1430.0  
Consejero | Terricola (+20): 684.0 | Marciano (+18): 682.0 | Vulcano (+30): 694.0  
Teniente | Terricola (+20): 444.0 | Marciano (+18): 442.0 | Vulcano (+30): 454.0  
Alferez | Terricola (+20): 222.0 | Marciano (+18): 220.0 | Vulcano (+30): 232.0

\--- 3\. COMPOSICION TECNICA DE DOS DECORADORES \---  
Alferez: 202.0 PG  
Cadena Alferez \+ Terricola \+ Marciano: 240.0 PG (esperado: 240\)  
ADVERTENCIA: el ejemplo solo demuestra anidamiento tecnico. Una persona no deberia tener dos origenes a la vez.  
PENDIENTE E1-08: agregar y demostrar un decorador de otro concepto remunerativo.

\[OK\] Calculados los 4 cargos y los 3 subsidios sin usar JUnit  
\=== FIN DE LA DEMOSTRACION DE HABERES \===

\=== ESCENARIO B: RECURSOS INSUFICIENTES \===

\--- 1\. PREPARACION \---  
Tripulacion valida: true  
Antes: Combustible: 60, Energía: 80, Desgaste: 0

\--- 2\. INTENTAR MISION SIN COMBUSTIBLE SUFICIENTE \---  
Mision completada: false (esperado: false)  
Informe generado: null (esperado: null; la mision no llego al cierre)

\--- 3\. COMPROBAR AUSENCIA DE CAMBIOS PARCIALES \---  
Despues: Combustible: 60, Energía: 80, Desgaste: 0  
Recursos intactos: true

\--- 4\. BITACORA \---  
Intercepcion: preparación fallida, misión cancelada

\[OK\] Rechazo registrado sin cambios parciales  
\=== FIN DEL ESCENARIO B \===

\=== ESCENARIO C: MOTOR WARP (STATE) \===

\--- 1\. ESTADO INICIAL \---  
Actual: Disponible

\--- 2\. TRANSICION INVALIDA \---  
Se intento entrar en Warp directamente desde Disponible.  
Actual: Disponible (esperado: Disponible)  
Evento de rechazo: ERROR: no se puede entrar en warp sin preparar el salto

\--- 3\. SECUENCIA VALIDA \---  
Preparar: PreparandoSalto  
Entrar en Warp: EnWarp  
Enfriar: Enfriamento  
Restablecer: Disponible

\--- 4\. BITACORA \---  
ERROR: no se puede entrar en warp sin preparar el salto  
preparando salto  
entrando en warp  
iniciando enfriamiento  
volviendo a disponible

\[OK\] Transicion invalida registrada y secuencia State completa  
\=== FIN DEL ESCENARIO C \===

\=== ESCENARIO D: CONTRATOS INVALIDOS \===

\--- 1\. ESTADO INICIAL \---  
Combustible: 60  
Energia: 80  
Desgaste: 0

\--- 2\. CARGA EXCESIVA DE COMBUSTIBLE \---  
Rechazo correcto: La carga supera la capacidad máxima de combustible  
Combustible tras rechazo: 60 (esperado: 60\)

\--- 3\. CARGA EXCESIVA DE ENERGIA \---  
Rechazo correcto: La carga supera la capacidad máxima de energía  
Energia tras rechazo: 80 (esperado: 80\)

\--- 4\. CONSUMO INVALIDO: NO HAY CAMBIOS PARCIALES \---  
Rechazo correcto: Combustible insuficiente

\--- 5\. ESTADO FINAL \---  
Combustible: 60 (esperado: 60\)  
Energia: 80 (esperado: 80\)  
Desgaste: 0 (esperado: 0\)

\--- 6\. MANTENIMIENTO \---  
Desgaste antes: 80  
Requiere mantenimiento: true  
Desgaste despues: 0  
Combustible y energia despues: 55 / 75

\[OK\] Contratos, atomicidad y mantenimiento verificados  
\=== FIN DEL ESCENARIO D \===

## **Escenario A — Ejecución correcta**

**Qué prueba:** el ciclo completo de las tres misiones (M-01, M-02, M-03) sobre una nave creada con Factory y tripulación válida: preparar → ejecutar → evaluar → cerrar, incluyendo el salto por el Motor Warp y la generación del InformeMision.

**Resultado:** las tres misiones terminan EXITOSA, con recursos consumidos y estado final coherentes. Sin embargo, el informe de las tres misiones no incluye la acción operativa correspondiente (por ejemplo, "M1 Intercepción: aproximando al objetivo") entre las Acciones principales, aunque esa línea sí existe en la Bitácora completa.

**Causa:** dos causas distintas.

* **M-01 y M-02:** el filtro `AsistenteDeComandos.eventosDe(prefijo)` busca el nombre de clase (ej. "Intercepcion", sin tilde) como substring dentro de los textos de la Bitácora. El evento real se registra con tilde ("M1 Intercepción..."). Como "Intercepcion" no es substring de "Intercepción", el filtro no lo encuentra.  
* **M-03:** `Retorno.ejecutar()` todavía no llama a `AC.registrarEvento(...)` (queda un TODO pendiente), por lo que no hay ninguna línea de acción operativa para filtrar.

## **Demostración de Haberes (Decorator)**

**Qué prueba:** el cálculo de sueldo para los cuatro cargos (Capitán, Consejero, Teniente, Alférez) con antigüedad, las doce combinaciones cargo × origen (Terrícola / Marciano / Vulcano), y una composición de dos decoradores encadenados.

**Resultado:** los cuatro cargos y las doce combinaciones dan los valores esperados. La cadena de dos decoradores (Alférez \+ Terrícola \+ Marciano) también da el valor esperado (240 PG), pero queda marcada como advertencia de diseño.

**Causa:** el ejemplo de dos decoradores combina dos orígenes planetarios a la vez, lo cual no tiene sentido de dominio — una persona no puede tener dos orígenes. Es una demostración válida del mecanismo técnico del patrón, pero no de un caso real de negocio. Falta agregar un decorador de otro concepto remunerativo (no otro origen) para una demostración con sentido completo, tal como pide E1-08.

## **Escenario B — Recursos insuficientes**

**Qué prueba:** que `preparar()` detiene el ciclo antes de ejecutar cualquier cambio, cuando la nave no tiene combustible suficiente para la misión.

**Resultado:** `director()` devuelve `false`, el informe queda en `null` (la misión nunca llegó a `cerrar()`), y los recursos de la nave permanecen intactos.

**Causa:** no aplica — el escenario confirma el comportamiento esperado, sin desvíos.

## **Escenario C — Motor Warp (State)**

**Qué prueba:** una transición inválida del motor (entrar en warp sin pasar antes por "preparando salto") y la secuencia completa válida de los cuatro estados.

**Resultado:** la transición inválida se rechaza y queda registrada en la bitácora. La secuencia válida recorre Disponible → PreparandoSalto → EnWarp → Enfriamiento → Disponible.

**Causa / punto a confirmar:** tanto acá como en el Escenario A, el motor pasa realmente por el estado Enfriamiento en cada salto exitoso. La aclaración del TP indicaba que, al no modelarse el paso del tiempo todavía, el salto debía volver a Disponible directamente, sin usar Enfriamiento como paso intermedio (el estado debía existir en el código pero no usarse en esta etapa). Conviene confirmar con el compañero que armó `Nave.saltar()` si este comportamiento fue un cambio de criterio deliberado o quedó así por otro motivo.

## **Escenario D — Contratos inválidos**

**Qué prueba:** cuatro invariantes de `Nave`: carga de combustible por encima de la capacidad máxima, carga de energía por encima de la capacidad máxima, consumo de recursos sin disponibilidad suficiente, y la operación de mantenimiento.

**Resultado:** las tres cargas/consumos inválidos se rechazan sin modificar el estado de la nave (atomicidad conservada). El mantenimiento resetea el desgaste a 0 sin tocar combustible ni energía.

**Causa:** no aplica — el escenario confirma el comportamiento esperado.

