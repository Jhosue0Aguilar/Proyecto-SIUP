import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelPagos extends JPanel {
    private GestorPagos gestor;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    private JButton btnRegistrar = new JButton("Registrar pago");
    private JButton btnProcesar = new JButton("Procesar");
    private JButton btnAnular = new JButton("Anular");
    private JButton btnReembolsar = new JButton("Reembolsar");
    private JButton btnComprobante = new JButton("Ver comprobante");

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
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnProcesar);
        botones.add(btnAnular);
        botones.add(btnReembolsar);
        botones.add(btnComprobante);
        add(botones, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> registrarPago());
        btnProcesar.addActionListener(e -> procesarPago());
        btnAnular.addActionListener(e -> anularPago());
        btnReembolsar.addActionListener(e -> reembolsarPago());
        btnComprobante.addActionListener(e -> verComprobante());

        refrescar();
    }

    private Pago pagoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la tabla.");
            return null;
        }
        return gestor.getPagos().get(fila);
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

        Pago pago = gestor.registrarPago((Persona) cboPersona.getSelectedItem(),
                (ConceptoPago) cboConcepto.getSelectedItem());
        refrescar();
        JOptionPane.showMessageDialog(this, "Pago registrado como PENDIENTE:\n" + pago);
    }

    public void procesarPago() {
        Pago pago = pagoSeleccionado();
        if (pago == null) return;
        MetodoPago metodo = new DialogoProcesarPago(SwingUtilities.getWindowAncestor(this)).mostrar();
        if (metodo == null) return;

        EstadoPago antes = pago.getEstado();
        pago.setMetodoPago(metodo);
        pago.procesar();
        refrescar();

        if (pago.getEstado() == EstadoPago.PAGADO) {
            JOptionPane.showMessageDialog(this, "Pago procesado correctamente.");
            mostrarComprobante(pago);
        } else if (pago.getEstado() == EstadoPago.RECHAZADO) {
            JOptionPane.showMessageDialog(this, "Pago RECHAZADO: metodo de pago invalido.",
                    "Rechazado", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Este pago no se puede procesar (estado: " + antes + ").");
        }
    }

    public void anularPago() {
        Pago pago = pagoSeleccionado();
        if (pago == null) return;
        EstadoPago antes = pago.getEstado();
        pago.anular();
        refrescar();
        if (pago.getEstado() == antes) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden anular pagos pendientes o rechazados.");
        } else {
            JOptionPane.showMessageDialog(this, "Pago anulado.");
        }
    }

    public void reembolsarPago() {
        Pago pago = pagoSeleccionado();
        if (pago == null) return;
        if (pago.getEstado() != EstadoPago.PAGADO) {
            JOptionPane.showMessageDialog(this, "Solo se pueden reembolsar pagos en estado PAGADO.");
            return;
        }
        String motivo = JOptionPane.showInputDialog(this, "Motivo del reembolso:");
        if (motivo == null || motivo.trim().isEmpty()) return;
        pago.solicitarReembolso(motivo.trim());
        refrescar();
        JOptionPane.showMessageDialog(this, "Reembolso aprobado.");
    }

    public void verComprobante() {
        Pago pago = pagoSeleccionado();
        if (pago == null) return;
        if (pago.getComprobante() == null) {
            JOptionPane.showMessageDialog(this, "Este pago aun no tiene comprobante.");
            return;
        }
        mostrarComprobante(pago);
    }

    private void mostrarComprobante(Pago pago) {
        Comprobante c = pago.getComprobante();
        String texto = "===== COMPROBANTE =====\n"
                + "Numero  : " + c.getNumero() + "\n"
                + "Fecha   : " + pago.getFecha() + "\n"
                + "Cliente : " + pago.getPersona().getNombres() + "\n"
                + "Concepto: " + pago.getConcepto().getNombre() + "\n"
                + "Metodo  : " + pago.getMetodoPago().getTipo()
                + " (Op: " + pago.getMetodoPago().getNumeroOp() + ")\n"
                + "Total   : S/ " + String.format("%.2f", c.getTotal()) + "\n"
                + "=======================";
        JTextArea area = new JTextArea(texto);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, area, "Comprobante", JOptionPane.PLAIN_MESSAGE);
    }

    public void refrescar() {
        modeloTabla.setRowCount(0);
        List<Pago> pagos = gestor.getPagos();
        for (int i = 0; i < pagos.size(); i++) {
            Pago p = pagos.get(i);
            modeloTabla.addRow(new Object[]{
                    i + 1, p.getFecha(), p.getPersona().getNombres(), p.getConcepto().getNombre(),
                    String.format("%.2f", p.getMonto()), p.getEstado()});
        }
    }

    public void aplicarPermisos() {
        boolean cajero = gestor.tienePermiso("REGISTRAR_PAGOS");
        boolean admin = gestor.tienePermiso("TODO");
        btnRegistrar.setEnabled(cajero);
        btnProcesar.setEnabled(cajero);
        btnComprobante.setEnabled(cajero);
        btnAnular.setEnabled(admin);
        btnReembolsar.setEnabled(admin);
    }
}