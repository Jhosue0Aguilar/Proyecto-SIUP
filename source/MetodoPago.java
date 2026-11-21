public class MetodoPago {
    private final TipoMetodoPago tipo;
    private final String numeroOp;

    public MetodoPago(TipoMetodoPago tipo, String numeroOp) {
        this.tipo = tipo;
        this.numeroOp = numeroOp;
    }

    public boolean validar() {
        return tipo != null && Validador.validarNumeroOp(numeroOp);
    }

    public TipoMetodoPago getTipo() {
        return tipo;
    }
    public String getNumeroOp() {
        return numeroOp;
    }
}
