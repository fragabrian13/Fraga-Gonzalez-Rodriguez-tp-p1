package juego;
import java.awt.Color;
import java.util.Random;
import entorno.Entorno;

public class Enemigo {
	private double x, y;                  // posicion del centro
    private double tam = 20;              // diametro del enemigo
    private double velocidad = 1.5;       // pixeles que avanza por tick
    private int dx, dy;                   // direccion de movimiento (-1, 0 o 1) en cada eje
    private boolean vertical;             // true = patrulla una calle vertical, false = horizontal
    private boolean puedePonerBomba;      // true = este enemigo deja bombas
    private int ultimaBomba;              // momento (ms) de la ultima bomba que dejo
    private int intervaloBomba;           // cuantos ms tiene que esperar para la proxima bomba
    private Random random;                // para elegir cosas al azar

    public Enemigo(double x, double y, boolean vertical, boolean puedePonerBomba, int ahora) {
        this.x = x;                                              // guardo X
        this.y = y;                                              // guardo Y
        this.random = new Random();                              // creo el generador de azar
        this.vertical = vertical;                                // guardo la orientacion
        this.puedePonerBomba = puedePonerBomba;                  // guardo si pone bombas
        int signo = 1;                                           // por defecto va hacia adelante (derecha o abajo)
        if (random.nextBoolean()) {                              // 50% de las veces (true)...
            signo = -1;                                          // ...va hacia atras (izquierda o arriba)
        }
        if (vertical) {                                          // si patrulla una calle vertical...
            dy = signo;                                          // ...se mueve en Y
        } else {                                                 // si es horizontal...
            dx = signo;                                          // ...se mueve en X
        }
        this.ultimaBomba = ahora;                                // cuenta desde que aparece
        this.intervaloBomba = nuevoIntervalo();                  // elijo cuanto va a esperar para la primera bomba
    }

    // Devuelve un tiempo de espera al azar entre 4000 y 7999 ms
    private int nuevoIntervalo() {
        return 4000 + random.nextInt(4000);
    }

    public void mover() {
        x += dx * velocidad;                                     // avanzo en X (dx es 0 si es vertical)
        y += dy * velocidad;                                     // avanzo en Y (dy es 0 si es horizontal)
        if (vertical) {                                          // si va por una calle vertical, rebota arriba y abajo
            // si se paso del borde de arriba: lo dejo justo en el borde y cambio a ir hacia abajo
            if (y < Mapa.ALTO_HUD + tam / 2) { y = Mapa.ALTO_HUD + tam / 2; dy = 1; }
            // si se paso del borde de abajo: lo dejo justo en el borde y cambio a ir hacia arriba
            if (y > Mapa.ALTO_HUD + Mapa.ALTO - tam / 2) { y = Mapa.ALTO_HUD + Mapa.ALTO - tam / 2; dy = -1; }
        } else {                                                 // si va por una calle horizontal, rebota en los costados
            if (x < tam / 2) { x = tam / 2; dx = 1; }            // borde izquierdo: pasa a ir hacia la derecha
            if (x > Mapa.ANCHO - tam / 2) { x = Mapa.ANCHO - tam / 2; dx = -1; }   // borde derecho: hacia la izquierda
        }
    }

    /** Devuelve true cuando le toca dejar una bomba (solo los enemigos que pueden). */
    public boolean debeSoltarBomba(int ahora) {
        // si puede poner bombas Y ya paso el tiempo de espera desde la ultima...
        if (puedePonerBomba && ahora - ultimaBomba >= intervaloBomba) {
            ultimaBomba = ahora;                                 // anoto el momento de esta bomba
            intervaloBomba = nuevoIntervalo();                   // sorteo la espera para la proxima
            return true;                                         // si, hay que soltar bomba
        }
        return false;                                            // todavia no
    }

    // Indica si el enemigo toca a otro rectangulo (el repartidor o un proyectil)
    public boolean colisionaCon(double ox, double oy, double ow, double oh) {
        return Colision.rectangulos(x, y, tam, tam, ox, oy, ow, oh);
    }

    public void dibujar(Entorno e) {
        if (puedePonerBomba) {                                   // enemigo bombardero
            e.dibujarCirculo(x, y, tam, new Color(120, 0, 120)); // circulo violeta oscuro
            e.dibujarCirculo(x, y, 8, Color.BLACK);              // con un punto negro adentro para distinguirlo
        } else {                                                 // enemigo comun
            e.dibujarCirculo(x, y, tam, new Color(170, 60, 200));   // circulo violeta claro
        }
    }

    public double getX() { return x; }                           // devuelve X
    public double getY() { return y; }                           // devuelve Y
    public boolean esVertical() { return vertical; }             // devuelve si patrulla calle vertical
}
