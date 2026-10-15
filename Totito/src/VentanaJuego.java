import javax.swing.*;
import java.awt.*;

/**
 * Interfaz gráfica (Swing) y control del flujo de la partida.
 * Usa solamente javax.swing y java.awt.
 */
public class VentanaJuego extends JFrame {

    private final Tablero tablero = new Tablero();
    private final Jugador humano = new Jugador("Jugador", 'X');
    private final Jugador computadora = new Jugador("Computadora", 'O');
    private final IA_Minimax ia = new IA_Minimax(computadora.getFicha(), humano.getFicha());

    private final JButton[][] botones = new JButton[3][3];
    private final JLabel lblEstado = new JLabel("", SwingConstants.CENTER);

    private boolean partidaTerminada = false;
    private boolean turnoHumano = true;

    private static final Font FUENTE_FICHA = new Font("SansSerif", Font.BOLD, 64);
    private static final Color COLOR_X = new Color(30, 100, 200);
    private static final Color COLOR_O = new Color(200, 50, 50);
    private static final Color COLOR_GANADOR = new Color(144, 238, 144);

    public VentanaJuego() {
        super("Totito - Proyecto Final Programación II");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // ---- Panel superior: estado del juego (turno / ganador / empate) ----
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblEstado, BorderLayout.NORTH);

        // ---- Panel central: cuadrícula 3x3 ----
        JPanel panelTablero = new JPanel(new GridLayout(3, 3, 4, 4));
        panelTablero.setBackground(Color.BLACK);
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 3; c++) {
                JButton b = new JButton();
                b.setFont(FUENTE_FICHA);
                b.setFocusPainted(false);
                b.setBackground(Color.WHITE);
                final int fila = f, col = c;
                b.addActionListener(e -> alHacerClic(fila, col));
                botones[f][c] = b;
                panelTablero.add(b);
            }
        }
        panelTablero.setPreferredSize(new Dimension(380, 380));
        add(panelTablero, BorderLayout.CENTER);

        // ---- Panel inferior: botón de nueva partida ----
        JButton btnReiniciar = new JButton("Nueva Partida / Reiniciar");
        btnReiniciar.addActionListener(e -> reiniciar());
        add(btnReiniciar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        reiniciar();
    }

    /** Se ejecuta cuando el usuario presiona una casilla. */
    private void alHacerClic(int fila, int col) {
        if (partidaTerminada || !turnoHumano) {
            return;
        }
        // Validación: la casilla debe estar libre
        if (!tablero.colocar(fila, col, humano.getFicha())) {
            return;
        }
        dibujarFicha(fila, col, humano.getFicha());

        if (revisarFinDePartida()) {
            return;
        }

        // Turno de la IA (con una pausa corta para que se note el cambio de turno)
        turnoHumano = false;
        lblEstado.setText("Turno de la Computadora (O)...");
        Timer temporizador = new Timer(400, e -> jugarIA());
        temporizador.setRepeats(false);
        temporizador.start();
    }

    /** La IA elige su jugada con Minimax y la ejecuta. */
    private void jugarIA() {
        int[] jugada = ia.mejorMovimiento(tablero);
        if (jugada != null) {
            tablero.colocar(jugada[0], jugada[1], computadora.getFicha());
            dibujarFicha(jugada[0], jugada[1], computadora.getFicha());
        }
        if (!revisarFinDePartida()) {
            turnoHumano = true;
            lblEstado.setText("Tu turno (X)");
        }
    }

    /** Muestra la ficha y deshabilita el botón para evitar tiros duplicados. */
    private void dibujarFicha(int fila, int col, char ficha) {
        JButton b = botones[fila][col];
        b.setText(String.valueOf(ficha));
        b.setForeground(ficha == 'X' ? COLOR_X : COLOR_O);
        b.setEnabled(false);
        // Al deshabilitar, Swing pinta el texto en gris; este truco mantiene el color.
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            protected void paintText(Graphics g, AbstractButton btn, Rectangle r, String t) {
                g.setColor(btn.getForeground());
                g.setFont(btn.getFont());
                FontMetrics fm = g.getFontMetrics();
                int x = r.x + (r.width - fm.stringWidth(t)) / 2;
                int y = r.y + fm.getAscent();
                g.drawString(t, x, y);
            }
        });
    }

    /**
     * Revisa si hay ganador o empate. Si la partida terminó, bloquea el tablero
     * y muestra el resultado.
     * @return true si la partida terminó.
     */
    private boolean revisarFinDePartida() {
        if (tablero.hayGanador(humano.getFicha())) {
            terminar("¡Ganaste! (X)", tablero.lineaGanadora(humano.getFicha()));
            return true;
        }
        if (tablero.hayGanador(computadora.getFicha())) {
            terminar("Ganó la Computadora (O)", tablero.lineaGanadora(computadora.getFicha()));
            return true;
        }
        if (tablero.estaLleno()) {
            terminar("¡Empate!", null);
            return true;
        }
        return false;
    }

    private void terminar(String mensaje, int[][] lineaGanadora) {
        partidaTerminada = true;
        lblEstado.setText(mensaje);
        if (lineaGanadora != null) {
            for (int[] p : lineaGanadora) {
                botones[p[0]][p[1]].setBackground(COLOR_GANADOR);
            }
        }
        // Deshabilitar todo lo que siga libre
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 3; c++) {
                botones[f][c].setEnabled(false);
            }
        }
    }

    /** Limpia el tablero y empieza una nueva partida (siempre inicia el jugador). */
    private void reiniciar() {
        tablero.reiniciar();
        partidaTerminada = false;
        turnoHumano = true;
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 3; c++) {
                JButton b = botones[f][c];
                b.updateUI(); // restaurar el aspecto normal del botón
                b.setText("");
                b.setBackground(Color.WHITE);
                b.setEnabled(true);
            }
        }
        lblEstado.setText("Tu turno (X)");
    }
}
