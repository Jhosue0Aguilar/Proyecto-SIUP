import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class GestorPagos {
    private final List<Persona> personas = new ArrayList<>();
    private final List<ConceptoPago> conceptos = new ArrayList<>();
    private Persona personaSesion;
    private int siguienteIdPersona = 1;
    private int siguienteIdPago = 1;

    private final Rol rolAdministrador = new Rol("Administrador", EnumSet.allOf(Permiso.class));
    private final Rol rolCajero = new Rol("Cajero",
            EnumSet.of(Permiso.REGISTRAR_PAGO, Permiso.PROCESAR_PAGO, Permiso.VER_TODO));
    private final Rol rolUsuario = new Rol("Usuario", EnumSet.noneOf(Permiso.class));

    public GestorPagos() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        conceptos.add(new ConceptoPago("Academico", "Matricula", new BigDecimal("350.00")));
        conceptos.add(new ConceptoPago("Academico", "Pension mensual", new BigDecimal("200.00")));
        conceptos.add(new ConceptoPago("Administrativo", "Constancia de estudios", new BigDecimal("15.00")));
        conceptos.add(new ConceptoPago("Servicio", "Alquiler de auditorio", new BigDecimal("500.00")));

        // Cuentas de demostracion (se crean directo, sin pasar por registrarPersona).
        Docente d = new Docente(siguienteIdPersona++, "Administrador del sistema", "00000000", "D-001", "Sistemas");
        d.setUsuario(new Usuario("admin", "1234", rolAdministrador));
        personas.add(d);

        Estudiante e = new Estudiante(siguienteIdPersona++, "Yael Mendoza", "71234567", "20250001", "Ing. de Sistemas");
        e.setUsuario(new Usuario("yaelmelagoza", "67123", rolCajero));
        personas.add(e);
    }

    // ---------------------------------------------------------------- sesion

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

    public boolean sesionRequiereCambioClave() {
        return personaSesion != null && personaSesion.getUsuario().isClaveTemporal();
    }

    /** Cambia la clave del usuario con sesion iniciada. */
    public void cambiarClave(String nueva) {
        exigirSesion();
        personaSesion.getUsuario().cambiarClave(nueva);
    }

    public boolean tienePermiso(Permiso permiso) {
        return personaSesion != null
                && personaSesion.getUsuario() != null
                && personaSesion.getUsuario().getRol().tienePermiso(permiso);
    }

    // ------------------------------------------------------------- personas

    /**
     * Registra una persona y le crea un usuario (su nro. de documento) con una clave temporal
     * aleatoria. Devuelve esa clave: es la unica vez que se conoce, luego solo queda su hash.
     * Lanza IllegalArgumentException si los datos no son validos o el documento ya existe.
     */
    public String registrarPersona(TipoPersona tipo, String nombres, String doc, String dato1, String dato2) {
        exigirPermiso(Permiso.REGISTRAR_PERSONA);
        if (tipo == null) {
            throw new IllegalArgumentException("Elige el tipo de persona.");
        }
        nombres = limpiar(nombres);
        doc = limpiar(doc);
        dato1 = limpiar(dato1);
        dato2 = limpiar(dato2);

        if (nombres.isEmpty() || dato1.isEmpty() || dato2.isEmpty()) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }
        if (!Validador.validarDocumento(doc)) {
            throw new IllegalArgumentException("El nro. de documento debe tener 8 digitos.");
        }
        if (tipo == TipoPersona.EXTERNO && !Validador.validarRuc(dato2)) {
            throw new IllegalArgumentException("El RUC debe tener 11 digitos.");
        }
        if (existeDocumento(doc) || existeUsuario(doc)) {
            throw new IllegalArgumentException("Ya existe una persona registrada con ese documento.");
        }

        Persona p = crearPersona(tipo, nombres, doc, dato1, dato2);
        String claveTemporal = Usuario.generarClaveTemporal();
        p.setUsuario(new Usuario(doc, claveTemporal, rolUsuario, true));
        personas.add(p);
        return claveTemporal;
    }

    private Persona crearPersona(TipoPersona tipo, String nombres, String doc, String dato1, String dato2) {
        int id = siguienteIdPersona++;
        switch (tipo) {
            case ESTUDIANTE: return new Estudiante(id, nombres, doc, dato1, dato2);
            case DOCENTE:    return new Docente(id, nombres, doc, dato1, dato2);
            case EXTERNO:    return new Externo(id, nombres, doc, dato1, dato2);
            default: throw new IllegalArgumentException("Tipo de persona no soportado.");
        }
    }

    private boolean existeDocumento(String doc) {
        for (Persona p : personas) {
            if (p.getNumeroDoc().equals(doc)) return true;
        }
        return false;
    }

    private boolean existeUsuario(String nombreUsuario) {
        for (Persona p : personas) {
            if (p.getUsuario() != null && p.getUsuario().getUsuario().equals(nombreUsuario)) return true;
        }
        return false;
    }

    private static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    // ---------------------------------------------------------------- pagos

    public Pago registrarPago(Persona persona, ConceptoPago concepto) {
        exigirPermiso(Permiso.REGISTRAR_PAGO);
        if (persona == null || concepto == null) {
            throw new IllegalArgumentException("Elige una persona y un concepto.");
        }
        Pago pago = new Pago(siguienteIdPago++, persona, personaSesion.getUsuario(), concepto);
        persona.agregarPago(pago);
        return pago;
    }

    public String procesarPago(Pago pago, MetodoPago metodo) {
        exigirPermiso(Permiso.PROCESAR_PAGO);
        exigirAcceso(pago);
        return pago.procesar(metodo, personaSesion.getUsuario());
    }

    public String anularPago(Pago pago) {
        exigirPermiso(Permiso.ANULAR_PAGO);
        exigirAcceso(pago);
        return pago.anular(personaSesion.getUsuario());
    }

    public String solicitarReembolso(Pago pago, String motivo) {
        exigirPermiso(Permiso.REEMBOLSAR);
        exigirAcceso(pago);
        return pago.solicitarReembolso(motivo, personaSesion.getUsuario());
    }

    public String aprobarReembolso(Pago pago) {
        exigirPermiso(Permiso.APROBAR_REEMBOLSO);
        exigirAcceso(pago);
        return pago.aprobarReembolso(personaSesion.getUsuario());
    }

    public String rechazarReembolso(Pago pago) {
        exigirPermiso(Permiso.APROBAR_REEMBOLSO);
        exigirAcceso(pago);
        return pago.rechazarReembolso(personaSesion.getUsuario());
    }

    // ----------------------------------------------------------- consultas

    public Persona getPersonaSesion() {
        return personaSesion;
    }

    /** Personas que la sesion puede ver: todas con VER_TODO, si no solo la propia. */
    public List<Persona> getPersonas() {
        if (tienePermiso(Permiso.VER_TODO)) {
            return Collections.unmodifiableList(new ArrayList<>(personas));
        }
        List<Persona> propias = new ArrayList<>();
        if (personaSesion != null) propias.add(personaSesion);
        return propias;
    }

    /** Pagos que la sesion puede ver (todos con VER_TODO, si no solo los propios), ordenados por numero. */
    public List<Pago> getPagos() {
        List<Pago> visibles = new ArrayList<>();
        for (Persona p : getPersonas()) {
            visibles.addAll(p.getPagos());
        }
        Collections.sort(visibles, new Comparator<Pago>() {
            @Override
            public int compare(Pago a, Pago b) {
                return Integer.compare(a.getId(), b.getId());
            }
        });
        return visibles;
    }

    public List<ConceptoPago> getConceptos() {
        return Collections.unmodifiableList(conceptos);
    }

    // ----------------------------------------------------------- seguridad

    private void exigirSesion() {
        if (personaSesion == null) {
            throw new SecurityException("No hay una sesion iniciada.");
        }
    }

    private void exigirPermiso(Permiso permiso) {
        exigirSesion();
        if (!tienePermiso(permiso)) {
            throw new SecurityException("No tienes permiso para esta operacion (" + permiso + ").");
        }
    }

    private void exigirAcceso(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("Selecciona un pago.");
        }
        if (!tienePermiso(Permiso.VER_TODO) && pago.getPersona() != personaSesion) {
            throw new SecurityException("No puedes operar sobre pagos de otras personas.");
        }
    }
}
