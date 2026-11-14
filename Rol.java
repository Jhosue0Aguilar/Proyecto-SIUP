public class Rol {
    private String nombre;
    private String permisos;

    public Rol(String nombre, String permisos) {
        this.nombre = nombre;
        this.permisos = permisos;
    }

    public String getNombre() { return nombre; }
    public String getPermisos() { return permisos; }

    @Override
    public String toString() { return nombre + " (" + permisos + ")"; }
}
