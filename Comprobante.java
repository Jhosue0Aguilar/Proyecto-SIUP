public class Comprobante {
    private static int contador = 1;

    private String numero;
    private Double total;

    public Comprobante(Double total) {
        this.numero = String.format("C-%04d", contador++);
        this.total = total;
    }

    public void generar(Pago pago) {
        System.out.println("\n===== COMPROBANTE =====");
        System.out.println("Numero  : " + numero);
        System.out.println("Fecha   : " + pago.getFecha());
        System.out.println("Cliente : " + pago.getPersona().getNombres());
        System.out.println("Concepto: " + pago.getConcepto().getNombre());
        System.out.println("Metodo  : " + pago.getMetodoPago().getTipo()
                + " (Op: " + pago.getMetodoPago().getNumeroOp() + ")");
        System.out.println("Total   : S/ " + String.format("%.2f", total));
        System.out.println("=======================");
    }

    public String getNumero() { return numero; }
    public Double getTotal() { return total; }
}
