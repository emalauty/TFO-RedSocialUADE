import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Usamos SwingUtilities.invokeLater para asegurar que la GUI se inicie correctamente
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true); // Hacemos visible la ventana
            }
        });
    }
}