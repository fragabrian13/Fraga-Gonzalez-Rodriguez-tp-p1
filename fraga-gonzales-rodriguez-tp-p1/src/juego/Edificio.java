package juego;                                                  // paquete del juego

import java.awt.Color;                                           // para los colores de los dibujos
import entorno.Entorno;                                          // el entorno grafico que nos dieron (jar)

/** Un edificio del mapa: puede ser una casa o un negocio. */
public class Edificio {
    private double x, y;          // posicion del centro del edificio
    private double ancho, alto;   // tamanio del edificio
    private boolean esNegocio;    // true = negocio, false = casa

    // Constructor: guarda los datos que recibe en los atributos
    public Edificio(double x, double y, double ancho, double alto, boolean esNegocio) {
        this.x = x;                                              // guardo la posicion X
        this.y = y;                                              // guardo la posicion Y
        this.ancho = ancho;                                      // guardo el ancho
        this.alto = alto;                                        // guardo el alto
        this.esNegocio = esNegocio;                              // guardo si es negocio o casa
    }

    // Dibuja el edificio en pantalla
    public void dibujar(Entorno e) {
        if (esNegocio) {                                         // si es un negocio...
            e.dibujarRectangulo(x, y, ancho, alto, 0, new Color(255, 160, 0));                   // cuerpo naranja
            e.dibujarRectangulo(x, y - alto / 2 + 5, ancho, 10, 0, new Color(200, 30, 30));      // toldo rojo arriba
        } else {                                                 // si es una casa...
            e.dibujarRectangulo(x, y, ancho, alto, 0, new Color(190, 70, 50));                   // cuerpo marron
            e.dibujarRectangulo(x, y, ancho - 20, alto - 16, 0, new Color(150, 40, 30));         // "techo" mas oscuro adentro
        }
    }

    /** Indica si un punto (por ejemplo, el mouse) esta sobre el edificio. */
    public boolean contiene(double px, double py) {
        // El punto esta adentro si su distancia al centro es menor a la mitad del ancho (en X) y del alto (en Y)
        return Colision.valorAbsoluto(px - x) <= ancho / 2 && Colision.valorAbsoluto(py - y) <= alto / 2;
    }

    /** Distancia al cuadrado hasta un punto (se compara contra el radio al cuadrado, sin raiz). */
    public double distanciaCuadradaA(double px, double py) {
        return (px - x) * (px - x) + (py - y) * (py - y);        // Pitagoras sin la raiz cuadrada
    }

    public double getX() { return x; }                           // devuelve la X del centro
    public double getY() { return y; }                           // devuelve la Y del centro
    public boolean esNegocio() { return esNegocio; }             // devuelve si es negocio
}
