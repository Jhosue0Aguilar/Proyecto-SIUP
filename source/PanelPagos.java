import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelPagos extends JPanel {
    private final GestorPagos gestor;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    // Pagos mostrados, en el mismo orden que las filas de la tabla.
    private List<Pago> pagosVisibles = new ArrayList<>();

    private final JButton btnRegistrar = new JButton("Registrar pago");
    private final JButton btnProcesar = new JButton("Procesar");
    private final JButton btnAnular = new JButton("Anular");
    private final JButton btnReembolsar = new JButton("Solicitar reembolso");
    private final JButton btnComprobante = new JButton("Ver comprobante");
    private final JButton btnHistorial = new JButton("Historial");

    public PanelPagos(GestorPagos gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        modeloTabla = new DefaultTableModel(
                new String[]{"N°", "Fecha", "Persona", "Concepto", "Monto (S/)", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarBotones();
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnProcesar);
        botones.add(btnAnular);
        botones.add(btnReembolsar);
        botones.add(btnComprobante);
        botones.add(btnHistorial);
        add(botones, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> registrarPago());
        btnProcesar.addActionListener(e -> procesarPago());
        btnAnular.addActionListener(e -> anularPago());
        btnReembolsar.addActionListener(e -> reembolsarPago());
        btnComprobante.addActionListener(e -> verComprobante());
        btnHistorial.addActionListener(e -> verHistorial());

        refrescar();
    }

    private Pago pagoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= pagosVisibles.size()) return null;
        return pagosVisibles.get(fila);
    }

    private Pago exigirSeleccion() {
        Pago pago = pagoSeleccionado();
        if (pago == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la tabla.");
        }
        return pago;
    }

    private void mostrarError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }

    public void registrarPago() {
        JComboBox<Persona> cboPersona = new JComboBox<>(gestor.getPersonas().toArray(new Persona[0]));
        JComboBox<ConceptoPago> cboConcepto = new JComboBox<>(gestor.getConceptos().toArray(new ConceptoPago[0]));
        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Persona:"));
        form.add(cboPersona);
        form.add(new JLabel("Concepto:"));
        form.add(cboConcepto);

        int op = JOptionPane.showConfirmDialog(this, form, "Registrar pago",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;

        try {
            Pago pago = gestor.registrarPago((Persona) cboPersona.getSelectedItem(),
                    (ConceptoPago) cboConcepto.getSelectedItem());
            refrescar();
            JOptionPane.showMessageDialog(this, "Pago registrado como PENDIENTE:\n" + pago);
        } 
        catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            mostrarError(ex);
        }
    }

    public void procesarPago() {
        Pago pago = exigirSeleccion();
        if (pago == null) return;
        MetodoPago metodo = new DialogoProcesarPago(SwingUtilities.getWindowAncestor(this)).mostrar();
        if (metodo == null) return;

        try {
            String mensaje = gestor.procesarPago(pago, metodo);
            refrescar();
            if (pago.getEstado() == EstadoPago.PAGADO) {
                JOptionPane.showMessageDialog(this, mensaje);
                mostrarComprobante(pago);
            } else {
                JOptionPane.showMessageDialog(this, mensaje, "Rechazado", JOptionPane.WARNING_MESSAGE);
            }
        } 
        catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            mostrarError(ex);
            refrescar();
        }
    }

    public void anularPago() {
        Pago pago = exigirSeleccion();
        if (pago == null) return;
        try {
            String mensaje = gestor.anularPago(pago);
            refrescar();
            JOptionPane.showMessageDialog(this, mensaje);
        } 
        catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            mostrarError(ex);
            refrescar();
        }
    }

    // Si el pago tiene un reembolso pendiente lo resuelve (aprobar/rechazar); si no, lo solicita. 
    public void reembolsarPago() {
        Pago pago = exigirSeleccion();
        if (pago == null) return;
        try {
            if (pago.tieneReembolsoPendiente()) {
                resolverReembolso(pago);
            } 
            else {
                solicitarReembolso(pago);
            }
        } 
        catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            mostrarError(ex);
            refrescar();
        }
    }

    private void solicitarReembolso(Pago pago) {
        String motivo = JOptionPane.showInputDialog(this, "Motivo del reembolso:");
        if (motivo == null || motivo.trim().isEmpty()) return;
        String mensaje = gestor.solicitarReembolso(pago, motivo.trim());
        refrescar();
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void resolverReembolso(Pago pago) {
        Reembolso r = pago.getReembolso();
        String detalle = "Solicitado por: " + r.getSolicitadoPor().getUsuario()
                + " (" + Formato.fecha(r.getFecha()) + ")\n"
                + "Monto : S/ " + Formato.monto(r.getMonto()) + "\n"
                + "Motivo: " + r.getMotivo();
        Object[] opciones = {"Aprobar", "Rechazar", "Cancelar"};
        int eleccion = JOptionPane.showOptionDialog(this, detalle, "Resolver reembolso",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[2]);
        String mensaje;
        if (eleccion == 0) {
            mensaje = gestor.aprobarReembolso(pago);
        } 
        else if (eleccion == 1) {
            mensaje = gestor.rechazarReembolso(pago);
        } 
        else {
            return;
        }
        refrescar();
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void verComprobante() {
        Pago pago = exigirSeleccion();
        if (pago == null) return;
        if (pago.getComprobante() == null) {
            JOptionPane.showMessageDialog(this, "Este pago aun no tiene comprobante.");
            return;
        }
        mostrarComprobante(pago);
    }

    public void verHistorial() {
        Pago pago = exigirSeleccion();
        if (pago == null) return;
        JTextArea area = new JTextArea(String.join("\n", pago.getHistorial()), 10, 60);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Historial del pago #" + pago.getId(), JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarComprobante(Pago pago) {
        JTextArea area = new JTextArea(pago.getComprobante().generar(pago));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, area, "Comprobante", JOptionPane.PLAIN_MESSAGE);
    }

    private static String textoEstado(Pago p) {
        return p.tieneReembolsoPendiente() ? p.getEstado() + " (reembolso pendiente)" : p.getEstado().toString();
    }

    public void refrescar() {
        Pago seleccionado = pagoSeleccionado();
        modeloTabla.setRowCount(0);
        pagosVisibles = new ArrayList<>(gestor.getPagos());
        int filaSeleccionada = -1;
        for (int i = 0; i < pagosVisibles.size(); i++) {
            Pago p = pagosVisibles.get(i);
            if (p == seleccionado) filaSeleccionada = i;
            modeloTabla.addRow(new Object[]{
                    p.getId(), Formato.fecha(p.getFecha()), p.getPersona().getNombres(),
                    p.getConcepto().getNombre(), Formato.monto(p.getMonto()), textoEstado(p)});
        }
        if (filaSeleccionada >= 0) tabla.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        actualizarBotones();
    }

    // Habilita cada boton segun el permiso de la sesion y el estado del pago seleccionado. 
    private void actualizarBotones() {
        Pago p = pagoSeleccionado();
        boolean hayPago = p != null;

        btnRegistrar.setEnabled(gestor.tienePermiso(Permiso.REGISTRAR_PAGO));
        btnProcesar.setEnabled(hayPago && p.puedeProcesarse() && gestor.tienePermiso(Permiso.PROCESAR_PAGO));
        btnAnular.setEnabled(hayPago && p.puedeAnularse() && gestor.tienePermiso(Permiso.ANULAR_PAGO));

        boolean pendiente = hayPago && p.tieneReembolsoPendiente();
        btnReembolsar.setText(pendiente ? "Resolver reembolso" : "Solicitar reembolso");
        btnReembolsar.setEnabled(hayPago && (pendiente
                ? gestor.tienePermiso(Permiso.APROBAR_REEMBOLSO)
                : p.puedeSolicitarReembolso() && gestor.tienePermiso(Permiso.REEMBOLSAR)));

        btnComprobante.setEnabled(hayPago && p.getComprobante() != null);
        btnHistorial.setEnabled(hayPago);
    }

    public void aplicarPermisos() {
        actualizarBotones();
    }
}
