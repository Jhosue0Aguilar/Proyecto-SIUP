import java.time.LocalDate;

public class Reembolso {
    private Double monto;
    private String motivo;
    private LocalDate fecha;
    private boolean aprobado;

    public Reembolso(Double monto, String motivo) {
        this.monto = monto;
        this.motivo = motivo;
        this.fecha = LocalDate.now();
        this.aprobado = false;
    }

    public void aprobar() {
        aprobado = true;
        System.out.println("Reembolso aprobado por S/ " + String.format("%.2f", monto)
                + " | Motivo: " + motivo + " | Fecha: " + fecha);
    }

    public boolean isAprobado() { 
        return aprobado; 
    }
}
