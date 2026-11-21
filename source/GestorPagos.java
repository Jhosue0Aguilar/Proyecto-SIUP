import java.util.*;

public class GestorPagos {
    private List<Persona> personas = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();
    private List<ConceptoPago> conceptos = new ArrayList<>();
    private Persona personaSesion;
    private int siguienteId = 1;

    public GestorPagos() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        conceptos.add(new ConceptoPago("Academico", "Matricula", 350.0));
        conceptos.add(new ConceptoPago("Academico", "Pension mensual", 200.0));
        conceptos.add(new ConceptoPago("Administrativo", "Constancia de estudios", 15.0));
        conceptos.add(new ConceptoPago("Servicio", "Alquiler de auditorio", 500.0));

        Rol admin = new Rol("Administrador", "TODO");
        Rol cajero = new Rol("Cajero", "REGISTRAR_PAGOS");

        Docente d = new Docente(siguienteId++, "Administrador del sistema", "00000000", "D-001", "Sistemas");
        d.setUsuario(new Usuario("admin", "1234", admin));
        personas.add(d);

        Estudiante e = new Estudiante(siguienteId++, "Yael Mendoza", "71234567", "20250001", "Ing. de Sistemas");
        e.setUsuario(new Usuario("yaelmelagoza", "67123", cajero));
        personas.add(e);
    }

    public boolean iniciarSesion(String usuario, String clave) {
        for (Persona p : personas) {
            if (p.getUsuario() != null && p.getUsuario().iniciarSesion(usuario, clave)) {
                personaSesion = p;
                return true;
            }
        }
        return false;
    }

    public void cerrarSesion() {
        personaSesion = null;
    }

    /** tipo: 1 = Estudiante, 2 = Docente, 3 = Externo */
    public Persona registrarPersona(int tipo, String nombres, String doc, String dato1, String dato2) {
        Persona p;
        if (tipo == 1) {
            p = new Estudiante(siguienteId++, nombres, doc, dato1, dato2);
        } else if (tipo == 2) {
            p = new Docente(siguienteId++, nombres, doc, dato1, dato2);
        } else {
            p = new Externo(siguienteId++, nombres, doc, dato1, dato2);
        }
        p.setUsuario(new Usuario(doc, doc, new Rol("Usuario", "CONSULTAR")));
        personas.add(p);
        return p;
    }

    public Pago registrarPago(Persona persona, ConceptoPago concepto) {
        Pago pago = new Pago(persona, personaSesion.getUsuario(), concepto);
        pagos.add(pago);
        return pago;
    }

    public boolean tienePermiso(String permiso) {
        if (personaSesion == null || personaSesion.getUsuario() == null) return false;
        String permisos = personaSesion.getUsuario().getRol().getPermisos();
        return permisos.equals("TODO") || permisos.equals(permiso);
    }

    public Persona getPersonaSesion() { return personaSesion; }
    public List<Persona> getPersonas() { return personas; }
    public List<Pago> getPagos() { return pagos; }
    public List<ConceptoPago> getConceptos() { return conceptos; }
}