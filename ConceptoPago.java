public class ConceptoPago {
    private String tipo;
    private String nombre;
    private Double monto;

    public ConceptoPago(String tipo, String nombre, Double monto) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.monto = monto;
    }

    public double calcularMonto() {
        return monto;
    }

    public String getTipo() { return tipo; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre + " (" + tipo + ") - S/ " + String.format("%.2f", monto); }
}
