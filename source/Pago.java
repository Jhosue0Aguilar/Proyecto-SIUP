import java.time.LocalDate;

public class Pago {
    private LocalDate fecha;
    private Double monto;
    private EstadoPago estado;

    private Persona persona;        
    private Usuario registradoPor;    
    private ConceptoPago concepto;   
    private MetodoPago metodoPago;    
    private Comprobante comprobante;
    private Reembolso reembolso;      

    public Pago(Persona persona, Usuario registradoPor, ConceptoPago concepto) {
        this.fecha = LocalDate.now();
        this.persona = persona;
        this.registradoPor = registradoPor;
        this.concepto = concepto;
        this.monto = concepto.calcularMonto();
        this.estado = EstadoPago.PENDIENTE;
        persona.agregarPago(this);
    }

    public void procesar() {
        if (estado != EstadoPago.PENDIENTE && estado != EstadoPago.RECHAZADO) {
            System.out.println("Este pago no se puede procesar (estado: " + estado + ").");
            return;
        }
        if (metodoPago != null && metodoPago.validar()) {
            estado = EstadoPago.PAGADO;
            comprobante = new Comprobante(monto);
            comprobante.generar(this);
        } 
        else {
            estado = EstadoPago.RECHAZADO;
            System.out.println("Pago RECHAZADO: metodo de pago invalido.");
        }
    }

    public void anular() {
        if (estado == EstadoPago.PENDIENTE || estado == EstadoPago.RECHAZADO) {
            estado = EstadoPago.ANULADO;
            System.out.println("Pago anulado.");
        } 
        else {
            System.out.println("Solo se pueden anular pagos pendientes o rechazados.");
        }
    }

    public void solicitarReembolso(String motivo) {
        if (estado != EstadoPago.PAGADO) {
            System.out.println("Solo se pueden reembolsar pagos en estado PAGADO.");
            return;
        }
        reembolso = new Reembolso(monto, motivo);
        reembolso.aprobar();
        estado = EstadoPago.REEMBOLSADO;
    }

    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public LocalDate getFecha() { 
        return fecha; 
    }
    public Double getMonto() { 
        return monto; 
    }
    public EstadoPago getEstado() { 
        return estado; 
    }
    public Persona getPersona() { 
        return persona; 
    }
    public Usuario getRegistradoPor() { 
        return registradoPor; 
    }
    public ConceptoPago getConcepto() { 
        return concepto; 
    }
    public MetodoPago getMetodoPago() { 
        return metodoPago; 
    }
    public Comprobante getComprobante() { 
        return comprobante; 
    }

    @Override
    public String toString() {
        return fecha + " | " + persona.getNombres() + " | " + concepto.getNombre()
                + " | S/ " + String.format("%.2f", monto) + " | " + estado;
    }
}
