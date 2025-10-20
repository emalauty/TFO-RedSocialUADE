package vista;

import modelo.GestorDeDatos;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Red Social UADE");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Creamos una instancia de nuestro nuevo panel para el muro
        PanelMuro panelMuro = new PanelMuro();

        // Creamos el panel para la Red de Amigos
        PanelRedDeAmigos panelRedDeAmigos = new PanelRedDeAmigos();

        // Añadimos los paneles como pestañas
        tabbedPane.addTab("Muro de Publicaciones", panelMuro);
        tabbedPane.addTab("Red de Amigos", panelRedDeAmigos);

        // Añadimos el panel de pestañas a la ventana
        add(tabbedPane);

        // El listener para guardar se queda aquí, pero ahora pide los datos al panel del muro
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                GestorDeDatos gestor = new GestorDeDatos();
                // Usamos el getter que creamos en PanelMuro
                gestor.guardarPublicaciones(panelMuro.getMuro().getListaDeTodasLasPublicaciones());
            }
        });
    }
}