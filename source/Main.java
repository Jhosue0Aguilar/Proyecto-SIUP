import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static List<Persona> personas = new ArrayList<>();
    static List<Pago> pagos = new ArrayList<>();
    static List<ConceptoPago> conceptos = new ArrayList<>();
    static Usuario sesion = null;
    static int siguienteId = 1;

    public static void main(String[] args) {
        cargarDatosIniciales();
        while (true) {
            if (sesion == null) {
                if (!login()) break;
            } 
            else {
                menu();
            }
        }
        System.out.println("Hasta luego.");
    }

    static void cargarDatosIniciales() {
        conceptos.add(new ConceptoPago("Academico", "Matricula", 350.0));
        conceptos.add(new ConceptoPago("Academico", "Pension mensual", 200.0));
        conceptos.add(new ConceptoPago("Administrativo", "Constancia de estudios", 15.0));
        conceptos.add(new ConceptoPago("Servicio", "Alquiler de auditorio", 500.0));

        Rol admin = new Rol("Administrador", "TODO");
        Rol cajero = new Rol("Cajero", "REGISTRAR_PAGOS");
        Docente d = new Docente(siguienteId++, "Administrador del sistema", "00000000", "D-001", "Sistemas");
        d.setUsuario(new Usuario("admin", "1234", admin));
        personas.add(d);

        Estudiante e = new Estudiante(siguienteId++, "Ana Torres", "71234567", "20250001", "Ing. de Sistemas");
        e.setUsuario(new Usuario("ana", "ana123", cajero));
        personas.add(e);
    }

    static boolean login() {
        System.out.println("\n=== SISTEMA DE PAGOS ===");
        System.out.println("(escribe 'salir' como usuario para terminar)");
        System.out.print("Usuario: ");
        String u = sc.nextLine().trim();
        if (u.equalsIgnoreCase("salir")) return false;
        System.out.print("Clave: ");
        String c = sc.nextLine().trim();
        for (Persona p : personas) {
            if (p.getUsuario() != null && p.getUsuario().iniciarSesion(u, c)) {
                sesion = p.getUsuario();
                System.out.println("Bienvenido, " + p.getNombres() + " - Rol: " + sesion.getRol().getNombre());
                return true;
            }
        }
        System.out.println("Credenciales incorrectas o usuario inactivo.");
        return true;
    }

    static void menu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Registrar persona");
        System.out.println("2. Listar personas");
        System.out.println("3. Registrar pago");
        System.out.println("4. Procesar pago");
        System.out.println("5. Ver deuda de una persona");
        System.out.println("6. Listar pagos");
        System.out.println("7. Anular pago");
        System.out.println("8. Reembolsar pago");
        System.out.println("9. Ver comprobante");
        System.out.println("0. Cerrar sesion");
        int op = leerInt("Opcion: ");
        switch (op) {
            case 1:
                registrarPersona();
                break;
            case 2:
                listarPersonas();
                break;
            case 3:
                registrarPago();
                break;
            case 4:
                procesarPago();
                break;
            case 5:
                verDeuda();
                break;
            case 6:
                listarPagos();
                break;
            case 7:
                anularPago();
                break;
            case 8:
                reembolsarPago();
                break;
            case 9:
                verComprobante();
                break;
            case 0:
                cerrarSesion();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    static void registrarPersona() {
        System.out.println("Tipo: 1. Estudiante  2. Docente  3. Externo");
        int tipo = leerInt("Tipo: ");
        if (tipo < 1 || tipo > 3) { 
            System.out.println("Tipo invalido."); 
            return; 
        }
        String nombres = leerTexto("Nombres: ");
        String doc = leerTexto("Nro. documento: ");
        Persona p;
        if (tipo == 1) {
            p = new Estudiante(siguienteId++, nombres, doc, leerTexto("Codigo: "), leerTexto("Carrera: "));
        } 
        else if (tipo == 2) {
            p = new Docente(siguienteId++, nombres, doc, leerTexto("Codigo: "), leerTexto("Departamento: "));
        } 
        else {
            p = new Externo(siguienteId++, nombres, doc, leerTexto("Empresa: "), leerTexto("RUC: "));
        }
        p.setUsuario(new Usuario(doc, doc, new Rol("Usuario", "CONSULTAR")));
        personas.add(p);
        System.out.println("Persona registrada: " + p);
    }

    static void listarPersonas() {
        for (Persona p : personas) 
            System.out.println(p);
    }

    static void registrarPago() {
        Persona p = elegirPersona();
        if (p == null) 
            return;
        System.out.println("Conceptos disponibles:");
        for (int i = 0; i < conceptos.size(); i++) 
            System.out.println((i + 1) + ". " + conceptos.get(i));
        int c = leerInt("Concepto: ");
        if (c < 1 || c > conceptos.size()) { 
            System.out.println("Concepto invalido."); 
            return; 
        }
        Pago pago = new Pago(p, sesion, conceptos.get(c - 1));
        pagos.add(pago);
        System.out.println("Pago registrado como PENDIENTE: " + pago);
    }

    static void procesarPago() {
        Pago pago = elegirPago();
        if (pago == null) 
            return;
        System.out.println("Metodo: 1. Efectivo  2. Tarjeta  3. Yape/Plin  4. Transferencia");
        int m = leerInt("Metodo: ");
        String[] tipos = {"Efectivo", "Tarjeta", "Yape/Plin", "Transferencia"};
        if (m < 1 || m > 4) { 
            System.out.println("Metodo invalido."); 
            return; 
        }
        String numOp = leerTexto("Numero de operacion (min. 4 caracteres): ");
        pago.setMetodoPago(new MetodoPago(tipos[m - 1], numOp));
        pago.procesar();
    }

    static void verDeuda() {
        Persona p = elegirPersona();
        if (p == null) 
            return;
        System.out.println("Deuda de " + p.getNombres() + ": S/ " + String.format("%.2f", p.verDeuda()));
    }

    static void listarPagos() {
        if (pagos.isEmpty()) { 
            System.out.println("No hay pagos registrados.");
            return; 
        }
        for (int i = 0; i < pagos.size(); i++) 
            System.out.println((i + 1) + ". " + pagos.get(i));
    }

    static void anularPago() {
        Pago pago = elegirPago();
        if (pago != null) 
            pago.anular();
    }

    static void reembolsarPago() {
        Pago pago = elegirPago();
        if (pago == null) 
            return;
        pago.solicitarReembolso(leerTexto("Motivo del reembolso: "));
    }

    static void verComprobante() {
        Pago pago = elegirPago();
        if (pago == null) 
            return;
        if (pago.getComprobante() == null) 
            System.out.println("Este pago aun no tiene comprobante.");
        else 
            pago.getComprobante().generar(pago);
    }

    static Persona elegirPersona() {
        listarPersonas();
        int id = leerInt("ID de la persona: ");
        for (Persona p : personas) 
            if (p.getId() == id) 
                return p;
        System.out.println("Persona no encontrada.");
        return null;
    }

    static Pago elegirPago() {
        if (pagos.isEmpty()) { 
            System.out.println("No hay pagos registrados."); 
            return null; }
        listarPagos();
        int n = leerInt("Numero de pago: ");
        if (n < 1 || n > pagos.size()) { 
            System.out.println("Pago no encontrado."); 
            return null; 
        }
        return pagos.get(n - 1);
    }

    static int leerInt(String msg) {
        System.out.print(msg);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } 
        catch (NumberFormatException e) {
            return -1;
        }
    }

    static String leerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    static void cerrarSesion() {
        sesion = null;
        System.out.println("Sesion cerrada.");
    }
}
