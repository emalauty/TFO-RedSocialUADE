package vista;

import modelo.GestorDeDatos;
import modelo.Muro;
import modelo.Publicacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private final Muro miMuro;
    private final JPanel panelMuro;
    private final JScrollPane scrollPane;

    public VentanaPrincipal() {
        // --- Configuración básica de la ventana ---
        setTitle("Red Social UADE");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // El Muro ahora carga los datos desde el archivo al ser creado
        miMuro = new Muro();

        // --- PANEL SUPERIOR (CONTENEDOR DE FILTROS Y CREACIÓN) ---
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));

        // Panel de Filtros
        JPanel panelFiltros = new JPanel();
        JButton botonRecientes = new JButton("Ver más Recientes");
        JButton botonRelevantes = new JButton("Ver más Relevantes");
        panelFiltros.add(botonRecientes);
        panelFiltros.add(botonRelevantes);

        // Panel para Crear Publicaciones
        JPanel panelCreacion = new JPanel(new BorderLayout(5, 5));
        panelCreacion.setBorder(BorderFactory.createTitledBorder("Crear Nueva Publicación"));
        JTextField campoAutor = new JTextField("Tu Nombre");
        JTextArea areaContenido = new JTextArea();
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        JScrollPane scrollContenido = new JScrollPane(areaContenido);
        scrollContenido.setPreferredSize(new Dimension(0, 80));
        JButton botonPublicar = new JButton("Publicar");
        panelCreacion.add(campoAutor, BorderLayout.NORTH);
        panelCreacion.add(scrollContenido, BorderLayout.CENTER);
        panelCreacion.add(botonPublicar, BorderLayout.SOUTH);

        // Agregamos los paneles al contenedor superior
        panelSuperior.add(panelFiltros);
        panelSuperior.add(panelCreacion);

        // --- PANEL CENTRAL PARA EL MURO (CON SCROLL) ---
        panelMuro = new JPanel();
        panelMuro.setLayout(new BoxLayout(panelMuro, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(panelMuro);

        // --- AÑADIMOS LOS PANELES PRINCIPALES A LA VENTANA ---
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- LÓGICA DE LOS BOTONES ---

        // Acción del botón Publicar
        botonPublicar.addActionListener(e -> {
            String autor = campoAutor.getText();
            String contenido = areaContenido.getText();

            if (autor.trim().isEmpty() || contenido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El autor y el contenido no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            miMuro.agregarPublicacion(new Publicacion(autor, contenido));
            areaContenido.setText("");
            actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha());
        });

        // Acciones de los botones de filtro
        botonRecientes.addActionListener(e -> actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha()));
        botonRelevantes.addActionListener(e -> actualizarMuro(miMuro.getPublicacionesPorRelevancia()));

        // --- [CAMBIO IMPORTANTE] Listener para guardar los datos al cerrar la ventana ---
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                GestorDeDatos gestor = new GestorDeDatos();
                gestor.guardarPublicaciones(miMuro.getListaDeTodasLasPublicaciones());
            }
        });

        // Carga inicial del muro (ya no se crean publicaciones de ejemplo)
        actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha());
    }

    /**
     * Limpia el muro y lo vuelve a dibujar con una lista de publicaciones.
     * @param publicaciones La lista de publicaciones a mostrar.
     */
    private void actualizarMuro(List<Publicacion> publicaciones) {
        panelMuro.removeAll();
        for (Publicacion pub : publicaciones) {
            JPanel panelPost = new JPanel(new BorderLayout(10, 10));
            panelPost.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            panelPost.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            JTextArea textoContenido = new JTextArea(pub.getContenido());
            textoContenido.setWrapStyleWord(true);
            textoContenido.setLineWrap(true);
            textoContenido.setEditable(false);
            textoContenido.setOpaque(false);

            JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel labelLikes = new JLabel("Me Gusta: " + pub.getCantidadDeMeGusta());
            JButton botonLike = new JButton("👍 Me Gusta");

            botonLike.addActionListener(new ActionListener() {
                private boolean liked = false;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!liked) {
                        pub.darMeGusta();
                        botonLike.setText("👎 Quitar Me Gusta");
                        liked = true;
                    } else {
                        pub.quitarMeGusta();
                        botonLike.setText("👍 Me Gusta");
                        liked = false;
                    }
                    labelLikes.setText("Me Gusta: " + pub.getCantidadDeMeGusta());
                }
            });

            panelInferior.add(labelLikes);
            panelInferior.add(botonLike);

            panelPost.add(new JLabel("  Autor: " + pub.getAutor()), BorderLayout.NORTH);
            panelPost.add(textoContenido, BorderLayout.CENTER);
            panelPost.add(panelInferior, BorderLayout.SOUTH);

            panelMuro.add(panelPost);
            panelMuro.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        panelMuro.revalidate();
        panelMuro.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
}