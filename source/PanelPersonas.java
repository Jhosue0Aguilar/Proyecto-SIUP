import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelPersonas extends JPanel {
    private GestorPagos gestor;
    private DefaultTableModel modeloTabla;

    private JPanel formulario = new JPanel(new GridLayout(5, 2, 6, 6));
    private JComboBox<String> cboTipo = new JComboBox<>(new String[]{"Estudiante", "Docente", "Externo"});
    private JTextField txtNombres = new JTextField();
    private JTextField txtDoc = new JTextField();
    private JLabel lblDato1 = new JLabel("Codigo:");
    private JLabel lblDato2 = new JLabel("Carrera:");
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
        formulario.add(new JLabel("Nro. documento:"));
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
        switch (cboTipo.getSelectedIndex()) {
            case 0: lblDato1.setText("Codigo:");  lblDato2.setText("Carrera:");      break;
            case 1: lblDato1.setText("Codigo:");  lblDato2.setText("Departamento:"); break;
            default: lblDato1.setText("Empresa:"); lblDato2.setText("RUC:");         break;
        }
    }

    public void registrarPersona() {
        String nombres = txtNombres.getText().trim();
        String doc = txtDoc.getText().trim();
        String d1 = txtDato1.getText().trim();
        String d2 = txtDato2.getText().trim();
        if (nombres.isEmpty() || doc.isEmpty() || d1.isEmpty() || d2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Persona p = gestor.registrarPersona(cboTipo.getSelectedIndex() + 1, nombres, doc, d1, d2);
        JOptionPane.showMessageDialog(this, "Persona registrada:\n" + p);
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
                    p.getId(), p.getTipo(), p.getNombres(), String.format("%.2f", p.verDeuda())});
        }
    }

    /** Solo el rol TODO (administrador) puede registrar personas. */
    public void aplicarPermisos() {
        boolean puede = gestor.tienePermiso("TODO");
        btnRegistrar.setEnabled(puede);
        for (Component c : formulario.getComponents()) c.setEnabled(puede);
    }
}