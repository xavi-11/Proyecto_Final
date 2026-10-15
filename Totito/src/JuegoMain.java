import javax.swing.SwingUtilities;

/**
 * Punto de entrada del programa.
 */
public class JuegoMain {
    public static void main(String[] args) {
        // Swing debe iniciarse en el hilo de eventos (EDT)
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}
