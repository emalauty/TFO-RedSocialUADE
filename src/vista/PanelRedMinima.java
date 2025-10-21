package vista;

import modelo.grafo.Conexion;
import modelo.grafo.RedSocialGrafo;
import modelo.grafo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelRedMinima extends JPanel {

    private final RedSocialGrafo grafo;
    private final JComboBox<Usuario> comboUsuarioA;
    private final JComboBox<Usuario> comboUsuarioB;
    private final JTextArea areaResultadoCompleto;
    private final JTextArea areaResultadoMinimo;
    private final Runnable actualizadorExterno;

    public PanelRedMinima(RedSocialGrafo grafo, Runnable actualizadorExterno) {
        this.grafo = grafo;
        this.actualizadorExterno = actualizadorExterno;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PANEL DE CONTROLES (IZQUIERDA) ---
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles de la Red"));

        // (El resto del panel de controles es igual...)
        JPanel panelAnadirUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField campoNombreUsuario = new JTextField(15);
        JButton botonAnadirUsuario = new JButton("Añadir Usuario");
        panelAnadirUsuario.add(new JLabel("Nombre:"));
        panelAnadirUsuario.add(campoNombreUsuario);
        panelAnadirUsuario.add(botonAnadirUsuario);

        JPanel panelAnadirConexion = new JPanel(new GridLayout(3, 2, 5, 5));
        panelAnadirConexion.setBorder(BorderFactory.createTitledBorder("Crear Amistad"));
        comboUsuarioA = new JComboBox<>();
        comboUsuarioB = new JComboBox<>();
        JTextField campoCosto = new JTextField(5);
        JButton botonAnadirConexion = new JButton("Añadir Conexión");
        panelAnadirConexion.add(new JLabel("Usuario A:"));
        panelAnadirConexion.add(comboUsuarioA);
        panelAnadirConexion.add(new JLabel("Usuario B:"));
        panelAnadirConexion.add(comboUsuarioB);
        panelAnadirConexion.add(new JLabel("Costo:"));
        panelAnadirConexion.add(campoCosto);

        JButton botonCalcularMST = new JButton("Calcular Red Mínima (Kruskal)");
        botonCalcularMST.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelControles.add(panelAnadirUsuario);
        panelControles.add(Box.createRigidArea(new Dimension(0, 15)));
        panelControles.add(panelAnadirConexion);
        panelControles.add(botonAnadirConexion);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(botonCalcularMST);

        // --- PANEL DE RESULTADOS (CENTRO) ---
        JTabbedPane panelTabsResultados = new JTabbedPane();
        areaResultadoCompleto = new JTextArea("Agrega usuarios y conexiones...");
        areaResultadoCompleto.setEditable(false);
        areaResultadoMinimo = new JTextArea();
        areaResultadoMinimo.setEditable(false);
        panelTabsResultados.addTab("Red Completa", new JScrollPane(areaResultadoCompleto));
        panelTabsResultados.addTab("Red Mínima (MST)", new JScrollPane(areaResultadoMinimo));

        add(panelControles, BorderLayout.WEST);
        add(panelTabsResultados, BorderLayout.CENTER);

        // --- LÓGICA DE BOTONES ---
        botonAnadirUsuario.addActionListener(e -> {
            String nombre = campoNombreUsuario.getText().trim();
            if (!nombre.isEmpty()) {
                Usuario nuevoUsuario = new Usuario(nombre);
                grafo.agregarUsuario(nuevoUsuario);
                actualizarCombos();
                actualizadorExterno.run();
                campoNombreUsuario.setText("");
            }
        });

        botonAnadirConexion.addActionListener(e -> {
            Usuario uA = (Usuario) comboUsuarioA.getSelectedItem();
            Usuario uB = (Usuario) comboUsuarioB.getSelectedItem();
            try {
                int costo = Integer.parseInt(campoCosto.getText().trim());
                if (uA != null && uB != null && !uA.equals(uB)) {
                    grafo.agregarConexion(new Conexion(uA, uB, costo));
                    actualizarVistaRedCompleta(); // <-- Este método ahora tiene código
                    actualizadorExterno.run();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El costo debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonCalcularMST.addActionListener(e -> {
            List<Conexion> redMinima = grafo.calcularRedMinima();
            actualizarVistaRedMinima(redMinima); // <-- Este método ahora tiene código
        });
    }

    public void actualizarCombos() {
        comboUsuarioA.removeAllItems();
        comboUsuarioB.removeAllItems();
        for (Usuario u : grafo.getUsuarios()) {
            comboUsuarioA.addItem(u);
            comboUsuarioB.addItem(u);
        }
    }

    // --- [CORRECCIÓN 1] ---
    // Implementación de los métodos que estaban vacíos

    private void actualizarVistaRedCompleta() {
        StringBuilder sb = new StringBuilder();
        int costoTotal = 0;
        for (Conexion c : grafo.getConexiones()) {
            sb.append(c.getUsuarioA().getNombre())
                    .append(" <-> ")
                    .append(c.getUsuarioB().getNombre())
                    .append(" (Costo: ").append(c.getCosto()).append(")\n");
            costoTotal += c.getCosto();
        }
        sb.append("\nCosto Total: ").append(costoTotal);
        areaResultadoCompleto.setText(sb.toString());
    }

    private void actualizarVistaRedMinima(List<Conexion> redMinima) {
        StringBuilder sb = new StringBuilder();
        int costoTotalMinimo = 0;
        for (Conexion c : redMinima) {
            sb.append(c.getUsuarioA().getNombre())
                    .append(" <-> ")
                    .append(c.getUsuarioB().getNombre())
                    .append(" (Costo: ").append(c.getCosto()).append(")\n");
            costoTotalMinimo += c.getCosto();
        }
        sb.append("\nCosto Mínimo Total: ").append(costoTotalMinimo);
        areaResultadoMinimo.setText(sb.toString());
    }
}