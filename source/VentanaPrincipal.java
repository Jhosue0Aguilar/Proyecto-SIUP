import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private GestorPagos gestor;
    private JTabbedPane pestanas = new JTabbedPane();
    private PanelPagos panelPagos;
    private PanelPersonas panelPersonas;

    public VentanaPrincipal(GestorPagos gestor) {
        this.gestor = gestor;
        Persona sesion = gestor.getPersonaSesion();
        setTitle("Sistema de Pagos - " + sesion.getNombres()
                + " (" + sesion.getUsuario().getRol().getNombre() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);

        panelPagos = new PanelPagos(gestor);
        panelPersonas = new PanelPersonas(gestor);
        pestanas.addTab("Pagos", panelPagos);
        pestanas.addTab("Personas", panelPersonas);
        pestanas.addChangeListener(e -> {
            panelPagos.refrescar();
            panelPersonas.refrescar();
        });

        JButton btnSalir = new JButton("Cerrar sesion");
        btnSalir.addActionListener(e -> cerrarSesion());
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        arriba.add(btnSalir);

        setLayout(new BorderLayout());
        add(arriba, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);

        aplicarPermisos();
    }

    private void aplicarPermisos() {
        panelPagos.aplicarPermisos();
        panelPersonas.aplicarPermisos();
    }

    public void cerrarSesion() {
        gestor.cerrarSesion();
        new VentanaLogin(gestor).setVisible(true);
        dispose();
    }
}