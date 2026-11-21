import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Rol {
    private final String nombre;
    private final Set<Permiso> permisos;

    public Rol(String nombre, Set<Permiso> permisos) {
        this.nombre = nombre;
        EnumSet<Permiso> copia = EnumSet.noneOf(Permiso.class);
        copia.addAll(permisos);
        this.permisos = Collections.unmodifiableSet(copia);
    }

    public boolean tienePermiso(Permiso p) {
        return permisos.contains(p);
    }

    public String getNombre() {
        return nombre;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    @Override
    public String toString() {
        return nombre + " " + permisos;
    }
}
