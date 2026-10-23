package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import java.util.Random;                                         // generador de numeros al azar de Java
import entorno.Entorno;                                          // entorno grafico

/** Una cuadra (manzana): 3 casas y 1 negocio, ubicado al azar. */
public class Manzana {
    private double x, y;          // centro de la manzana
    private double ancho, alto;   // tamanio de la manzana
    private Edificio[] edificios; // los 4 edificios de la manzana
    private int indiceNegocio;    // cual de los 4 edificios es el negocio (0 a 3)
    private Random random;        // para elegir cosas al azar

    public Manzana(double x, double y, double ancho, double alto) {
        this.x = x;                                              // guardo el centro X
        this.y = y;                                              // guardo el centro Y
        this.ancho = ancho;                                      // guardo el ancho
        this.alto = alto;                                        // guardo el alto
        this.random = new Random();                              // creo el generador de azar
        this.edificios = new Edificio[4];                        // arreglo con lugar para 4 edificios
        this.indiceNegocio = random.nextInt(4);                  // elijo al azar (0,1,2 o 3) cual sera el negocio

        double dx = 35, dy = 25;  // desplazamientos desde el centro de la manzana a cada edificio
        int k = 0;                                               // posicion actual en el arreglo
        for (int fila = -1; fila <= 1; fila += 2) {              // fila toma -1 (arriba) y 1 (abajo)
            for (int col = -1; col <= 1; col += 2) {             // col toma -1 (izquierda) y 1 (derecha)
                // creo el edificio en una esquina; es negocio solo si k coincide con el indice elegido
                edificios[k] = new Edificio(x + col * dx, y + fila * dy, 60, 40, k == indiceNegocio);
                k++;                                             // paso al siguiente lugar del arreglo
            }
        }
    }

    public void dibujar(Entorno e) {
        e.dibujarRectangulo(x, y, ancho, alto, 0, new Color(70, 160, 60));   // el pasto verde de la manzana
        for (int i = 0; i < edificios.length; i++) {             // recorro los 4 edificios
            edificios[i].dibujar(e);                             // y dibujo cada uno
        }
    }

    // Indica si un rectangulo (por ejemplo el repartidor) toca la manzana
    public boolean colisiona(double px, double py, double pw, double ph) {
        return Colision.rectangulos(x, y, ancho, alto, px, py, pw, ph);
    }

    // Devuelve el edificio que es negocio
    public Edificio getNegocio() {
        return edificios[indiceNegocio];
    }

    // Devuelve una casa cualquiera de la manzana (nunca el negocio)
    public Edificio getCasaAleatoria() {
        int i = random.nextInt(4);                               // elijo un edificio al azar
        while (i == indiceNegocio) {                             // mientras me haya salido el negocio...
            i = random.nextInt(4);                               // ...vuelvo a elegir
        }
        return edificios[i];                                     // devuelvo la casa elegida
    }
}
