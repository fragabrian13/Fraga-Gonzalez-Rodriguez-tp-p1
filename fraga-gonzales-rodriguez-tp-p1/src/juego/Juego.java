package juego;
import java.awt.Color;                                           
import java.util.Random;                                         
import entorno.Entorno;                                          
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego{
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
    
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
