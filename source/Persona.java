import java.math.BigDecimal;
import java.util.*;


public abstract class Persona {
    private final int id;
    private final String nombres;
    private final String numeroDoc;
    private Usuario usuario;
    // Unica lista de pagos del sistema: GestorPagos no guarda otra copia.
    private final List<Pago> pagos = new ArrayList<>();

    public Persona(int id, String nombres, String nroDoc) {
        this.id = id;
        this.nombres = nombres;
        this.numeroDoc = nroDoc;
    }

    public BigDecimal verDeuda() {
        BigDecimal deuda = BigDecimal.ZERO;
        for (Pago p : pagos) {
            if (p.getEstado() == EstadoPago.PENDIENTE || p.getEstado() == EstadoPago.RECHAZADO) {
                deuda = deuda.add(p.getMonto());
            }
        }
        return deuda;
    }

    // Descuento como fraccion entre 0 y 1 (0.10 = 10 %). Por defecto no hay descuento. 
    public BigDecimal getDescuento() {
        return BigDecimal.ZERO;
    }

    public abstract String getTipo();

    public void agregarPago(Pago p) {
        pagos.add(p);
    }

    public int getId() {
        return id;
    }
    public String getNombres() {
        return nombres;
    }
    public String getNumeroDoc() {
        return numeroDoc;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + getTipo() + " - " + nombres + " (Doc: " + numeroDoc + ")";
    }
}
