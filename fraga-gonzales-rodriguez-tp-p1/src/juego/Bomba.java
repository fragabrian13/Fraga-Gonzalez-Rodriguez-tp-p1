package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import entorno.Entorno;                                          // entorno grafico

/** Bomba dejada por un enemigo. Tiene mecha, explota y su area cubre todo el ancho de la calle. */
public class Bomba {
    private static int MECHA = 3000;                       // ms hasta que explota
    private static int DURACION_EXPLOSION = 600;           // ms que dura la explosion
    private static double LARGO_AREA = 160;                // largo del area de la explosion
    private static double TAM = 14;                        // diametro de la bomba

    private double x, y;                                         // posicion de la bomba
    private boolean vertical;       // orientacion de la calle donde esta
    private int colocada;                                        // momento (ms) en que se puso
    private boolean explotando;                                  // true cuando ya exploto
    private int inicioExplosion;                                 // momento (ms) en que empezo a explotar

    public Bomba(double x, double y, boolean vertical, int ahora) {
        this.x = x;                                              // guardo X
        this.y = y;                                              // guardo Y
        this.vertical = vertical;                                // guardo la orientacion de la calle
        this.colocada = ahora;                                   // anoto cuando se coloco
    }

    public void actualizar(int ahora) {
        // si todavia no exploto y ya paso el tiempo de la mecha...
        if (!explotando && ahora - colocada >= MECHA) {
            explotando = true;                                   // ...explota
            inicioExplosion = ahora;                             // anoto cuando empezo la explosion
        }
    }

    // Devuelve true si la explosion ya duro lo suficiente (la bomba hay que sacarla)
    public boolean terminada(int ahora) {
        return explotando && ahora - inicioExplosion >= DURACION_EXPLOSION;
    }

    /** Colision con la bomba (antes de explotar) o con su area de efecto (al explotar). */
    public boolean alcanza(double px, double py, double pw, double ph) {
        if (!explotando) {                                       // antes de explotar...
            return Colision.rectangulos(x, y, TAM, TAM, px, py, pw, ph);   // ...solo cuenta tocar la bombita
        }
        if (vertical) {                                          // explotando en calle vertical: area alta y angosta
            return Colision.rectangulos(x, y, Mapa.CALLE, LARGO_AREA, px, py, pw, ph);
        }
        // explotando en calle horizontal: area ancha y baja
        return Colision.rectangulos(x, y, LARGO_AREA, Mapa.CALLE, px, py, pw, ph);
    }

    public void dibujar(Entorno e, int ahora) {
        if (explotando) {                                        // si esta explotando...
            Color fuego = new Color(255, 110, 0);                // color naranja fuego
            if (vertical) {                                      // calle vertical: rectangulo alto
                e.dibujarRectangulo(x, y, Mapa.CALLE, LARGO_AREA, 0, fuego);
            } else {                                             // calle horizontal: rectangulo ancho
                e.dibujarRectangulo(x, y, LARGO_AREA, Mapa.CALLE, 0, fuego);
            }
        } else {                                                 // si todavia no explota...
            e.dibujarCirculo(x, y, TAM, Color.BLACK);            // dibujo la bomba negra
            if ((ahora / 200) % 2 == 0) {                        // cada 200 ms alterna true/false (parpadeo)
                e.dibujarCirculo(x, y, 6, Color.RED);            // luz roja que titila
            }
        }
    }
}
