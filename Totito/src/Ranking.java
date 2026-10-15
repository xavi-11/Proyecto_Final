import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Guarda y muestra las partidas ganadas más rápidas (top 10).
 * Los datos se almacenan en un archivo de texto (ranking.txt) con una línea por registro:
 *     nombre;milisegundos;jugadas
 * Así el ranking se conserva aunque se cierre el programa.
 */
public class Ranking {

    public static final int MAX_REGISTROS = 10;

    /** Una partida ganada: quién la ganó, cuánto tardó y cuántas jugadas hizo. */
    public static class Registro {
        private final String nombre;
        private final long milis;
        private final int jugadas;

        public Registro(String nombre, long milis, int jugadas) {
            // El ';' es el separador del archivo, así que se elimina del nombre.
            String limpio = nombre == null ? "" : nombre.replace(';', ' ').trim();
            this.nombre = limpio.isEmpty() ? "Anónimo" : limpio;
            this.milis = milis;
            this.jugadas = jugadas;
        }

        public String getNombre() { return nombre; }
        public long getMilis() { return milis; }
        public int getJugadas() { return jugadas; }
    }

    private final Path archivo;
    private final List<Registro> registros = new ArrayList<>();

    public Ranking(String nombreArchivo) {
        this.archivo = Paths.get(nombreArchivo);
        cargar();
    }

    /** Lee el archivo (si existe) y deja los registros ordenados. */
    private void cargar() {
        registros.clear();
        if (!Files.exists(archivo)) {
            return;
        }
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                String[] partes = linea.split(";");
                if (partes.length == 3) {
                    try {
                        registros.add(new Registro(partes[0],
                                Long.parseLong(partes[1].trim()),
                                Integer.parseInt(partes[2].trim())));
                    } catch (NumberFormatException e) {
                        // Línea dañada: se ignora y se sigue con las demás
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer el ranking: " + e.getMessage());
        }
        ordenar();
    }

    /** Ordena de la más rápida a la más lenta y conserva solo las mejores. */
    private void ordenar() {
        registros.sort(Comparator.comparingLong(Registro::getMilis)
                                 .thenComparingInt(Registro::getJugadas));
        while (registros.size() > MAX_REGISTROS) {
            registros.remove(registros.size() - 1);
        }
    }

    private void guardar() {
        List<String> lineas = new ArrayList<>();
        for (Registro r : registros) {
            lineas.add(r.getNombre() + ";" + r.getMilis() + ";" + r.getJugadas());
        }
        try {
            Files.write(archivo, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el ranking: " + e.getMessage());
        }
    }

    /**
     * Agrega una partida al ranking y lo guarda en el archivo.
     * @return true si la partida quedó dentro del top 10.
     */
    public boolean agregar(Registro nuevo) {
        registros.add(nuevo);
        ordenar();
        boolean entro = registros.contains(nuevo);
        guardar();
        return entro;
    }

    public List<Registro> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    public static String formatoTiempo(long milis) {
        return String.format("%.1f s", milis / 1000.0);
    }

    /** Texto listo para mostrar en pantalla. */
    public String comoTexto() {
        if (registros.isEmpty()) {
            return "Aún no hay partidas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        int pos = 1;
        for (Registro r : registros) {
            String nombre = r.getNombre();
            if (nombre.length() > 12) {
                nombre = nombre.substring(0, 11) + ".";
            }
            sb.append(String.format("%2d. %-12s %8s  (%d jug.)%n",
                    pos++, nombre, formatoTiempo(r.getMilis()), r.getJugadas()));
        }
        return sb.toString();
    }
}
