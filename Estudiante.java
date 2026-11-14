public class Estudiante extends Persona {
    private String codigo;
    private String carrera;

    public Estudiante(int id, String nombres, String numeroDoc, String codigo, String carrera) {
        super(id, nombres, numeroDoc);
        this.codigo = codigo;
        this.carrera = carrera;
    }

    @Override
    public String getTipo() { 
        return "Estudiante"; 
    }

    @Override
    public String toString() { 
        return super.toString() + " | Codigo: " + codigo + " | Carrera: " + carrera; 
    }
}
