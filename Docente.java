public class Docente extends Persona {
    private String codigo;
    private String departamento;

    public Docente(int id, String nombres, String nroDoc, String codigo, String departamento) {
        super(id, nombres, nroDoc);
        this.codigo = codigo;
        this.departamento = departamento;
    }

    @Override
    public String getTipo() { return "Docente"; }

    @Override
    public String toString() { return super.toString() + " | Codigo: " + codigo + " | Depto: " + departamento; }
}
