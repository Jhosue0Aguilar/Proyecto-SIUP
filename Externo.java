public class Externo extends Persona {
    private String empresa;
    private String ruc;

    public Externo(int id, String nombres, String nroDoc, String empresa, String ruc) {
        super(id, nombres, nroDoc);
        this.empresa = empresa;
        this.ruc = ruc;
    }

    @Override
    public String getTipo() { return "Externo"; }

    @Override
    public String toString() { return super.toString() + " | Empresa: " + empresa + " | RUC: " + ruc; }
}
