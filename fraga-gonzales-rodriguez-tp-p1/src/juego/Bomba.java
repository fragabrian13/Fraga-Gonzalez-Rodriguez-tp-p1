import java.awt.Color;
import entorno.Entorno;

/** Bomba dejada por un enemigo. Tiene mecha, explota y su area cubre todo el ancho de la calle. */
public class Bomba {
    private static final long MECHA = 3000;
    private static final long DURACION_EXPLOSION = 600;
    private static final double LARGO_AREA = 160;
    private static final double TAM = 14;

    private double x, y;
    private boolean vertical;       // orientacion de la calle donde esta
    private long colocada;
    private boolean explotando;
    private long inicioExplosion;

    public Bomba(double x, double y, boolean vertical, long ahora) {
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.colocada = ahora;
    }

    public void actualizar(long ahora) {
        if (!explotando && ahora - colocada >= MECHA) {
            explotando = true;
            inicioExplosion = ahora;
        }
    }

    public boolean terminada(long ahora) {
        return explotando && ahora - inicioExplosion >= DURACION_EXPLOSION;
    }

    /** Colision con la bomba (antes de explotar) o con su area de efecto (al explotar). */
    public boolean alcanza(double px, double py, double pw, double ph) {
        if (!explotando) {
            return Colision.rectangulos(x, y, TAM, TAM, px, py, pw, ph);
        }
        if (vertical) {
            return Colision.rectangulos(x, y, Mapa.CALLE, LARGO_AREA, px, py, pw, ph);
        }
        return Colision.rectangulos(x, y, LARGO_AREA, Mapa.CALLE, px, py, pw, ph);
    }

    public void dibujar(Entorno e, long ahora) {
        if (explotando) {
            Color fuego = new Color(255, 110, 0);
            if (vertical) {
                e.dibujarRectangulo(x, y, Mapa.CALLE, LARGO_AREA, 0, fuego);
            } else {
                e.dibujarRectangulo(x, y, LARGO_AREA, Mapa.CALLE, 0, fuego);
            }
        } else {
            e.dibujarCirculo(x, y, TAM, Color.BLACK);
            if ((ahora / 200) % 2 == 0) {
                e.dibujarCirculo(x, y, 6, Color.RED);
            }
        }
    }
}
