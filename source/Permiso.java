public enum Permiso {
    REGISTRAR_PERSONA,
    REGISTRAR_PAGO,
    PROCESAR_PAGO,
    ANULAR_PAGO,
    REEMBOLSAR,          // solicitar un reembolso
    APROBAR_REEMBOLSO,   // aprobar o rechazar una solicitud de reembolso
    VER_TODO             // ver los pagos y personas de todos (sin este permiso solo se ven los propios)
}
