import javax.swing.*;
import java.awt.*;

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
        String c = new String(txtClave.getPassword()).trim();
        if (gestor.iniciarSesion(u, c)) {
            new VentanaPrincipal(gestor).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario inactivo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
        }
    }
}