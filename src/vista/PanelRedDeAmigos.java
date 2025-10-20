package vista;

import modelo.Conexion;
import modelo.RedSocialGrafo;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelRedDeAmigos extends JPanel {

    private final RedSocialGrafo grafo;
    private final JComboBox<Usuario> comboUsuarioA;
    private final JComboBox<Usuario> comboUsuarioB;
    private final JTextArea areaResultadoCompleto;
    private final JTextArea areaResultadoMinimo;

    public PanelRedDeAmigos() {
        // 1. Configuración del Panel Principal
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 2. Inicialización del Grafo
        this.grafo = new RedSocialGrafo();

        // --- PANEL DE CONTROLES (IZQUIERDA) ---
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles de la Red"));

        // Sección para añadir usuarios
        JPanel panelAnadirUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField campoNombreUsuario = new JTextField(15);
        JButton botonAnadirUsuario = new JButton("Añadir Usuario");
        panelAnadirUsuario.add(new JLabel("Nombre:"));
        panelAnadirUsuario.add(campoNombreUsuario);
        panelAnadirUsuario.add(botonAnadirUsuario);

        // Sección para añadir conexiones
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

        // Botón para calcular la red mínima
        JButton botonCalcular = new JButton("Calcular Red Mínima");
        botonCalcular.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir componentes al panel de controles
        panelControles.add(panelAnadirUsuario);
        panelControles.add(Box.createRigidArea(new Dimension(0, 15)));
        panelControles.add(panelAnadirConexion);
        panelControles.add(botonAnadirConexion);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(botonCalcular);


        // --- PANEL DE RESULTADOS (CENTRO) ---
        JPanel panelResultados = new JPanel(new GridLayout(1, 2, 10, 0));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Visualización de la Red"));

        areaResultadoCompleto = new JTextArea("Agrega usuarios y conexiones...");
        areaResultadoCompleto.setEditable(false);
        JScrollPane scrollCompleto = new JScrollPane(areaResultadoCompleto);
        scrollCompleto.setBorder(BorderFactory.createTitledBorder("Red Completa"));

        areaResultadoMinimo = new JTextArea();
        areaResultadoMinimo.setEditable(false);
        JScrollPane scrollMinimo = new JScrollPane(areaResultadoMinimo);
        scrollMinimo.setBorder(BorderFactory.createTitledBorder("Red de Conectividad Mínima"));

        panelResultados.add(scrollCompleto);
        panelResultados.add(scrollMinimo);


        // 3. Añadir Paneles Principales al Panel General
        add(panelControles, BorderLayout.WEST);
        add(panelResultados, BorderLayout.CENTER);


        // --- LÓGICA DE LOS BOTONES ---

        // Acción para añadir un nuevo usuario
        botonAnadirUsuario.addActionListener(e -> {
            String nombre = campoNombreUsuario.getText().trim();
            if (!nombre.isEmpty()) {
                Usuario nuevoUsuario = new Usuario(nombre);
                grafo.agregarUsuario(nuevoUsuario);
                actualizarCombos();
                campoNombreUsuario.setText("");
                System.out.println("Usuario añadido: " + nombre);
            }
        });

        // Acción para añadir una nueva conexión
        botonAnadirConexion.addActionListener(e -> {
            Usuario uA = (Usuario) comboUsuarioA.getSelectedItem();
            Usuario uB = (Usuario) comboUsuarioB.getSelectedItem();
            try {
                int costo = Integer.parseInt(campoCosto.getText().trim());
                if (uA != null && uB != null && !uA.equals(uB)) {
                    grafo.agregarConexion(new Conexion(uA, uB, costo));
                    actualizarVistaRedCompleta();
                    System.out.println("Conexión añadida: " + uA + " <-> " + uB);
                } else {
                    JOptionPane.showMessageDialog(this, "Selecciona dos usuarios diferentes.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El costo debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción para calcular el MST
        botonCalcular.addActionListener(e -> {
            List<Conexion> redMinima = grafo.calcularRedMinima();
            actualizarVistaRedMinima(redMinima);
        });
    }

    private void actualizarCombos() {
        comboUsuarioA.removeAllItems();
        comboUsuarioB.removeAllItems();
        for (Usuario u : grafo.getUsuarios()) {
            comboUsuarioA.addItem(u);
            comboUsuarioB.addItem(u);
        }
    }

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