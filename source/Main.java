import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GestorPagos gestor = new GestorPagos();
                new VentanaLogin(gestor).setVisible(true);
            }
        });
    }
}