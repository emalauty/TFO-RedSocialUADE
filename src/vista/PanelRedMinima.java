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
    private final JComboBox<Conexion> comboConexiones;

    public PanelRedMinima(RedSocialGrafo grafo, Runnable actualizadorExterno) {
        this.grafo = grafo;
        this.actualizadorExterno = actualizadorExterno;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PANEL DE CONTROLES (IZQUIERDA) ---
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles de la Red"));

        // (Sección Añadir Usuarios - sin cambios)
        JPanel panelAnadirUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField campoNombreUsuario = new JTextField(15);
        JButton botonAnadirUsuario = new JButton("Añadir Usuario");
        panelAnadirUsuario.add(new JLabel("Nombre:"));
        panelAnadirUsuario.add(campoNombreUsuario);
        panelAnadirUsuario.add(botonAnadirUsuario);

        // (Sección Añadir Conexiones - sin cambios)
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

        // (Botón Calcular MST - sin cambios)
        JButton botonCalcularMST = new JButton("Calcular Red Mínima (Kruskal)");
        botonCalcularMST.setAlignmentX(Component.CENTER_ALIGNMENT);

        // (Panel de Simulación de Bloqueo - sin cambios en los componentes)
        JPanel panelBloqueo = new JPanel(new BorderLayout(5, 5));
        panelBloqueo.setBorder(BorderFactory.createTitledBorder("Simulación de Bloqueo"));
        comboConexiones = new JComboBox<>();
        JButton botonBloquear = new JButton("Verificar Conectividad (DSU)"); // Renombrado para claridad
        panelBloqueo.add(new JLabel("Bloquear conexión:"), BorderLayout.NORTH);
        panelBloqueo.add(comboConexiones, BorderLayout.CENTER);
        panelBloqueo.add(botonBloquear, BorderLayout.SOUTH);

        comboConexiones.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Conexion) {
                    Conexion c = (Conexion) value;
                    setText(c.getUsuarioA().getNombre() + " <-> " + c.getUsuarioB().getNombre());
                }
                return this;
            }
        });

        // Añadir componentes al panel de controles
        panelControles.add(panelAnadirUsuario);
        panelControles.add(Box.createRigidArea(new Dimension(0, 15)));
        panelControles.add(panelAnadirConexion);
        panelControles.add(botonAnadirConexion);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(botonCalcularMST);
        panelControles.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControles.add(panelBloqueo);

        // --- PANEL DE RESULTADOS (CENTRO) ---
        // (Sin cambios)
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

        // (botonAnadirUsuario - sin cambios)
        botonAnadirUsuario.addActionListener(e -> {
            String nombre = campoNombreUsuario.getText().trim();
            if (!nombre.isEmpty()) {
                Usuario nuevoUsuario = new Usuario(nombre);
                grafo.agregarUsuario(nuevoUsuario);
                actualizarCombosDeUsuario();
                actualizadorExterno.run();
                campoNombreUsuario.setText("");
            }
        });

        // (botonAnadirConexion - sin cambios)
        botonAnadirConexion.addActionListener(e -> {
            Usuario uA = (Usuario) comboUsuarioA.getSelectedItem();
            Usuario uB = (Usuario) comboUsuarioB.getSelectedItem();
            try {
                int costo = Integer.parseInt(campoCosto.getText().trim());
                if (uA != null && uB != null && !uA.equals(uB)) {
                    Conexion nuevaConexion = new Conexion(uA, uB, costo);
                    grafo.agregarConexion(nuevaConexion);
                    actualizarVistaRedCompleta();
                    comboConexiones.addItem(nuevaConexion);
                    actualizadorExterno.run();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El costo debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // (botonCalcularMST - sin cambios)
        botonCalcularMST.addActionListener(e -> {
            List<Conexion> redMinima = grafo.calcularRedMinima();
            actualizarVistaRedMinima(redMinima);
        });

        // --- [LÓGICA ACTUALIZADA] ---
        // Lógica del botón de bloqueo usando DSU (find/union)
        botonBloquear.addActionListener(e -> {
            Conexion conexionABloquear = (Conexion) comboConexiones.getSelectedItem();
            if (conexionABloquear == null) {
                JOptionPane.showMessageDialog(this, "No hay conexiones para bloquear.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Llama al metodo que usa la lógica DSU (find/union)
            int k = grafo.contarComponentesConexos(conexionABloquear);

            String mensaje;
            String titulo = "Resultado de la Simulación";
            int tipoMensaje;

            // 2. Comprueba el resultado 'k' (número de islas)
            if (k == 1) {
                mensaje = "¡La red SIGUE conectada!\nIncluso sin la amistad entre " +
                        conexionABloquear.getUsuarioA().getNombre() + " y " + conexionABloquear.getUsuarioB().getNombre();
                tipoMensaje = JOptionPane.INFORMATION_MESSAGE;
            } else {
                int conexionesNecesarias = k - 1;
                mensaje = "¡La red SE HA ROTO!\n" +
                        "El bloqueo ha dividido la red en " + k + " islas.\n" +
                        "Se necesitan " + conexionesNecesarias + " conexión(es) nueva(s) para reconectarla.";
                tipoMensaje = JOptionPane.WARNING_MESSAGE;
            }

            JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
        });
    }

    // (El resto de los métodos no cambian)
    public void actualizarCombosDeUsuario() {
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
            sb.append(c.getUsuarioA().getNombre()).append(" <-> ").append(c.getUsuarioB().getNombre())
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
            sb.append(c.getUsuarioA().getNombre()).append(" <-> ").append(c.getUsuarioB().getNombre())
                    .append(" (Costo: ").append(c.getCosto()).append(")\n");
            costoTotalMinimo += c.getCosto();
        }
        sb.append("\nCosto Mínimo Total: ").append(costoTotalMinimo);
        areaResultadoMinimo.setText(sb.toString());
    }
}