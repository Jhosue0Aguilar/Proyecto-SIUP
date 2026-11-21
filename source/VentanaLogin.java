import java.awt.*;
import javax.swing.*;

public class VentanaLogin extends JFrame {
    private JTextField txtUsuario = new JTextField(15);
    private JPasswordField txtClave = new JPasswordField(15);
    private GestorPagos gestor;

    public VentanaLogin(GestorPagos gestor) {
        this.gestor = gestor;
        setTitle("Sistema de Pagos - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(new JLabel("Usuario:"));
        form.add(txtUsuario);
        form.add(new JLabel("Clave:"));
        form.add(txtClave);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> ingresar());
        txtClave.addActionListener(e -> ingresar());
        JPanel abajo = new JPanel();
        abajo.add(btnIngresar);

        setLayout(new BorderLayout());
        add(new JLabel("SISTEMA DE PAGOS", SwingConstants.CENTER), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String u = txtUsuario.getText().trim();
        String c = new String(txtClave.getPassword());   // la clave no se recorta: los espacios cuentan
        if (!gestor.iniciarSesion(u, c)) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario inactivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
            return;
        }
        if (gestor.sesionRequiereCambioClave() && !pedirNuevaClave()) {
            gestor.cerrarSesion();
            txtClave.setText("");
            return;
        }
        new VentanaPrincipal(gestor).setVisible(true);
        dispose();
    }

    // Obliga a cambiar la clave temporal. Devuelve false si la persona cancela. 
    private boolean pedirNuevaClave() {
        JPasswordField nueva = new JPasswordField(15);
        JPasswordField repetir = new JPasswordField(15);
        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Nueva clave (min. 8):"));
        form.add(nueva);
        form.add(new JLabel("Repite la clave:"));
        form.add(repetir);

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form, "Debes cambiar tu clave temporal",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) return false;

            String n1 = new String(nueva.getPassword());
            String n2 = new String(repetir.getPassword());
            nueva.setText("");
            repetir.setText("");
            if (!n1.equals(n2)) {
                JOptionPane.showMessageDialog(this, "Las claves no coinciden.", "Error", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            try {
                gestor.cambiarClave(n1);
                return true;
            } 
            catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Clave no valida", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
