/**
 * Agente de Inteligencia Artificial que usa el algoritmo MINIMAX.
 *
 * Idea general:
 *  - Se construye (recursivamente) el árbol de TODAS las partidas posibles.
 *  - Cada hoja (partida terminada) recibe un puntaje:
 *        gana la IA      -> positivo
 *        gana el humano  -> negativo
 *        empate          -> 0
 *  - En los turnos de la IA se elige el puntaje MÁXIMO (ella quiere ganar).
 *  - En los turnos del humano se elige el puntaje MÍNIMO (se asume que juega lo mejor posible).
 *  Resultado: la IA juega de forma óptima y nunca pierde.
 */
public class IA_Minimax {

    private final char fichaIA;
    private final char fichaHumano;

    public IA_Minimax(char fichaIA, char fichaHumano) {
        this.fichaIA = fichaIA;
        this.fichaHumano = fichaHumano;
    }

    /**
     * Recorre todas las casillas libres, simula cada jugada con Minimax
     * y devuelve la mejor como {fila, columna}. Devuelve null si no hay jugadas.
     */
    public int[] mejorMovimiento(Tablero tablero) {
        int mejorPuntaje = Integer.MIN_VALUE;
        int[] mejorJugada = null;

        for (int f = 0; f < Tablero.TAMANO; f++) {
            for (int c = 0; c < Tablero.TAMANO; c++) {
                if (tablero.esMovimientoValido(f, c)) {
                    tablero.colocar(f, c, fichaIA);            // 1. simular jugada de la IA
                    int puntaje = minimax(tablero, 1, false);  // 2. evaluar qué pasa después
                    tablero.quitar(f, c);                      // 3. deshacer la simulación

                    if (puntaje > mejorPuntaje) {
                        mejorPuntaje = puntaje;
                        mejorJugada = new int[]{f, c};
                    }
                }
            }
        }
        return mejorJugada;
    }

    /**
     * FUNCIÓN RECURSIVA DE MINIMAX.
     *
     * @param tablero      estado actual (simulado)
     * @param profundidad  cuántas jugadas se han simulado desde el estado real
     * @param turnoIA      true si le toca a la IA (maximiza), false si le toca al humano (minimiza)
     * @return puntaje del estado
     */
    private int minimax(Tablero tablero, int profundidad, boolean turnoIA) {

        // ---- CASOS BASE: la partida terminó ----
        // Se resta la profundidad para preferir ganar RÁPIDO y perder LENTO.
        if (tablero.hayGanador(fichaIA)) {
            return 10 - profundidad;
        }
        if (tablero.hayGanador(fichaHumano)) {
            return profundidad - 10;
        }
        if (tablero.estaLleno()) {
            return 0; // empate
        }

        // ---- CASO RECURSIVO ----
        if (turnoIA) {
            // MAX: la IA busca el puntaje más alto
            int mejor = Integer.MIN_VALUE;
            for (int f = 0; f < Tablero.TAMANO; f++) {
                for (int c = 0; c < Tablero.TAMANO; c++) {
                    if (tablero.esMovimientoValido(f, c)) {
                        tablero.colocar(f, c, fichaIA);
                        mejor = Math.max(mejor, minimax(tablero, profundidad + 1, false));
                        tablero.quitar(f, c);
                    }
                }
            }
            return mejor;
        } else {
            // MIN: el humano busca el puntaje más bajo (lo peor para la IA)
            int mejor = Integer.MAX_VALUE;
            for (int f = 0; f < Tablero.TAMANO; f++) {
                for (int c = 0; c < Tablero.TAMANO; c++) {
                    if (tablero.esMovimientoValido(f, c)) {
                        tablero.colocar(f, c, fichaHumano);
                        mejor = Math.min(mejor, minimax(tablero, profundidad + 1, true));
                        tablero.quitar(f, c);
                    }
                }
            }
            return mejor;
        }
    }
}
