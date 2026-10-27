package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import entorno.Entorno;                                          // entorno grafico

/** Un pedido: una casa lo hace, un negocio lo prepara y el repartidor lo lleva. */
public class Pedido {
    // Estados posibles del pedido (son numeros con nombre)
    public static int REALIZADO = 0;                       // la casa lo hizo, el negocio todavia no lo recibe
    public static int ESPERANDO = 1;                       // el negocio ya lo tiene listo para retirar
    public static int EN_CAMINO = 2;                       // el repartidor lo retiro y lo lleva a la casa
    public static int ENTREGADO = 3;                       // llego a la casa
    public static int PERDIDO = 4;                         // se perdio (tiempo o golpe)

    private static int DEMORA_NEGOCIO = 1500;   // ms hasta que el negocio recibe el encargo
    private static int LIMITE_ENTREGA = 30000;  // ms para entregar una vez recogido

    private Edificio casa, negocio;                              // origen y destino del pedido
    private int estado;                                          // estado actual (uno de los de arriba)
    private int creado, recogido;                                // momentos (ms) de creacion y de retiro

    public Pedido(Edificio casa, Edificio negocio, int ahora) {
        this.casa = casa;                                        // guardo la casa
        this.negocio = negocio;                                  // guardo el negocio
        this.estado = REALIZADO;                                 // arranca como "realizado"
        this.creado = ahora;                                     // anoto cuando se creo
    }

    public void actualizar(int ahora) {
        if (estado == REALIZADO && ahora - creado >= DEMORA_NEGOCIO) {   // paso la demora del negocio...
            estado = ESPERANDO;                                  // ...ahora espera al repartidor
        } else if (estado == EN_CAMINO && ahora - recogido > LIMITE_ENTREGA) {   // se acabo el tiempo para entregar...
            estado = PERDIDO;                                    // ...se pierde el pedido
        }
    }

    // El repartidor retira el pedido en el negocio
    public void recoger(int ahora) {
        if (estado == ESPERANDO) {                               // solo si estaba esperando
            estado = EN_CAMINO;                                  // pasa a "en camino"
            recogido = ahora;                                    // empieza a correr el tiempo limite
        }
    }

    // El repartidor entrega el pedido en la casa
    public void entregar() {
        if (estado == EN_CAMINO) {                               // solo si iba en camino
            estado = ENTREGADO;                                  // queda entregado
        }
    }

    // Marca el pedido como perdido
    public void perder() {
        estado = PERDIDO;
    }

    /** Edificio al que hay que ir ahora: el negocio al esperar, la casa al ir en camino. */
    public Edificio getDestino() {
        if (estado == ESPERANDO) return negocio;                 // hay que ir a retirarlo
        if (estado == EN_CAMINO) return casa;                    // hay que ir a entregarlo
        return null;                                             // en otros estados no hay destino
    }

    public int getEstado() { return estado; }                    // devuelve el estado actual

    // Segundos que quedan para entregar (en 0 si ya se acabo)
    public int segundosRestantes(int ahora) {
        int resto = LIMITE_ENTREGA - (ahora - recogido);         // ms que faltan
        if (resto < 0) {                                         // si ya paso el limite...
            return 0;                                            // ...devuelvo 0 (no negativo)
        }
        return resto / 1000;                                     // paso de ms a segundos
    }

    /** Dibuja la flecha que senala el destino actual (si hay). */
    public void dibujarFlecha(Entorno e, int ahora) {
        Edificio d = getDestino();                               // a donde tiene que apuntar la flecha
        if (d == null) {                                         // si no hay destino...
            return;                                              // ...no dibujo nada
        }
        double salto = 0;                    // la flecha sube y baja cada 150 ms
        if ((ahora / 150) % 2 == 0) {                            // alterna cada 150 ms
            salto = 4;                                           // en un momento baja 4 pixeles
        }
        Color c = Color.GREEN;               // verde: va a la casa
        if (estado == ESPERANDO) {                               // si hay que ir al negocio...
            c = Color.RED;                   // roja: va al negocio
        }
        // palito de la flecha, arriba del edificio
        e.dibujarRectangulo(d.getX(), d.getY() - 10 + salto, 8, 14, 0, c);
        e.dibujarTriangulo(d.getX(), d.getY() + 3 + salto, 14, 18, 1.5708, c);   // punta de la flecha, apuntando abajo
    }
}
