import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorPagos gestor = new GestorPagos();
            new VentanaLogin(gestor).setVisible(true);
        });
    }
}