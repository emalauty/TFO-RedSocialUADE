import com.formdev.flatlaf.FlatLightLaf;
import vista.VentanaPrincipal;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 1. Instala el Look and Feel moderno (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize LaF: " + e.getMessage());
        }

        // 2. Inicia la ventana (igual que antes)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}