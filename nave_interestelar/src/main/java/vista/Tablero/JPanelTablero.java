
package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import modelo.destinoytablero.Observador;
import modelo.destinoytablero.Posicion;
import modelo.elementos.Asteroide;
import modelo.elementos.ElementoDelUniverso;
import modelo.elementos.EstacionDeCarga;
import modelo.nave.Nave;
import modelo.nave.NaveCarguero;
import modelo.nave.NaveCombate;
import modelo.nave.NaveExploradora;
import modelo.universo.Universo;
import modelo.elementos.Gema;
import modelo.elementos.Anomalia;
/**
 * Vista grafica del tablero del universo.
 * Muestra los elementos, la nave y el objetivo.
 *
 * Implementa Observer para recibir actualizaciones
 * automaticas sin modificar el modelo.
 *
 * Invariantes:
 * - universo != null.
 * - celdas tiene las dimensiones del tablero.
 * - La vista no ejecuta movimientos ni interacciones.
 */
public class JPanelTablero extends JPanel implements Observador {

    private static final int MARGEN = 34;
    private static final int ALTO_LEYENDA = 52;

    private static final Color FONDO = new Color(10, 18, 35);
    private static final Color CASILLA_1 = new Color(22, 38, 61);
    private static final Color CASILLA_2 = new Color(25, 43, 68);
    private static final Color GRILLA = new Color(56, 77, 105);

    private static final Color COLOR_NAVE =
            new Color(75, 186, 255);
    private static final Color COLOR_OBJETIVO =
            new Color(250, 202, 83);
    private static final Color COLOR_ASTEROIDE =
            new Color(159, 170, 188);
    private static final Color COLOR_ESTACION =
            new Color(64, 217, 159);
    private static final Color COLOR_ENEMIGA =
            new Color(247, 111, 119);
    private static final Color COLOR_GEMA =
            new Color(255, 214, 70);
    private static final Color COLOR_ANOMALIA =
            new Color(177, 122, 252);

    private enum TipoCelda {
        VACIA,
        ASTEROIDE,
        ESTACION,
        EXPLORADORA,
        CARGUERO,
        COMBATE,
        GEMA,
        ANOMALIA,
        OTRO
    }

    private final Universo universo;

    private TipoCelda[][] celdas;
    private Posicion posicionNave;
    private Posicion objetivo;
    private boolean naveDestruida;

    /**
     * Crea el panel y se registra como observador.
     *
     * Pre:
     * - universo != null.
     * - Se invoca desde el hilo de Swing (EDT).
     *
     * Post:
     * - Inicializa la vista del tablero.
     * - Registra el panel como observador.
     * - Muestra el estado inicial del universo.
     *
     * @param universo modelo a representar
     * @throws IllegalArgumentException si universo es null
     */
    public JPanelTablero(Universo universo) {

        if (universo == null) {
            throw new IllegalArgumentException(
                    "El universo no puede ser null"
            );
        }

        this.universo = universo;

        setBackground(FONDO);
        setOpaque(true);
        setPreferredSize(new Dimension(690, 720));

        setBorder(
                BorderFactory.createTitledBorder(
                        "Tablero del universo"
                )
        );

        actualizarInstantanea();

        universo.getTablero().agregarObservador(this);
    }

    /**
     * Recibe notificaciones del Tablero.
     * Compatible con la version original del proyecto.
     *
     * Pre: ninguna.
     * Post:
     * - Programa la actualizacion en el EDT.
     * - No modifica el modelo.
     */
    public void actualizar() {
        programarActualizacion();
    }

    /**
     * Recibe notificaciones del Tablero.
     * Compatible con la version nueva de la catedra.
     *
     * Pre: ninguna.
     * Post:
     * - Programa la actualizacion en el EDT.
     * - No modifica el modelo.
     *
     * @param mensaje descripcion opcional del cambio
     */
    public void actualizar(String mensaje) {
        programarActualizacion();
    }

    /**
     * Fuerza una actualizacion de la vista.
     * Util cuando cambian recursos de la nave sin
     * que el tablero emita una notificacion.
     *
     * Pre: ninguna.
     * Post:
     * - Encola una actualizacion en el EDT.
     * - No modifica el universo.
     */
    public void refrescar() {
        programarActualizacion();
    }

    /**
     * Elimina el panel de los observadores del tablero.
     *
     * Pre: ninguna.
     * Post:
     * - El panel deja de recibir notificaciones nuevas.
     * - No modifica los elementos del tablero.
     */
    public void desconectar() {
        universo.getTablero().quitarObservador(this);
    }

    /**
     * Programa la actualizacion grafica.
     *
     * Pre: ninguna.
     * Post:
     * - Se encola la lectura del modelo y el repintado.
     * - No modifica el universo.
     */
    private void programarActualizacion() {

        SwingUtilities.invokeLater(() -> {
            actualizarInstantanea();
            repaint();
        });
    }

    /**
     * Copia el estado del modelo para representarlo.
     *
     * Pre: universo y tablero inicializados.
     * Post:
     * - Actualiza los datos visuales del panel.
     * - No modifica el modelo.
     */
    private void actualizarInstantanea() {

        ElementoDelUniverso[][] elementos =
                universo.getTablero().verTablero();

        TipoCelda[][] nueva =
                new TipoCelda[elementos.length][];

        for (int fila = 0; fila < elementos.length; fila++) {

            nueva[fila] =
                    new TipoCelda[elementos[fila].length];

            for (int columna = 0;
                 columna < elementos[fila].length;
                 columna++) {

                nueva[fila][columna] =
                        clasificar(elementos[fila][columna]);
            }
        }

        celdas = nueva;

        posicionNave =
                universo.getTablero().getPosicionNave();

        objetivo = universo.getObjetivo();

        naveDestruida = universo.getNave().estaDestruida();
    }

    /**
     * Determina la representacion de un elemento.
     *
     * Pre: ninguna; elemento puede ser null.
     * Post: no modifica el elemento.
     *
     * @param elemento elemento del tablero
     * @return tipo visual correspondiente
     */
    private TipoCelda clasificar(ElementoDelUniverso elemento) {

        if (elemento == null) {
            return TipoCelda.VACIA;
        }
        if (elemento instanceof Gema gema) {
            return gema.estaRecolectada()
                    ? TipoCelda.VACIA
                    : TipoCelda.GEMA;
        }

        if (elemento instanceof Anomalia) {
            return TipoCelda.ANOMALIA;
        }
        if (elemento instanceof Asteroide) {
            return TipoCelda.ASTEROIDE;
        }

        if (elemento instanceof EstacionDeCarga) {
            return TipoCelda.ESTACION;
        }

        if (elemento instanceof NaveExploradora) {
            return TipoCelda.EXPLORADORA;
        }

        if (elemento instanceof NaveCarguero) {
            return TipoCelda.CARGUERO;
        }

        if (elemento instanceof NaveCombate) {
            return TipoCelda.COMBATE;
        }

        if (elemento instanceof Nave) {
            return TipoCelda.OTRO;
        }

        return TipoCelda.OTRO;
    }

    /**
     * Dibuja el tablero y sus elementos.
     *
     * Pre:
     * - g != null.
     * - celdas inicializada.
     *
     * Post:
     * - Dibuja la grilla y sus elementos.
     * - Representa la nave y el objetivo.
     * - No modifica el modelo.
     *
     * @param g contexto grafico de Swing
     */
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (celdas == null || celdas.length == 0) {
                return;
            }

            int filas = celdas.length;
            int columnas = celdas[0].length;

            int disponibleAncho =
                    getWidth() - 2 * MARGEN;

            int disponibleAlto =
                    getHeight() - 2 * MARGEN - ALTO_LEYENDA;

            int lado = Math.min(
                    disponibleAncho / columnas,
                    disponibleAlto / filas
            );

            if (lado < 4) {
                return;
            }

            int ancho = columnas * lado;
            int alto = filas * lado;

            int inicioX = (getWidth() - ancho) / 2;

            int inicioY = MARGEN + Math.max(
                    0, (disponibleAlto - alto) / 2
            );

            // DIBUJAR CASILLAS Y ELEMENTOS

            for (int fila = 0; fila < filas; fila++) {

                for (int columna = 0;
                     columna < columnas;
                     columna++) {

                    int x = inicioX + columna * lado;
                    int y = inicioY + fila * lado;

                    g2.setColor(
                            (fila + columna) % 2 == 0
                                    ? CASILLA_1 : CASILLA_2
                    );

                    g2.fillRect(x, y, lado, lado);

                    g2.setColor(GRILLA);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRect(x, y, lado, lado);

                    dibujarElemento(
                            g2,
                            celdas[fila][columna],
                            x, y, lado
                    );
                }
            }

            // DIBUJAR OBJETIVO

            if (objetivo != null) {
                dibujarObjetivo(
                        g2, objetivo,
                        inicioX, inicioY, lado
                );
            }

            // DIBUJAR NAVE PRINCIPAL

            if (posicionNave != null) {
                dibujarNave(
                        g2, posicionNave,
                        inicioX, inicioY, lado
                );
            }

            dibujarEjes(
                    g2, inicioX, inicioY,
                    lado, filas, columnas
            );

            dibujarLeyenda(
                    g2, inicioX,
                    inicioY + alto + 23
            );

        } finally {
            g2.dispose();
        }
    }

    /**
     * Dibuja el contenido de una casilla.
     *
     * Pre: g2 != null y lado > 0.
     * Post:
     * - Dibuja el elemento correspondiente.
     * - No modifica el modelo.
     *
     * @param g2 contexto grafico
     * @param tipo categoria del elemento
     * @param x coordenada horizontal en pantalla
     * @param y coordenada vertical en pantalla
     * @param lado dimension de la casilla
     */
    private void dibujarElemento(
            Graphics2D g2,
            TipoCelda tipo,
            int x, int y, int lado) {

        int margen = Math.max(2, lado / 5);
        int centroX = x + lado / 2;
        int centroY = y + lado / 2;

        switch (tipo) {

            case ASTEROIDE:

                g2.setColor(COLOR_ASTEROIDE);

                g2.fillOval(
                        x + margen, y + margen,
                        lado - 2 * margen,
                        lado - 2 * margen
                );

                g2.setColor(new Color(90, 100, 116));

                g2.fillOval(
                        centroX - lado / 7,
                        centroY - lado / 8,
                        Math.max(2, lado / 5),
                        Math.max(2, lado / 5)
                );

                break;

            case GEMA:

                g2.setColor(COLOR_GEMA);

                g2.fillOval(
                        x + margen, y + margen,
                        lado - 2 * margen,
                        lado - 2 * margen
                );

                break;

            case ANOMALIA:

                g2.setColor(COLOR_ANOMALIA);

                g2.setStroke(
                        new BasicStroke(Math.max(2f, lado / 8f))
                );

                g2.drawOval(
                        x + margen, y + margen,
                        lado - 2 * margen,
                        lado - 2 * margen
                );

                g2.fillOval(
                        centroX - lado / 8,
                        centroY - lado / 8,
                        Math.max(3, lado / 4),
                        Math.max(3, lado / 4)
                );

                break;

            case ESTACION:

                g2.setColor(COLOR_ESTACION);

                g2.fillRoundRect(
                        x + margen, y + margen,
                        lado - 2 * margen,
                        lado - 2 * margen,
                        5, 5
                );

                g2.setColor(FONDO);

                g2.setStroke(
                        new BasicStroke(Math.max(2f, lado / 10f))
                );

                g2.drawLine(
                        centroX - lado / 5, centroY,
                        centroX + lado / 5, centroY
                );

                g2.drawLine(
                        centroX, centroY - lado / 5,
                        centroX, centroY + lado / 5
                );

                break;

            case EXPLORADORA:
                dibujarEnemiga(g2, x, y, lado, "E");
                break;

            case CARGUERO:
                dibujarEnemiga(g2, x, y, lado, "C");
                break;

            case COMBATE:
                dibujarEnemiga(g2, x, y, lado, "B");
                break;

            case OTRO:
                dibujarEnemiga(g2, x, y, lado, "?");
                break;

            default:
                break;
        }
    }

    /**
     * Dibuja una nave enemiga.
     *
     * Pre: g2 != null y lado > 0.
     * Post: representa la nave con una letra.
     *
     * @param g2 contexto grafico
     * @param x posicion horizontal
     * @param y posicion vertical
     * @param lado tamaño de la casilla
     * @param letra identificador del tipo de nave
     */
    private void dibujarEnemiga(
            Graphics2D g2,
            int x, int y, int lado,
            String letra) {

        g2.setColor(COLOR_ENEMIGA);

        int margen = Math.max(2, lado / 6);

        g2.fillRoundRect(
                x + margen, y + margen,
                lado - 2 * margen,
                lado - 2 * margen,
                6, 6
        );

        g2.setColor(FONDO);

        g2.setFont(
                getFont().deriveFont(
                        Font.BOLD,
                        Math.max(9f, lado * 0.42f)
                )
        );

        FontMetrics fm = g2.getFontMetrics();

        g2.drawString(
                letra,
                x + (lado - fm.stringWidth(letra)) / 2,
                y + (lado + fm.getAscent()
                        - fm.getDescent()) / 2
        );
    }

    /**
     * Resalta la coordenada del objetivo.
     *
     * Pre: posicion != null y dentro del tablero.
     * Post: dibuja el objetivo sin modificarlo.
     */
    private void dibujarObjetivo(
            Graphics2D g2,
            Posicion posicion,
            int inicioX, int inicioY,
            int lado) {

        int x = inicioX + posicion.getY() * lado;
        int y = inicioY + posicion.getX() * lado;

        g2.setColor(COLOR_OBJETIVO);

        g2.setStroke(
                new BasicStroke(Math.max(2f, lado / 10f))
        );

        g2.drawRect(
                x + 3, y + 3,
                lado - 6, lado - 6
        );
    }

    /**
     * Dibuja la nave principal.
     *
     * Pre: posicion != null y dentro del tablero.
     * Post:
     * - Representa la nave sobre su casilla.
     * - La dibuja gris si esta destruida.
     */
    private void dibujarNave(
            Graphics2D g2,
            Posicion posicion,
            int inicioX, int inicioY,
            int lado) {

        int x = inicioX + posicion.getY() * lado;
        int y = inicioY + posicion.getX() * lado;

        Polygon figura = new Polygon(
                new int[] {
                        x + lado / 2,
                        x + lado / 5,
                        x + 4 * lado / 5
                },
                new int[] {
                        y + lado / 6,
                        y + 4 * lado / 5,
                        y + 4 * lado / 5
                },
                3
        );

        g2.setColor(
                naveDestruida ? Color.GRAY : COLOR_NAVE
        );

        g2.fillPolygon(figura);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(figura);
    }

    /**
     * Dibuja referencias numericas en la grilla.
     *
     * Pre: filas y columnas mayores que cero.
     * Post: dibuja indices cada cinco casillas.
     */
    private void dibujarEjes(
            Graphics2D g2,
            int inicioX, int inicioY,
            int lado, int filas, int columnas) {

        g2.setColor(new Color(188, 202, 219));

        g2.setFont(
                new Font(Font.MONOSPACED, Font.PLAIN, 11)
        );

        for (int columna = 0;
             columna < columnas;
             columna += 5) {

            g2.drawString(
                    Integer.toString(columna),
                    inicioX + columna * lado + 2,
                    inicioY - 9
            );
        }

        for (int fila = 0; fila < filas; fila += 5) {

            g2.drawString(
                    Integer.toString(fila),
                    inicioX - 25,
                    inicioY + fila * lado + lado / 2 + 4
            );
        }
    }


    /**
     * Dibuja las referencias de los elementos.
     *
     * Pre: g2 != null.
     * Post: muestra la leyenda sin modificar el modelo.
     */
    private void dibujarLeyenda(Graphics2D g2, int x, int y) {

        g2.setFont(
                new Font(Font.SANS_SERIF, Font.PLAIN, 11)
        );

        String[] textos = {
                "Tu nave",
                "Asteroide",
                "Estacion",
                "Enemigas",
                "Gema",
                "Anomalia",
                "Objetivo"
        };

        Color[] colores = {
                COLOR_NAVE,
                COLOR_ASTEROIDE,
                COLOR_ESTACION,
                COLOR_ENEMIGA,
                COLOR_GEMA,
                COLOR_ANOMALIA,
                COLOR_OBJETIVO
        };

        int actual = x;
        int filaY = y;

        for (int i = 0; i < textos.length; i++) {

            int itemAncho =
                    g2.getFontMetrics().stringWidth(textos[i]) + 32;

            if (actual + itemAncho > getWidth() - MARGEN) {
                actual = x;
                filaY += 19;
            }

            g2.setColor(colores[i]);
            g2.fillRect(actual, filaY - 10, 10, 10);

            g2.setColor(Color.WHITE);
            g2.drawString(textos[i], actual + 14, filaY);

            actual += itemAncho;
        }
    }

}
