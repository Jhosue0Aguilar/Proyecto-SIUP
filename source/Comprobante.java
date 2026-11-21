import java.math.*;
import java.time.LocalDateTime;

public class Comprobante {
    private static int contador = 1;

    private final String numero;
    private final BigDecimal total;
    private final LocalDateTime fechaEmision;

    public Comprobante(BigDecimal total) {
        this.numero = String.format("C-%04d", contador++);
        this.total = total;
        this.fechaEmision = LocalDateTime.now();
    }

    // Devuelve el texto del comprobante. Es el unico lugar donde se arma. 
    public String generar(Pago pago) {
        MetodoPago metodo = pago.getMetodoPago();
        return "===== COMPROBANTE =====\n"
                + "Numero  : " + numero + "\n"
                + "Fecha   : " + Formato.fecha(fechaEmision) + "\n"
                + "Cliente : " + pago.getPersona().getNombres() + "\n"
                + "Concepto: " + pago.getConcepto().getNombre() + "\n"
                + "Metodo  : " + metodo.getTipo() + " (Op: " + metodo.getNumeroOp() + ")\n"
                + "Total   : S/ " + Formato.monto(total) + "\n"
                + "=======================";
    }

    public String getNumero() {
        return numero;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
}
