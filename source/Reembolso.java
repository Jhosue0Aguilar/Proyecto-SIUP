import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Flujo: solicitado (pendiente) -> aprobado o rechazado, guardando quien lo resolvio y cuando. */
public class Reembolso {
    private final BigDecimal monto;
    private final String motivo;
    private final LocalDateTime fecha;          // fecha de la solicitud
    private final Usuario solicitadoPor;
    private boolean resuelto;
    private boolean aprobado;
    private Usuario resueltoPor;
    private LocalDateTime fechaResolucion;

    public Reembolso(BigDecimal monto, String motivo, Usuario solicitadoPor) {
        this.monto = monto;
        this.motivo = motivo;
        this.solicitadoPor = solicitadoPor;
        this.fecha = LocalDateTime.now();
    }

    public void aprobar(Usuario por) {
        resolver(por, true);
    }

    public void rechazar(Usuario por) {
        resolver(por, false);
    }

    private void resolver(Usuario por, boolean aprobar) {
        if (resuelto) {
            throw new IllegalStateException("La solicitud de reembolso ya fue resuelta.");
        }
        resuelto = true;
        aprobado = aprobar;
        resueltoPor = por;
        fechaResolucion = LocalDateTime.now();
    }

    public boolean isPendiente() {
        return !resuelto;
    }
    public boolean isAprobado() {
        return aprobado;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public String getMotivo() {
        return motivo;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public Usuario getSolicitadoPor() {
        return solicitadoPor;
    }
    public Usuario getResueltoPor() {
        return resueltoPor;
    }
    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }
}
