import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConceptoPago {
    private final String tipo;
    private final String nombre;
    private final BigDecimal monto;

    public ConceptoPago(String tipo, String nombre, BigDecimal monto) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.monto = monto;
    }

    // Monto a cobrar a esa persona, aplicando su descuento. 
    public BigDecimal calcularMonto(Persona persona) {
        BigDecimal descuento = persona.getDescuento();
        if (descuento.signum() < 0 || descuento.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("El descuento debe estar entre 0 y 1.");
        }
        return monto.multiply(BigDecimal.ONE.subtract(descuento)).setScale(2, RoundingMode.HALF_UP);
    }

    public String getTipo() {
        return tipo;
    }
    public String getNombre() {
        return nombre;
    }
    public BigDecimal getMonto() {
        return monto;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ") - S/ " + Formato.monto(monto);
    }
}
