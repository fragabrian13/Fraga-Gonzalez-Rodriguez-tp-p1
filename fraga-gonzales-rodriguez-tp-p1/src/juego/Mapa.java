package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import java.util.Random;                                         // azar de Java
import entorno.Entorno;                                          // entorno grafico

/** El mapa de la ciudad: grilla de 4x4 manzanas separadas por calles. */
public class Mapa {
    public static int ALTO_HUD = 50;                       // alto de la barra de arriba con los datos
    public static int CALLE = 50;                          // ancho de cada calle
    public static int MANZANA_ANCHO = 150;                 // ancho de cada manzana
    public static int MANZANA_ALTO = 100;                  // alto de cada manzana
    public static int COLUMNAS = 4;                        // manzanas a lo ancho
    public static int FILAS = 4;                           // manzanas a lo alto
    public static int ANCHO = CALLE + COLUMNAS * (MANZANA_ANCHO + CALLE);  // ancho total = 850
    public static int ALTO = CALLE + FILAS * (MANZANA_ALTO + CALLE);       // alto total = 650

    private Manzana[] manzanas;                                  // las 16 manzanas
    private Random random;                                       // para elegir manzanas al azar

    public Mapa() {
        this.random = new Random();                              // creo el generador de azar
        manzanas = new Manzana[COLUMNAS * FILAS];                // arreglo de 16 lugares
        int k = 0;                                               // posicion actual en el arreglo
        for (int f = 0; f < FILAS; f++) {                        // recorro las filas de manzanas
            for (int c = 0; c < COLUMNAS; c++) {                 // recorro las columnas de manzanas
                // X del centro: una calle de margen + las manzanas (con su calle) anteriores + mitad de esta manzana
                double x = CALLE + c * (MANZANA_ANCHO + CALLE) + MANZANA_ANCHO / 2.0;
                // Y del centro: igual, pero sumando tambien la barra de arriba (HUD)
                double y = ALTO_HUD + CALLE + f * (MANZANA_ALTO + CALLE) + MANZANA_ALTO / 2.0;
                manzanas[k] = new Manzana(x, y, MANZANA_ANCHO, MANZANA_ALTO);   // creo la manzana
                k++;                                             // siguiente lugar
            }
        }
    }

    /** X del centro de la calle vertical i (0 a COLUMNAS). */
    public static double centroCalleVertical(int i) {
        return CALLE / 2.0 + i * (MANZANA_ANCHO + CALLE);        // mitad de calle + i bloques (manzana + calle)
    }

    /** Y del centro de la calle horizontal j (0 a FILAS). */
    public static double centroCalleHorizontal(int j) {
        return ALTO_HUD + CALLE / 2.0 + j * (MANZANA_ALTO + CALLE);   // igual, sumando la barra de arriba
    }

    public void dibujar(Entorno e) {
        // fondo gris oscuro que representa el asfalto de las calles
        e.dibujarRectangulo(ANCHO / 2.0, ALTO_HUD + ALTO / 2.0, ANCHO, ALTO, 0, new Color(60, 60, 60));
        for (int i = 0; i < manzanas.length; i++) {              // recorro las 16 manzanas
            manzanas[i].dibujar(e);                              // y dibujo cada una encima del asfalto
        }
    }

    // Indica si un rectangulo choca con alguna manzana (o sea, no esta en la calle)
    public boolean colisionaConManzana(double x, double y, double w, double h) {
        for (int i = 0; i < manzanas.length; i++) {              // pruebo con cada manzana
            if (manzanas[i].colisiona(x, y, w, h)) {             // si toca alguna...
                return true;                                     // ...ya devuelvo true
            }
        }
        return false;                                            // no toco ninguna
    }

    /** Indica si un rectangulo esta completamente dentro de los limites del mapa. */
    public boolean contiene(double x, double y, double w, double h) {
        return x - w / 2 >= 0 && x + w / 2 <= ANCHO              // no se pasa por izquierda ni derecha
            && y - h / 2 >= ALTO_HUD && y + h / 2 <= ALTO_HUD + ALTO;   // no se pasa por arriba ni abajo
    }

    // Indica si un punto esta dentro del mapa
    public static boolean estaDentro(double x, double y) {
        return x >= 0 && x <= ANCHO && y >= ALTO_HUD && y <= ALTO_HUD + ALTO;
    }

    // Devuelve el negocio de una manzana elegida al azar
    public Edificio negocioAleatorio() {
        return manzanas[random.nextInt(manzanas.length)].getNegocio();
    }

    // Devuelve una casa de una manzana elegida al azar
    public Edificio casaAleatoria() {
        return manzanas[random.nextInt(manzanas.length)].getCasaAleatoria();
    }
}
