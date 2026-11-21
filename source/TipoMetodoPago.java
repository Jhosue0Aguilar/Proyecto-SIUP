public enum TipoMetodoPago {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    YAPE_PLIN("Yape/Plin"),
    TRANSFERENCIA("Transferencia");

    private final String etiqueta;

    TipoMetodoPago(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
