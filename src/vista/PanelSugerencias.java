package vista;

import modelo.grafo.RedSocialGrafo;
import modelo.grafo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PanelSugerencias extends JPanel {

    private final RedSocialGrafo grafo;
    private final GrafoPanel panelDeDibujo;
    private final JComboBox<Usuario> comboUsuarioInicio;
    private final JComboBox<Usuario> comboUsuarioFin; // [NUEVO]
    private final JTextArea areaResultadosTexto; // [Renombrado]

    public PanelSugerencias(RedSocialGrafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Panel de Control (Izquierda)
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createTitledBorder("Controles de Búsqueda"));
        panelControl.setPreferredSize(new Dimension(350, 0)); // Un poco más ancho

        // --- [NUEVO] Panel de Selección de Usuarios ---
        JPanel panelSeleccion = new JPanel(new GridLayout(2, 2, 5, 5));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Selección de Usuarios"));
        comboUsuarioInicio = new JComboBox<>();
        comboUsuarioFin = new JComboBox<>(); // [NUEVO]
        panelSeleccion.add(new JLabel("Usuario de Inicio:"));
        panelSeleccion.add(comboUsuarioInicio);
        panelSeleccion.add(new JLabel("Usuario Final (para rutas):"));
        panelSeleccion.add(comboUsuarioFin); // [NUEVO]

        // --- [NUEVO] Panel de Botones de Algoritmos ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Ejecutar Algoritmos"));
        JButton botonDijkstra = new JButton("Buscar Caminos Cortos (Dijkstra)");
        JButton botonBacktrack = new JButton("Explorar Rutas de Influencia"); // [NUEVO]
        panelBotones.add(botonDijkstra);
        panelBotones.add(botonBacktrack); // [NUEVO]

        // Área de texto para los resultados (compartida)
        areaResultadosTexto = new JTextArea();
        areaResultadosTexto.setEditable(false);
        JScrollPane scrollResultados = new JScrollPane(areaResultadosTexto);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados de Búsqueda"));

        panelControl.add(panelSeleccion); // [MODIFICADO]
        panelControl.add(Box.createRigidArea(new Dimension(0, 15)));
        panelControl.add(panelBotones); // [MODIFICADO]
        panelControl.add(Box.createRigidArea(new Dimension(0, 15)));
        panelControl.add(scrollResultados);

        // 2. Panel de Dibujo (Centro)
        panelDeDibujo = new GrafoPanel(grafo);

        // Añadir paneles al principal
        add(panelControl, BorderLayout.WEST);
        add(panelDeDibujo, BorderLayout.CENTER);

        // --- LÓGICA DE BOTONES ---

        // Lógica de Dijkstra (como antes, pero usa el nuevo combo)
        botonDijkstra.addActionListener(e -> {
            Usuario usuarioInicio = (Usuario) comboUsuarioInicio.getSelectedItem();
            if (usuarioInicio != null) {
                Map<Usuario, Integer> distancias = grafo.encontrarCaminosMasCortos(usuarioInicio);
                actualizarVistaDijkstra(usuarioInicio, distancias);
                panelDeDibujo.setResultadosDijkstra(distancias);
            }
        });

        // --- [NUEVO] Lógica de Backtracking ---
        botonBacktrack.addActionListener(e -> {
            Usuario usuarioInicio = (Usuario) comboUsuarioInicio.getSelectedItem();
            Usuario usuarioFin = (Usuario) comboUsuarioFin.getSelectedItem();

            if (usuarioInicio == null || usuarioFin == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de inicio y uno final.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (usuarioInicio.equals(usuarioFin)) {
                JOptionPane.showMessageDialog(this, "El usuario de inicio y final no pueden ser el mismo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Limpiamos el coloreado de Dijkstra del grafo
            panelDeDibujo.limpiarResultados();

            // Llamamos al algoritmo
            List<List<Usuario>> todosLosCaminos = grafo.explorarRutasDeInfluencia(usuarioInicio, usuarioFin);

            // Mostramos los resultados en el área de texto
            actualizarVistaRutas(usuarioInicio, usuarioFin, todosLosCaminos);
        });
    }

    /**
     * Actualiza TODOS los JComboBox de este panel.
     */
    public void actualizarCombos() {
        comboUsuarioInicio.removeAllItems();
        comboUsuarioFin.removeAllItems(); // [NUEVO]

        for (Usuario u : grafo.getUsuarios()) {
            comboUsuarioInicio.addItem(u);
            comboUsuarioFin.addItem(u); // [NUEVO]
        }

        panelDeDibujo.limpiarResultados();
        panelDeDibujo.repaint();
    }

    /**
     * Muestra los resultados de Dijkstra en el área de texto.
     */
    private void actualizarVistaDijkstra(Usuario inicio, Map<Usuario, Integer> distancias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Distancias más cortas desde: ").append(inicio.getNombre()).append("\n(Algoritmo de Dijkstra)\n\n");

        distancias.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    Usuario u = entry.getKey();
                    if (!u.equals(inicio)) {
                        String dist = entry.getValue() == Integer.MAX_VALUE ? "Inalcanzable" : entry.getValue().toString();
                        sb.append(u.getNombre()).append(": ").append(dist).append("\n");
                    }
                });
        areaResultadosTexto.setText(sb.toString());
    }

    /**
     * [NUEVO] Muestra los resultados de Backtracking en el área de texto.
     */
    private void actualizarVistaRutas(Usuario inicio, Usuario fin, List<List<Usuario>> todosLosCaminos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rutas de influencia encontradas de '")
                .append(inicio.getNombre()).append("' a '")
                .append(fin.getNombre()).append("':\n");
        sb.append("(Algoritmo de Backtracking)\n\n");

        if (todosLosCaminos.isEmpty()) {
            sb.append("No se encontró ninguna ruta entre estos usuarios.");
        } else {
            sb.append("Se encontraron ").append(todosLosCaminos.size()).append(" rutas:\n");

            // Opcional: Encontrar la ruta "más efectiva" (la más corta)
            List<Usuario> rutaMasCorta = todosLosCaminos.stream()
                    .min(Comparator.comparingInt(List::size))
                    .orElse(null);

            sb.append("\n--- RUTA MÁS CORTA ---\n");
            if (rutaMasCorta != null) {
                sb.append(caminoATexto(rutaMasCorta));
                sb.append(" (").append(rutaMasCorta.size() - 1).append(" saltos)\n");
            }

            sb.append("\n--- TODAS LAS RUTAS ---\n");
            for (List<Usuario> camino : todosLosCaminos) {
                sb.append(caminoATexto(camino)).append("\n");
            }
        }
        areaResultadosTexto.setText(sb.toString());
    }

    /**
     * [NUEVO] Método auxiliar para convertir un camino (Lista de Usuarios) a un String.
     */
    private String caminoATexto(List<Usuario> camino) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camino.size(); i++) {
            sb.append(camino.get(i).getNombre());
            if (i < camino.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}