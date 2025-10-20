package vista;

import modelo.Muro;
import modelo.Publicacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VentanaPrincipal extends JFrame {

    private Muro miMuro;
    private JPanel panelMuro;
    private JScrollPane scrollPane;

    public VentanaPrincipal() {
        setTitle("Red Social UADE");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        miMuro = new Muro();
        crearPublicacionesDeEjemplo();

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));

        JPanel panelFiltros = new JPanel();
        JButton botonRecientes = new JButton("Ver más Recientes");
        JButton botonRelevantes = new JButton("Ver más Relevantes");
        panelFiltros.add(botonRecientes);
        panelFiltros.add(botonRelevantes);

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

        panelSuperior.add(panelFiltros);
        panelSuperior.add(panelCreacion);

        panelMuro = new JPanel();
        panelMuro.setLayout(new BoxLayout(panelMuro, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(panelMuro);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        botonPublicar.addActionListener(e -> {
            String autor = campoAutor.getText();
            String contenido = areaContenido.getText();
            if (autor.trim().isEmpty() || contenido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El autor y el contenido no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            miMuro.addPublicacion(new Publicacion(autor, contenido));
            areaContenido.setText("");
            actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha());
        });

        botonRecientes.addActionListener(e -> actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha()));
        botonRelevantes.addActionListener(e -> actualizarMuro(miMuro.getPublicacionesPorRelevancia()));

        actualizarMuro(miMuro.getPublicacionesOrdenadasPorFecha());
    }

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

    private void crearPublicacionesDeEjemplo() {
        try {
            miMuro.addPublicacion(new Publicacion("Juan", "¡Mi primera publicación! Hablando de algoritmos."));
            TimeUnit.MILLISECONDS.sleep(500);
            Publicacion pub2 = new Publicacion("Maria", "Qué buen día para programar en Java y Swing.");
            pub2.darMeGusta();
            pub2.darMeGusta();
            miMuro.addPublicacion(pub2);
            TimeUnit.MILLISECONDS.sleep(500);
            miMuro.addPublicacion(new Publicacion("Carlos", "El problema del TFO sobre relevancia es interesante."));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}