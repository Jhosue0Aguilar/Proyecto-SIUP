import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pago {
    private final int id;
    private final LocalDateTime fecha;
    private final BigDecimal monto;
    private EstadoPago estado;

    private final Persona persona;
    private final Usuario registradoPor;
    private final ConceptoPago concepto;
    private MetodoPago metodoPago;
    private Comprobante comprobante;
    private Reembolso reembolso;

    // Auditoria: quien hizo cada accion y cuando.
    private final List<String> historial = new ArrayList<>();

    /** El pago NO se agrega solo a la persona: lo hace GestorPagos.registrarPago. */
    public Pago(int id, Persona persona, Usuario registradoPor, ConceptoPago concepto) {
        this.id = id;
        this.fecha = LocalDateTime.now();
        this.persona = persona;
        this.registradoPor = registradoPor;
        this.concepto = concepto;
        this.monto = concepto.calcularMonto(persona);
        this.estado = EstadoPago.PENDIENTE;
        registrarEvento(registradoPor, "Registrado como PENDIENTE por S/ " + Formato.monto(monto));
    }

    public boolean puedeProcesarse() {
        return estado == EstadoPago.PENDIENTE || estado == EstadoPago.RECHAZADO;
    }

    public boolean puedeAnularse() {
        return estado == EstadoPago.PENDIENTE || estado == EstadoPago.RECHAZADO;
    }

    public boolean tieneReembolsoPendiente() {
        return reembolso != null && reembolso.isPendiente();
    }

    public boolean puedeSolicitarReembolso() {
        return estado == EstadoPago.PAGADO && !tieneReembolsoPendiente();
    }

    /**
     * Cobra el pago. El metodo de pago solo se guarda si el cobro se realiza, asi un intento
     * sobre un pago ya cobrado no cambia los datos del comprobante original.
     * Lanza IllegalStateException si el estado no permite procesar; devuelve el mensaje del resultado.
     */
    public String procesar(MetodoPago metodo, Usuario por) {
        if (!puedeProcesarse()) {
            throw new IllegalStateException("Este pago no se puede procesar (estado: " + estado + ").");
        }
        if (metodo == null || !metodo.validar()) {
            estado = EstadoPago.RECHAZADO;
            registrarEvento(por, "Intento de cobro RECHAZADO: metodo de pago invalido");
            return "Pago RECHAZADO: metodo de pago invalido.";
        }
        metodoPago = metodo;
        estado = EstadoPago.PAGADO;
        comprobante = new Comprobante(monto);
        registrarEvento(por, "Cobrado con " + metodo.getTipo() + " (Op: " + metodo.getNumeroOp()
                + "), comprobante " + comprobante.getNumero());
        return "Pago procesado correctamente. Comprobante " + comprobante.getNumero() + ".";
    }

    public String anular(Usuario por) {
        if (!puedeAnularse()) {
            throw new IllegalStateException("Solo se pueden anular pagos pendientes o rechazados.");
        }
        estado = EstadoPago.ANULADO;
        registrarEvento(por, "Anulado");
        return "Pago anulado.";
    }

    /** Crea una solicitud de reembolso pendiente; el pago sigue PAGADO hasta que se apruebe. */
    public String solicitarReembolso(String motivo, Usuario por) {
        if (estado != EstadoPago.PAGADO) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos en estado PAGADO.");
        }
        if (tieneReembolsoPendiente()) {
            throw new IllegalStateException("Este pago ya tiene una solicitud de reembolso pendiente.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indica el motivo del reembolso.");
        }
        reembolso = new Reembolso(monto, motivo.trim(), por);
        registrarEvento(por, "Reembolso SOLICITADO. Motivo: " + reembolso.getMotivo());
        return "Reembolso solicitado por S/ " + Formato.monto(monto) + ". Queda pendiente de aprobacion.";
    }

    public String aprobarReembolso(Usuario por) {
        exigirReembolsoPendiente();
        reembolso.aprobar(por);
        estado = EstadoPago.REEMBOLSADO;
        registrarEvento(por, "Reembolso APROBADO por S/ " + Formato.monto(reembolso.getMonto()));
        return "Reembolso aprobado por S/ " + Formato.monto(reembolso.getMonto()) + ".";
    }

    public String rechazarReembolso(Usuario por) {
        exigirReembolsoPendiente();
        reembolso.rechazar(por);
        registrarEvento(por, "Reembolso RECHAZADO");
        return "Solicitud de reembolso rechazada. El pago sigue en estado PAGADO.";
    }

    private void exigirReembolsoPendiente() {
        if (!tieneReembolsoPendiente()) {
            throw new IllegalStateException("Este pago no tiene una solicitud de reembolso pendiente.");
        }
    }

    private void registrarEvento(Usuario por, String accion) {
        String quien = (por == null) ? "sistema" : por.getUsuario();
        historial.add(Formato.fecha(LocalDateTime.now()) + " | " + quien + " | " + accion);
    }

    public int getId() {
        return id;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public BigDecimal getMonto() {
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
    public Reembolso getReembolso() {
        return reembolso;
    }
    public List<String> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    @Override
    public String toString() {
        return "#" + id + " | " + Formato.fecha(fecha) + " | " + persona.getNombres() + " | "
                + concepto.getNombre() + " | S/ " + Formato.monto(monto) + " | " + estado;
    }
}
