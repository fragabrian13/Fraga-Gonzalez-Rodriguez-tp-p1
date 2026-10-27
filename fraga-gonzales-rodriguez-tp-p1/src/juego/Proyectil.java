import java.awt.Color;
import entorno.Entorno;

/** Disparo del repartidor. Sale en la ultima direccion de movimiento. */
public class Proyectil {
    private static final double VELOCIDAD = 7;
    private double x, y;
    private double vx, vy;
    private double tam = 8;

    public Proyectil(double x, double y, double dirX, double dirY) {
        this.x = x;
        this.y = y;
        double largo = Math.sqrt(dirX * dirX + dirY * dirY);
        this.vx = dirX / largo * VELOCIDAD;
        this.vy = dirY / largo * VELOCIDAD;
    }

    public void mover() {
        x += vx;
        y += vy;
    }

    public boolean fueraDelMapa() {
        return !Mapa.estaDentro(x, y);
    }

    public boolean colisionaCon(Enemigo en) {
        return en.colisionaCon(x, y, tam, tam);
    }

    public void dibujar(Entorno e) {
        e.dibujarCirculo(x, y, tam, Color.YELLOW);
    }
}
