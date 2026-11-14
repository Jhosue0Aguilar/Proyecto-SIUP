public class Estudiante extends Persona {
    private String codigo;
    private String carrera;

    public Estudiante(int id, String nombres, String nroDoc, String codigo, String carrera) {
        super(id, nombres, nroDoc);
        this.codigo = codigo;
        this.carrera = carrera;
    }

    @Override
    public String getTipo() { return "Estudiante"; }

    @Override
    public String toString() { return super.toString() + " | Codigo: " + codigo + " | Carrera: " + carrera; }
}
