import java.awt.Color;
import entorno.Entorno;

/** Un pedido: una casa lo hace, un negocio lo prepara y el repartidor lo lleva. */
public class Pedido {
    public static final int REALIZADO = 0;
    public static final int ESPERANDO = 1;
    public static final int EN_CAMINO = 2;
    public static final int ENTREGADO = 3;
    public static final int PERDIDO = 4;

    private static final long DEMORA_NEGOCIO = 1500;   // ms hasta que el negocio recibe el encargo
    private static final long LIMITE_ENTREGA = 30000;  // ms para entregar una vez recogido

    private Edificio casa, negocio;
    private int estado;
    private long creado, recogido;

    public Pedido(Edificio casa, Edificio negocio, long ahora) {
        this.casa = casa;
        this.negocio = negocio;
        this.estado = REALIZADO;
        this.creado = ahora;
    }

    public void actualizar(long ahora) {
        if (estado == REALIZADO && ahora - creado >= DEMORA_NEGOCIO) {
            estado = ESPERANDO;
        } else if (estado == EN_CAMINO && ahora - recogido > LIMITE_ENTREGA) {
            estado = PERDIDO;
        }
    }

    public void recoger(long ahora) {
        if (estado == ESPERANDO) {
            estado = EN_CAMINO;
            recogido = ahora;
        }
    }

    public void entregar() {
        if (estado == EN_CAMINO) {
            estado = ENTREGADO;
        }
    }

    public void perder() {
        estado = PERDIDO;
    }

    /** Edificio al que hay que ir ahora: el negocio al esperar, la casa al ir en camino. */
    public Edificio getDestino() {
        if (estado == ESPERANDO) return negocio;
        if (estado == EN_CAMINO) return casa;
        return null;
    }

    public int getEstado() { return estado; }

    public long segundosRestantes(long ahora) {
        long resto = LIMITE_ENTREGA - (ahora - recogido);
        return resto < 0 ? 0 : resto / 1000;
    }

    /** Dibuja la flecha que senala el destino actual (si hay). */
    public void dibujarFlecha(Entorno e, long ahora) {
        Edificio d = getDestino();
        if (d == null) {
            return;
        }
        double salto = Math.sin(ahora / 150.0) * 4;
        Color c = (estado == ESPERANDO) ? Color.RED : Color.GREEN;
        e.dibujarRectangulo(d.getX(), d.getY() - 10 + salto, 8, 14, 0, c);
        // Si la punta apunta para el lado equivocado, cambiar Math.PI por 0
        e.dibujarTriangulo(d.getX(), d.getY() + 3 + salto, 12, 22, Math.PI, c);
    }
}
