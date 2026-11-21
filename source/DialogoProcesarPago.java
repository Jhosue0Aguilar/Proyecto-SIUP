import java.awt.*;
import javax.swing.*;

public class DialogoProcesarPago extends JDialog {
    private JComboBox<TipoMetodoPago> cboMetodo = new JComboBox<>(TipoMetodoPago.values());
    private JTextField txtOperacion = new JTextField(15);
    private MetodoPago resultado = null;

    public DialogoProcesarPago(Window padre) {
        super(padre, "Procesar pago", ModalityType.APPLICATION_MODAL);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(new JLabel("Metodo:"));
        form.add(cboMetodo);
        form.add(new JLabel("Nro. operacion (min. 4):"));
        form.add(txtOperacion);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.addActionListener(e -> {
            resultado = new MetodoPago((TipoMetodoPago) cboMetodo.getSelectedItem(), txtOperacion.getText().trim());
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnAceptar);
        botones.add(btnCancelar);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(padre);
    }

    // Muestra el dialogo y devuelve el metodo elegido, o null si se cancelo.
    public MetodoPago mostrar() {
        setVisible(true);
        return resultado;
    }
}
