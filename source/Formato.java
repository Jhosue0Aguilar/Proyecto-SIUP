import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatos de texto compartidos (montos y fechas) para no repetirlos en cada clase. */
public final class Formato {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Formato() { }

    /** Monto con dos decimales y punto decimal, por ejemplo 350.00 */
    public static String monto(BigDecimal monto) {
        return String.format(Locale.US, "%.2f", monto);
    }

    public static String fecha(LocalDateTime fecha) {
        return fecha == null ? "-" : FECHA_HORA.format(fecha);
    }
}
