package juego;                                                  // paquete del juego

import java.awt.Color;                                           // colores
import java.util.Random;                                         // azar de Java
import entorno.Entorno;                                          // entorno grafico
import entorno.InterfaceJuego;                                   // interfaz que obliga a tener el metodo tick()

/** Delivery Rush: clase principal. Coordina el mapa, el repartidor, los enemigos, las bombas y los pedidos. */
public class Juego extends InterfaceJuego {
    // Estados del juego (numeros con nombre)
    private static int ESPERA = 0, JUGANDO = 1, GANADO = 2, PERDIDO = 3;
    private static int META_ENTREGAS = 5;                  // pedidos a entregar para ganar
    private static int MAX_PERDIDOS = 5;                   // pedidos perdidos para perder
    private static int VIDAS_INICIALES = 3;                // vidas con las que arranca el repartidor
    private static int CANT_ENEMIGOS = 5;   // siempre hay al menos 4 en el mapa
    private static int CANT_BOMBAS = 10;                   // maximo de bombas a la vez
    private static double RADIO_INTERACCION = 90;          // distancia maxima para retirar/entregar
    private static int PAUSA_ENTRE_PEDIDOS = 2000;         // ms de espera antes del proximo pedido

    private Entorno entorno;                                     // la ventana del juego
    private Random random;                                       // azar de Java
    private Mapa mapa;                                           // la ciudad
    private Repartidor repartidor;                               // el jugador
    private Enemigo[] enemigos;                                  // arreglo de enemigos
    private Bomba[] bombas;                                      // arreglo de bombas (null = lugar libre)
    private Proyectil proyectil;   // como maximo uno en vuelo
    private Pedido pedido;         // pedido actual (null si no hay)

    private int estado;                                          // estado actual (ESPERA, JUGANDO, ...)
    private int entregas, perdidos, eliminados;                  // contadores del HUD
    private int inicioJuego, tiempoFinal, proximoPedido;         // momentos (ms) para el cronometro y los pedidos
    private String motivoDerrota;                                // texto que se muestra al terminar

    // Constructor: arma la ventana y arranca el juego
    Juego() {
        // creo la ventana: ancho del mapa, alto = barra de arriba + mapa
        this.entorno = new Entorno(this, "Delivery Rush", Mapa.ANCHO, Mapa.ALTO_HUD + Mapa.ALTO);
        this.random = new Random();                              // creo el generador de azar
        reiniciar();                                             // dejo todo listo para empezar
        this.entorno.iniciar();                                  // arranca el ciclo: llama a tick() todo el tiempo
    }

    // Deja todo en su estado inicial (pantalla de espera)
    private void reiniciar() {
        mapa = new Mapa();                                       // mapa nuevo
        // el repartidor arranca en el centro de la calle de abajo (calle vertical 2, calle horizontal de abajo)
        repartidor = new Repartidor(Mapa.centroCalleVertical(2), Mapa.centroCalleHorizontal(Mapa.FILAS),
                VIDAS_INICIALES);
        enemigos = new Enemigo[CANT_ENEMIGOS];                   // arreglo de enemigos vacio
        bombas = new Bomba[CANT_BOMBAS];                         // arreglo de bombas vacio
        proyectil = null;                                        // sin proyectil en vuelo
        pedido = null;                                           // sin pedido
        entregas = 0;                                            // contadores en cero
        perdidos = 0;
        eliminados = 0;
        motivoDerrota = "";                                      // sin mensaje
        estado = ESPERA;                                         // espera que apriete ENTER
        reponerEnemigos(0);                                      // creo los enemigos iniciales
    }

    // Arranca una partida (cuando se aprieta ENTER en la pantalla de espera)
    private void comenzar(int ahora) {
        mapa = new Mapa();        // mapa nuevo en cada partida
        estado = JUGANDO;                                        // pasa a jugar
        inicioJuego = ahora;                                     // anoto cuando empezo (para el cronometro)
        proximoPedido = ahora + 1000;                            // el primer pedido llega al segundo
        for (int i = 0; i < enemigos.length; i++) {   // reinicia temporizadores de bombas
            enemigos[i] = null;                                  // borro los enemigos viejos
        }
        reponerEnemigos(ahora);                                  // creo enemigos nuevos con el tiempo actual
    }

    // Se ejecuta automaticamente muchas veces por segundo (lo llama el entorno)
    public void tick() {
        int ahora = entorno.tiempo();                            // tiempo actual en ms

        if (estado == JUGANDO) {                                 // si se esta jugando...
            actualizar(ahora);                                   // ...muevo y calculo todo
        } else if (entorno.sePresiono(entorno.TECLA_ENTER)) {    // si no se juega y se apreto ENTER...
            if (estado == ESPERA) {                              // ...desde la pantalla de inicio: empiezo
                comenzar(ahora);
            } else {                                             // ...desde ganar/perder: vuelvo al inicio
                reiniciar();
            }
        }
        dibujar(ahora);                                          // en todos los casos dibujo la pantalla
    }

    // ---------------------------------------------------------------- logica

    // Toda la logica de un tick mientras se juega
    private void actualizar(int ahora) {
        repartidor.mover(entorno, mapa);                         // muevo al repartidor segun las teclas

        // Disparo (solo uno en vuelo)
        if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO) && proyectil == null) {   // clic izquierdo y no hay otro disparo
            proyectil = repartidor.disparar();                   // creo el proyectil
        }
        if (proyectil != null) {                                 // si hay un proyectil en vuelo...
            proyectil.mover();                                   // ...lo muevo
            if (proyectil.fueraDelMapa() || proyectil.chocaConEdificio(mapa)) {   // si salio del mapa o choco con un edificio...
                proyectil = null;                                // ...lo borro
            } else {                                             // si sigue adentro, veo si le pego a alguien
                for (int i = 0; i < enemigos.length && proyectil != null; i++) {   // recorro enemigos (corta si ya pego)
                    if (enemigos[i] != null && proyectil.colisionaCon(enemigos[i])) {   // si le pego a este...
                        enemigos[i] = null;                      // ...lo elimino
                        proyectil = null;                        // ...el proyectil desaparece
                        eliminados++;                            // ...sumo uno al contador
                    }
                }
            }
        }
        reponerEnemigos(ahora);                                  // si mate alguno, aparece otro en su lugar

        // Enemigos: movimiento, bombas y choque con el repartidor
        for (int i = 0; i < enemigos.length; i++) {              // recorro los enemigos
            Enemigo en = enemigos[i];                            // el enemigo actual
            en.mover();                                          // lo muevo
            if (en.debeSoltarBomba(ahora)) {                     // si le toca soltar bomba...
                colocarBomba(en, ahora);                         // ...la coloco donde esta
            }
            // si toca al repartidor y el repartidor no esta en periodo de invulnerabilidad...
            if (en.colisionaCon(repartidor.getX(), repartidor.getY(), 20, 20)
                    && repartidor.puedeSerGolpeado(ahora)) {
                repartidor.recibirGolpe(ahora);                  // ...pierde una vida
                if (pedido != null && pedido.getEstado() == Pedido.EN_CAMINO) {   // y si llevaba un pedido...
                    perderPedido(ahora);                         // ...lo pierde
                }
            }
        }

        // Bombas
        for (int i = 0; i < bombas.length; i++) {                // recorro los lugares de bombas
            if (bombas[i] != null) {                             // si hay una bomba...
                bombas[i].actualizar(ahora);                     // ...actualizo su mecha/explosion
                if (bombas[i].alcanza(repartidor.getX(), repartidor.getY(), 20, 20)) {   // si alcanza al repartidor...
                    terminar(ahora, PERDIDO, "Te alcanzo una bomba");   // ...pierde la partida
                }
                if (bombas[i] != null && bombas[i].terminada(ahora)) {   // si ya termino de explotar...
                    bombas[i] = null;                            // ...libero el lugar
                }
            }
        }

        // Pedidos
        if (pedido == null) {                                    // si no hay pedido...
            if (ahora >= proximoPedido) {                        // ...y ya llego la hora del proximo
                // creo un pedido entre una casa y un negocio al azar
                pedido = new Pedido(mapa.casaAleatoria(), mapa.negocioAleatorio(), ahora);
            }
        } else {                                                 // si hay pedido...
            pedido.actualizar(ahora);                            // ...actualizo su estado
            if (pedido.getEstado() == Pedido.PERDIDO) {          // si se perdio por tiempo...
                perderPedido(ahora);                             // ...lo cuento como perdido
            }
        }
        if (pedido != null && entorno.sePresionoBoton(entorno.BOTON_DERECHO)) {   // clic derecho con pedido activo
            interactuar(ahora);                                  // intento retirar o entregar
        }

        // Condiciones de fin
        if (entregas >= META_ENTREGAS) {                         // llego a la meta: gana
            terminar(ahora, GANADO, "Entregaste " + META_ENTREGAS + " pedidos");
        } else if (perdidos >= MAX_PERDIDOS) {                   // perdio demasiados pedidos: pierde
            terminar(ahora, PERDIDO, "Perdiste " + MAX_PERDIDOS + " pedidos");
        } else if (repartidor.getVidas() <= 0) {                 // sin vidas: pierde
            terminar(ahora, PERDIDO, "Te quedaste sin vidas");
        }
    }

    /** Clic derecho sobre el negocio (retirar) o sobre la casa (entregar), estando cerca de la flecha. */
    private void interactuar(int ahora) {
        Edificio destino = pedido.getDestino();                  // edificio al que hay que ir ahora
        if (destino == null) {                                   // si no hay destino...
            return;                                              // ...no hago nada
        }
        // true si el mouse esta justo sobre el edificio destino
        boolean clicEnDestino = destino.contiene(entorno.mouseX(), entorno.mouseY());
        // true si el repartidor esta a menos del radio de interaccion (comparo al cuadrado, sin raiz)
        boolean cerca = destino.distanciaCuadradaA(repartidor.getX(), repartidor.getY())
                <= RADIO_INTERACCION * RADIO_INTERACCION;
        if (!clicEnDestino || !cerca) {                          // si falla alguna de las dos condiciones...
            return;                                              // ...no pasa nada
        }
        if (pedido.getEstado() == Pedido.ESPERANDO) {            // si estaba esperando en el negocio...
            pedido.recoger(ahora);                               // ...lo retiro
        } else if (pedido.getEstado() == Pedido.EN_CAMINO) {     // si iba en camino a la casa...
            pedido.entregar();                                   // ...lo entrego
            entregas++;                                          // sumo una entrega
            pedido = null;                                       // ya no hay pedido actual
            proximoPedido = ahora + PAUSA_ENTRE_PEDIDOS;         // el proximo llega en 2 segundos
        }
    }

    // Cuenta un pedido perdido y programa el siguiente
    private void perderPedido(int ahora) {
        perdidos++;                                              // sumo un perdido
        pedido = null;                                           // saco el pedido actual
        proximoPedido = ahora + PAUSA_ENTRE_PEDIDOS;             // el proximo llega en 2 segundos
    }

    // Termina la partida con un estado (GANADO o PERDIDO) y un mensaje
    private void terminar(int ahora, int nuevoEstado, String motivo) {
        if (estado == JUGANDO) {                                 // solo si todavia se estaba jugando (evita terminar dos veces)
            estado = nuevoEstado;                                // guardo el nuevo estado
            motivoDerrota = motivo;                              // guardo el mensaje
            tiempoFinal = ahora - inicioJuego;                   // congelo el tiempo de la partida
        }
    }

    // Pone una bomba en la posicion del enemigo, en el primer lugar libre del arreglo
    private void colocarBomba(Enemigo en, int ahora) {
        for (int i = 0; i < bombas.length; i++) {                // busco un lugar libre
            if (bombas[i] == null) {                             // si esta libre...
                bombas[i] = new Bomba(en.getX(), en.getY(), en.esVertical(), ahora);   // ...creo la bomba ahi
                return;                                          // ...y termino
            }
        }
    }

    /** Crea enemigos en los lugares vacios del arreglo para mantener siempre la cantidad. */
    private void reponerEnemigos(int ahora) {
        for (int i = 0; i < enemigos.length; i++) {              // recorro el arreglo
            if (enemigos[i] == null) {                           // si hay un lugar vacio...
                enemigos[i] = crearEnemigo(ahora);               // ...creo un enemigo nuevo
            }
        }
    }

    // Crea un enemigo en una calle al azar, lejos del repartidor
    private Enemigo crearEnemigo(int ahora) {
        boolean vertical = false;                                // orientacion elegida
        double x = 0, y = 0;                                     // posicion elegida
        for (int intento = 0; intento < 50; intento++) {         // pruebo hasta 50 posiciones
            vertical = random.nextBoolean();                     // elijo al azar vertical u horizontal
            if (vertical) {                                      // calle vertical:
                x = Mapa.centroCalleVertical(random.nextInt(Mapa.COLUMNAS + 1));   // X = centro de una de las 5 calles
                y = Mapa.ALTO_HUD + 20 + random.nextInt(Mapa.ALTO - 40);           // Y = cualquier punto de la calle
            } else {                                             // calle horizontal:
                y = Mapa.centroCalleHorizontal(random.nextInt(Mapa.FILAS + 1));    // Y = centro de una de las 5 calles
                x = 20 + random.nextInt(Mapa.ANCHO - 40);                          // X = cualquier punto de la calle
            }
            double ddx = x - repartidor.getX(), ddy = y - repartidor.getY();       // diferencias con el repartidor
            if (ddx * ddx + ddy * ddy >= 150 * 150) {            // distancia al cuadrado >= 150 al cuadrado
                break;   // lo bastante lejos del repartidor
            }
        }
        // creo el enemigo; 4 de cada 10 (nextInt(10) < 4) pueden poner bombas
        return new Enemigo(x, y, vertical, random.nextInt(10) < 4, ahora);
    }

    // ---------------------------------------------------------------- dibujo

    // Dibuja toda la pantalla (el orden importa: lo ultimo queda arriba)
    private void dibujar(int ahora) {
        mapa.dibujar(entorno);                                   // primero el fondo y las manzanas
        if (pedido != null) {                                    // si hay pedido...
            pedido.dibujarFlecha(entorno, ahora);                // ...dibujo la flecha del destino
        }
        for (int i = 0; i < bombas.length; i++) {                // bombas
            if (bombas[i] != null) {
                bombas[i].dibujar(entorno, ahora);
            }
        }
        for (int i = 0; i < enemigos.length; i++) {              // enemigos
            if (enemigos[i] != null) {
                enemigos[i].dibujar(entorno);
            }
        }
        if (proyectil != null) {                                 // proyectil
            proyectil.dibujar(entorno);
        }
        repartidor.dibujar(entorno, ahora);                      // repartidor
        dibujarHud(ahora);                                       // barra de datos de arriba

        // Carteles segun el estado
        if (estado == ESPERA) {
            mensaje("DELIVERY RUSH", "Presiona ENTER para comenzar", "Mover: WASD / flechas   Disparar: clic izq.   Pedidos: clic der.");
        } else if (estado == GANADO) {
            mensaje("GANASTE!", motivoDerrota, "ENTER para jugar de nuevo");
        } else if (estado == PERDIDO) {
            mensaje("PERDISTE", motivoDerrota, "ENTER para jugar de nuevo");
        }
    }

    // Dibuja la barra de arriba con tiempo, vidas y contadores
    private void dibujarHud(int ahora) {
        // fondo oscuro de la barra
        entorno.dibujarRectangulo(Mapa.ANCHO / 2.0, Mapa.ALTO_HUD / 2.0, Mapa.ANCHO, Mapa.ALTO_HUD, 0,
                new Color(30, 30, 40));
        int ms = 0;                                              // milisegundos a mostrar
        if (estado == JUGANDO) {                                 // jugando: tiempo corriendo
            ms = ahora - inicioJuego;
        } else if (estado != ESPERA) {                           // ganado o perdido: tiempo congelado
            ms = tiempoFinal;
        }                                                        // en espera queda en 0
        int seg = ms / 1000;                                     // paso a segundos
        entorno.cambiarFont("Arial", 16, Color.WHITE);           // letra Arial 16 blanca
        // minutos = seg / 60, segundos = seg % 60 (el resto), cada uno con dos digitos
        entorno.escribirTexto("Tiempo: " + dosDigitos(seg / 60) + ":" + dosDigitos(seg % 60), 15, 20);
        entorno.escribirTexto("Vidas: " + repartidor.getVidas(), 180, 20);
        entorno.escribirTexto("Pedidos entregados: " + entregas + "/" + META_ENTREGAS, 290, 20);
        entorno.escribirTexto("Pedidos perdidos: " + perdidos + "/" + MAX_PERDIDOS, 560, 20);
        entorno.escribirTexto("Enemigos eliminados: " + eliminados, 15, 42);
        entorno.escribirTexto("Pedido actual: " + textoPedido(ahora), 290, 42);
    }

    /** Devuelve el numero con al menos dos digitos (5 pasa a "05"). */
    private String dosDigitos(int n) {
        if (n < 10) {                                            // si tiene un solo digito...
            return "0" + n;                                      // ...le agrego un 0 adelante
        }
        return "" + n;                                           // si no, lo paso a texto tal cual
    }

    // Devuelve el texto que describe el pedido actual para el HUD
    private String textoPedido(int ahora) {
        if (pedido == null) {                                    // sin pedido
            return "ninguno";
        }
        int e = pedido.getEstado();                              // estado del pedido
        if (e == Pedido.REALIZADO) return "Realizado";
        if (e == Pedido.ESPERANDO) return "Esperando (retirar en el negocio)";
        return "En camino (" + pedido.segundosRestantes(ahora) + " s)";   // en camino: muestra el tiempo que queda
    }

    // Dibuja un cartel negro en el centro con un titulo y dos lineas de texto
    private void mensaje(String titulo, String linea1, String linea2) {
        double cx = Mapa.ANCHO / 2.0, cy = Mapa.ALTO_HUD + Mapa.ALTO / 2.0;   // centro del mapa
        entorno.dibujarRectangulo(cx, cy, 640, 200, 0, new Color(0, 0, 0));   // fondo negro del cartel
        entorno.cambiarFont("Arial", 36, Color.YELLOW);          // titulo grande amarillo
        entorno.escribirTexto(titulo, cx - 160, cy - 40);
        entorno.cambiarFont("Arial", 20, Color.WHITE);           // primera linea blanca
        entorno.escribirTexto(linea1, cx - 160, cy + 5);
        entorno.cambiarFont("Arial", 14, Color.LIGHT_GRAY);      // segunda linea gris chica
        entorno.escribirTexto(linea2, cx - 290, cy + 50);
    }

    // Punto de entrada del programa
    public static void main(String[] args) {
        Juego juego = new Juego();                               // crear el Juego ya abre la ventana y arranca todo
    }
}
