package juego;
import java.awt.Color;                                           // colores
import entorno.Entorno;                                          // entorno grafico

public class Repartidor {
	private static int INVULNERABILIDAD = 1500;  // ms tras recibir un golpe
    private double x, y;                                         // posicion del centro
    private double ancho = 20, alto = 20;                        // tamanio del repartidor
    private double velocidad = 3;                                // pixeles por tick
    private int vidas;                                           // vidas que le quedan
    private int dirX, dirY;         // ultima direccion de movimiento (para disparar hacia ahi)
    private int ultimoGolpe;                                     // momento (ms) del ultimo golpe recibido

    public Repartidor(double x, double y, int vidas) {
        this.x = x;                                              // posicion inicial X
        this.y = y;                                              // posicion inicial Y
        this.vidas = vidas;                                      // vidas iniciales
        this.dirX = 0;                                           // al principio no se mueve en X...
        this.dirY = -1;                                          // ...y apunta hacia arriba
        this.ultimoGolpe = -INVULNERABILIDAD;                    // asi al empezar ya puede ser golpeado
    }

    public void mover(Entorno e, Mapa mapa) {
        int dx = 0, dy = 0;                                      // direccion que pide el jugador en este tick
        // cada tecla suma o resta 1 a la direccion (arriba/izquierda restan, abajo/derecha suman)
        if (e.estaPresionada('w') || e.estaPresionada(e.TECLA_ARRIBA)) dy -= 1;
        if (e.estaPresionada('s') || e.estaPresionada(e.TECLA_ABAJO)) dy += 1;
        if (e.estaPresionada('a') || e.estaPresionada(e.TECLA_IZQUIERDA)) dx -= 1;
        if (e.estaPresionada('d') || e.estaPresionada(e.TECLA_DERECHA)) dx += 1;
        if (dx == 0 && dy == 0) {                                // si no apreto nada (o se anulan)...
            return;                                              // ...no hago nada
        }
        if (dx == 0 || dy == 0) {                                // solo recuerdo direcciones rectas (el disparo no va en diagonal)
            dirX = dx;                                           // recuerdo la direccion para disparar
            dirY = dy;
        }

        double paso = velocidad;                                 // cuanto avanza este tick
        if (dx != 0 && dy != 0) {                                // si va en diagonal...
            paso = velocidad * 0.7071;                           // ...achico el paso para no ir mas rapido
        }
        double nx = x + dx * paso;                               // X a la que quiere ir
        if (puedeIr(nx, y, mapa)) {                              // si ahi no choca...
            x = nx;                                              // ...se mueve en X
        }
        double ny = y + dy * paso;                               // Y a la que quiere ir
        if (puedeIr(x, ny, mapa)) {                              // si ahi no choca...
            y = ny;                                              // ...se mueve en Y
        }
        // (X e Y se prueban por separado para que pueda "deslizarse" por las paredes)
    }

    // Devuelve true si en esa posicion esta dentro del mapa y no choca con ninguna manzana
    private boolean puedeIr(double nx, double ny, Mapa mapa) {
        return mapa.contiene(nx, ny, ancho, alto) && !mapa.colisionaConManzana(nx, ny, ancho, alto);
    }

    // Crea un proyectil que sale desde el repartidor en la ultima direccion
    public Proyectil disparar() {
        return new Proyectil(x, y, dirX, dirY);
    }

    // Devuelve true si ya paso la invulnerabilidad del ultimo golpe
    public boolean puedeSerGolpeado(int ahora) {
        return ahora - ultimoGolpe >= INVULNERABILIDAD;
    }

    public void recibirGolpe(int ahora) {
        vidas--;                                                 // pierde una vida
        ultimoGolpe = ahora;                                     // anoto cuando fue, para la invulnerabilidad
    }

    // Indica si el repartidor toca a otro rectangulo
    public boolean colisionaCon(double ox, double oy, double ow, double oh) {
        return Colision.rectangulos(x, y, ancho, alto, ox, oy, ow, oh);
    }

    public void dibujar(Entorno e, int ahora) {
        // parpadea (no se dibuja) mientras es invulnerable, alternando cada 120 ms
        boolean parpadeo = !puedeSerGolpeado(ahora) && (ahora / 120) % 2 == 0;
        if (parpadeo) {                                          // en los momentos "apagados"...
            return;                                              // ...no dibujo nada
        }
        e.dibujarRectangulo(x, y, ancho, alto, 0, new Color(30, 120, 255));   // cuerpo azul
        e.dibujarCirculo(x, y, 8, Color.WHITE);                  // circulo blanco en el centro
    }

    public double getX() { return x; }                           // devuelve X
    public double getY() { return y; }                           // devuelve Y
    public int getVidas() { return vidas; }                      // devuelve las vidas
}
