import java.util.ArrayList;
import java.util.List;

public abstract class Persona {
    protected int id;
    protected String nombres;
    protected String nroDoc;
    protected Usuario usuario;
    protected List<Pago> pagos = new ArrayList<>();

    public Persona(int id, String nombres, String nroDoc) {
        this.id = id;
        this.nombres = nombres;
        this.nroDoc = nroDoc;
    }

    // Suma de los pagos aun no cancelados (pendientes o rechazados)
    public double verDeuda() {
        double deuda = 0;
        for (Pago p : pagos) {
            if (p.getEstado() == EstadoPago.PENDIENTE || p.getEstado() == EstadoPago.RECHAZADO) {
                deuda += p.getMonto();
            }
        }
        return deuda;
    }

    public abstract String getTipo();

    public void agregarPago(Pago p) { pagos.add(p); }

    public int getId() { return id; }
    public String getNombres() { return nombres; }
    public String getNroDoc() { return nroDoc; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<Pago> getPagos() { return pagos; }

    @Override
    public String toString() {
        return "[" + id + "] " + getTipo() + " - " + nombres + " (Doc: " + nroDoc + ")";
    }
}
