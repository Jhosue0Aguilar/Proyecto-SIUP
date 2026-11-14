public class MetodoPago {
    private String tipo;
    private String numeroOp;

    public MetodoPago(String tipo, String numeroOp) {
        this.tipo = tipo;
        this.numeroOp = numeroOp;
    }

    // Valido si el numero de operacion no esta vacio y tiene al menos 4 caracteres
    public boolean validar() {
        return numeroOp != null && numeroOp.trim().length() >= 4;
    }

    public String getTipo() { return tipo; }
    public String getNumeroOp() { return numeroOp; }
}
