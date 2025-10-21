package vista;

import modelo.grafo.RedSocialGrafo;
import modelo.grafo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PanelSugerencias extends JPanel {

    private final RedSocialGrafo grafo;
    private final GrafoPanel panelDeDibujo;
    private final JComboBox<Usuario> comboUsuarioRecomendacion;
    private final JTextArea areaRecomendacionesTexto;

    public PanelSugerencias(RedSocialGrafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Panel de Control (Izquierda)
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createTitledBorder("Controles de Sugerencias"));
        panelControl.setPreferredSize(new Dimension(300, 0));

        // Sección para Recomendación de Amigos (Dijkstra)
        JPanel panelRecomendacion = new JPanel(new BorderLayout(5, 5));
        panelRecomendacion.setBorder(BorderFactory.createTitledBorder("Buscar Amigos"));
        comboUsuarioRecomendacion = new JComboBox<>();
        JButton botonRecomendar = new JButton("Buscar Caminos Cortos (Dijkstra)");
        panelRecomendacion.add(new JLabel("Recomendar para:"), BorderLayout.NORTH);
        panelRecomendacion.add(comboUsuarioRecomendacion, BorderLayout.CENTER);
        panelRecomendacion.add(botonRecomendar, BorderLayout.SOUTH);

        // Área de texto para los resultados de Dijkstra
        areaRecomendacionesTexto = new JTextArea();
        areaRecomendacionesTexto.setEditable(false);
        JScrollPane scrollResultados = new JScrollPane(areaRecomendacionesTexto);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados de Búsqueda"));

        panelControl.add(panelRecomendacion);
        panelControl.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControl.add(scrollResultados);

        // 2. Panel de Dibujo (Centro)
        panelDeDibujo = new GrafoPanel(grafo);

        // Añadir paneles al principal
        add(panelControl, BorderLayout.WEST);
        add(panelDeDibujo, BorderLayout.CENTER);

        // --- LÓGICA DE BOTONES ---
        botonRecomendar.addActionListener(e -> {
            Usuario usuarioInicio = (Usuario) comboUsuarioRecomendacion.getSelectedItem();
            if (usuarioInicio != null) {
                Map<Usuario, Integer> distancias = grafo.encontrarCaminosMasCortos(usuarioInicio);
                actualizarVistaRecomendaciones(usuarioInicio, distancias);
                panelDeDibujo.setResultadosDijkstra(distancias); // ¡Avisa al panel de dibujo!
            }
        });
    }

    /**
     * Actualiza el JComboBox de este panel.
     * Esta es la versión corregida sin la línea que daba error.
     */
    public void actualizarCombos() {
        comboUsuarioRecomendacion.removeAllItems();
        for (Usuario u : grafo.getUsuarios()) {
            comboUsuarioRecomendacion.addItem(u);
        }
        panelDeDibujo.limpiarResultados();
        panelDeDibujo.repaint();
    }

    /**
     * Muestra los resultados de Dijkstra en el área de texto.
     */
    private void actualizarVistaRecomendaciones(Usuario inicio, Map<Usuario, Integer> distancias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Distancias más cortas desde: ").append(inicio.getNombre()).append("\n\n");

        // Ordenamos los resultados por distancia para que sean más legibles
        distancias.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    Usuario u = entry.getKey();
                    if (!u.equals(inicio)) { // No nos recomendamos a nosotros mismos
                        String dist = entry.getValue() == Integer.MAX_VALUE ? "Inalcanzable" : entry.getValue().toString();
                        sb.append(u.getNombre()).append(": ").append(dist).append("\n");
                    }
                });
        areaRecomendacionesTexto.setText(sb.toString());
    }
}