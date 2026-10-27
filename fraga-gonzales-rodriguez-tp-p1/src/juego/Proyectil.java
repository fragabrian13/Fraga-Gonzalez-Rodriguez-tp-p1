package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import entorno.Entorno;                                          // entorno grafico

/** Disparo del repartidor. Sale en la ultima direccion de movimiento. */
public class Proyectil {
    private static double VELOCIDAD = 7;                   // pixeles por tick
    private double x, y;                                         // posicion actual
    private double vx, vy;                                       // velocidad en cada eje
    private double tam = 8;                                      // diametro del proyectil

    public Proyectil(double x, double y, double dirX, double dirY) {
        this.x = x;                                              // arranca en la posicion del repartidor (X)
        this.y = y;                                              // y en Y
        double factor = 1;                    // en diagonal se achica para no ir mas rapido
        if (dirX != 0 && dirY != 0) {                            // si la direccion es diagonal...
            factor = 0.7071;                                     // ...multiplico por 1/raiz(2)
        }
        this.vx = dirX * VELOCIDAD * factor;                     // velocidad horizontal
        this.vy = dirY * VELOCIDAD * factor;                     // velocidad vertical
    }

    public void mover() {
        x += vx;                                                 // avanzo en X
        y += vy;                                                 // avanzo en Y
    }

    // Devuelve true si el proyectil salio del mapa (hay que borrarlo)
    public boolean fueraDelMapa() {
        return !Mapa.estaDentro(x, y);
    }

    // Devuelve true si el proyectil choco contra un edificio (no los atraviesa)
    public boolean chocaConEdificio(Mapa mapa) {
        return mapa.colisionaConManzana(x, y, tam, tam);
    }

    // Devuelve true si el proyectil le pego a un enemigo
    public boolean colisionaCon(Enemigo en) {
        return en.colisionaCon(x, y, tam, tam);
    }

    public void dibujar(Entorno e) {
        e.dibujarCirculo(x, y, tam, Color.YELLOW);               // circulo amarillo
    }
}
