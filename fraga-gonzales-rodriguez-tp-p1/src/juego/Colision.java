package juego;

public class Colision {
	// Devuelve true si el rectangulo 1 (centro x1,y1; ancho w1; alto h1) toca al rectangulo 2
    public static boolean rectangulos(double x1, double y1, double w1, double h1,
                                      double x2, double y2, double w2, double h2) {
        return valorAbsoluto(x1 - x2) < (w1 + w2) / 2 && valorAbsoluto(y1 - y2) < (h1 + h2) / 2;    // Chocan si la distancia entre centros en X es menor que la mitad de los dos anchos sumados
        																							// Y TAMBIEN la distancia en Y es menor que la mitad de los dos altos sumados
    }

    /** Valor absoluto de un numero (sin usar Math). */
    public static double valorAbsoluto(double n) {
        if (n < 0) {                                             // si el numero es negativo...
            return -n;                                           // ...le cambio el signo
        }
        return n;                                                // si no, ya es positivo: lo devuelvo igual
    }
}
