/**
 * Representa el tablero 3x3 del Totito.
 * Contiene SOLO la lógica del tablero (sin nada de interfaz gráfica).
 */
public class Tablero {

    public static final char VACIO = ' ';
    public static final int TAMANO = 3;

    private final char[][] celdas = new char[TAMANO][TAMANO];

    // Las 8 líneas ganadoras posibles (3 filas, 3 columnas, 2 diagonales).
    // Cada línea guarda tres posiciones en formato {fila, columna}.
    private static final int[][][] LINEAS = {
        {{0,0},{0,1},{0,2}}, {{1,0},{1,1},{1,2}}, {{2,0},{2,1},{2,2}}, // filas
        {{0,0},{1,0},{2,0}}, {{0,1},{1,1},{2,1}}, {{0,2},{1,2},{2,2}}, // columnas
        {{0,0},{1,1},{2,2}}, {{0,2},{1,1},{2,0}}                       // diagonales
    };

    public Tablero() {
        reiniciar();
    }

    /** Deja todas las casillas vacías. */
    public void reiniciar() {
        for (int f = 0; f < TAMANO; f++) {
            for (int c = 0; c < TAMANO; c++) {
                celdas[f][c] = VACIO;
            }
        }
    }

    /** Devuelve la ficha en la casilla indicada. */
    public char obtener(int fila, int col) {
        return celdas[fila][col];
    }

    /** Valida que la casilla esté dentro del tablero y libre. */
    public boolean esMovimientoValido(int fila, int col) {
        return fila >= 0 && fila < TAMANO
            && col >= 0 && col < TAMANO
            && celdas[fila][col] == VACIO;
    }

    /**
     * Coloca una ficha si el movimiento es válido.
     * @return true si se pudo colocar, false si la casilla está ocupada o fuera de límites.
     */
    public boolean colocar(int fila, int col, char ficha) {
        if (!esMovimientoValido(fila, col)) {
            return false;
        }
        celdas[fila][col] = ficha;
        return true;
    }

    /** Quita una ficha (lo usa Minimax para "deshacer" una jugada simulada). */
    public void quitar(int fila, int col) {
        celdas[fila][col] = VACIO;
    }

    /** Devuelve la línea ganadora de la ficha dada, o null si no ha ganado. */
    public int[][] lineaGanadora(char ficha) {
        for (int[][] linea : LINEAS) {
            if (celdas[linea[0][0]][linea[0][1]] == ficha
             && celdas[linea[1][0]][linea[1][1]] == ficha
             && celdas[linea[2][0]][linea[2][1]] == ficha) {
                return linea;
            }
        }
        return null;
    }

    /** true si la ficha dada tiene 3 en línea. */
    public boolean hayGanador(char ficha) {
        return lineaGanadora(ficha) != null;
    }

    /** true si no queda ninguna casilla vacía. */
    public boolean estaLleno() {
        for (int f = 0; f < TAMANO; f++) {
            for (int c = 0; c < TAMANO; c++) {
                if (celdas[f][c] == VACIO) {
                    return false;
                }
            }
        }
        return true;
    }

    /** true si el tablero está lleno y nadie ganó. */
    public boolean hayEmpate(char fichaA, char fichaB) {
        return estaLleno() && !hayGanador(fichaA) && !hayGanador(fichaB);
    }
}
