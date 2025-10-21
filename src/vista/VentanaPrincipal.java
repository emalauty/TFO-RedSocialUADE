package vista;

import persistencia.GestorDeDatos;
import modelo.grafo.RedSocialGrafo; // Importamos la clase del grafo
import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Red Social UADE");
        setSize(1024, 768); // Más grande para el grafo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- 1. Crear el objeto Grafo COMPARTIDO ---
        RedSocialGrafo grafoCompartido = new RedSocialGrafo();

        // --- 2. Crear los 3 Paneles ---
        PanelMuro panelMuro = new PanelMuro();

        // Creamos un "Runnable" para que el panel de red mínima pueda avisar al panel de sugerencias
        PanelSugerencias panelSugerencias = new PanelSugerencias(grafoCompartido);
        Runnable actualizador = () -> panelSugerencias.actualizarCombos();

        PanelRedMinima panelRedMinima = new PanelRedMinima(grafoCompartido, actualizador);

        // --- 3. Añadir Paneles como Pestañas ---
        tabbedPane.addTab("Muro de Publicaciones", panelMuro);
        tabbedPane.addTab("Red Mínima (Kruskal)", panelRedMinima);
        tabbedPane.addTab("Sugerencias (Dijkstra)", panelSugerencias);

        add(tabbedPane);

        // Listener para guardar (solo el muro, como antes)
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                GestorDeDatos gestor = new GestorDeDatos();
                gestor.guardarPublicaciones(panelMuro.getMuro().getListaDeTodasLasPublicaciones());
            }
        });
    }
}