import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelPersonas extends JPanel {
    private GestorPagos gestor;
    private DefaultTableModel modeloTabla;

    private JPanel formulario = new JPanel(new GridLayout(5, 2, 6, 6));
    private JComboBox<TipoPersona> cboTipo = new JComboBox<>(TipoPersona.values());
    private JTextField txtNombres = new JTextField();
    private JTextField txtDoc = new JTextField();
    private JLabel lblDato1 = new JLabel(TipoPersona.ESTUDIANTE.getEtiquetaDato1());
    private JLabel lblDato2 = new JLabel(TipoPersona.ESTUDIANTE.getEtiquetaDato2());
    private JTextField txtDato1 = new JTextField();
    private JTextField txtDato2 = new JTextField();
    private JButton btnRegistrar = new JButton("Registrar persona");

    public PanelPersonas(GestorPagos gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Tipo", "Nombres", "Deuda (S/)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        add(new JScrollPane(new JTable(modeloTabla)), BorderLayout.CENTER);

        formulario.setBorder(BorderFactory.createTitledBorder("Nueva persona"));
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cboTipo);
        formulario.add(new JLabel("Nombres:"));
        formulario.add(txtNombres);
        formulario.add(new JLabel("Nro. documento (8 digitos):"));
        formulario.add(txtDoc);
        formulario.add(lblDato1);
        formulario.add(txtDato1);
        formulario.add(lblDato2);
        formulario.add(txtDato2);

        cboTipo.addActionListener(e -> cambiarEtiquetas());
        btnRegistrar.addActionListener(e -> registrarPersona());

        JPanel sur = new JPanel(new BorderLayout(6, 6));
        sur.add(formulario, BorderLayout.CENTER);
        sur.add(btnRegistrar, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        refrescar();
    }

    private void cambiarEtiquetas() {
        TipoPersona tipo = (TipoPersona) cboTipo.getSelectedItem();
        lblDato1.setText(tipo.getEtiquetaDato1());
        lblDato2.setText(tipo.getEtiquetaDato2());
    }

    public void registrarPersona() {
        TipoPersona tipo = (TipoPersona) cboTipo.getSelectedItem();
        String nombres = txtNombres.getText().trim();
        String doc = txtDoc.getText().trim();
        String d1 = txtDato1.getText().trim();
        String d2 = txtDato2.getText().trim();

        String claveTemporal;
        try {
            claveTemporal = gestor.registrarPersona(tipo, nombres, doc, d1, d2);
        } 
        catch (IllegalArgumentException | SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea aviso = new JTextArea(
                "Persona registrada: " + tipo + " - " + nombres + "\n\n"
                + "Usuario        : " + doc + "\n"
                + "Clave temporal : " + claveTemporal + "\n\n"
                + "Entregasela ahora: no se vuelve a mostrar.\n"
                + "Tendra que cambiarla en su primer ingreso.");
        aviso.setEditable(false);
        aviso.setOpaque(false);
        JOptionPane.showMessageDialog(this, aviso, "Persona registrada", JOptionPane.INFORMATION_MESSAGE);

        txtNombres.setText("");
        txtDoc.setText("");
        txtDato1.setText("");
        txtDato2.setText("");
        refrescar();
    }

    public void refrescar() {
        modeloTabla.setRowCount(0);
        for (Persona p : gestor.getPersonas()) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getTipo(), p.getNombres(), Formato.monto(p.verDeuda())});
        }
    }

    // Solo quien tiene el permiso REGISTRAR_PERSONA puede usar el formulario. 
    public void aplicarPermisos() {
        boolean puede = gestor.tienePermiso(Permiso.REGISTRAR_PERSONA);
        btnRegistrar.setEnabled(puede);
        for (Component c : formulario.getComponents()) c.setEnabled(puede);
    }
}
