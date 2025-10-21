package vista;

import modelo.grafo.Conexion;
import modelo.grafo.RedSocialGrafo;
import modelo.grafo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafoPanel extends JPanel {

    private final RedSocialGrafo grafo;
    private final Map<Usuario, Point> posiciones;
    private Map<Usuario, Integer> distanciasDijkstra = new HashMap<>();

    public GrafoPanel(RedSocialGrafo grafo) {
        this.grafo = grafo;
        this.posiciones = new HashMap<>();
        setBackground(Color.WHITE);
    }

    public void setResultadosDijkstra(Map<Usuario, Integer> distancias) {
        this.distanciasDijkstra = distancias;
        repaint(); // Vuelve a dibujar el panel con los nuevos resultados
    }

    public void limpiarResultados() {
        this.distanciasDijkstra.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Activa el Anti-aliasing para que se vea suave
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Conexion> conexiones = grafo.getConexiones();
        List<Usuario> usuarios = grafo.getUsuarios();

        // Asignamos posiciones a los nuevos usuarios aquí,
        // solo cuando el panel ya tiene un tamaño.
        if (getWidth() > 0 && getHeight() > 0) {
            for (Usuario u : usuarios) {
                if (!posiciones.containsKey(u)) {
                    int x = (int) (Math.random() * (getWidth() * 0.8) + (getWidth() * 0.1));
                    int y = (int) (Math.random() * (getHeight() * 0.8) + (getHeight() * 0.1));
                    posiciones.put(u, new Point(x, y));
                }
            }
        }

        // 1. Dibujar todas las conexiones (Aristas)
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.LIGHT_GRAY); // Color para las líneas
        Font costoFont = new Font("Arial", Font.PLAIN, 12); // Fuente para el costo

        for (Conexion c : conexiones) {
            Point p1 = posiciones.get(c.getUsuarioA());
            Point p2 = posiciones.get(c.getUsuarioB());
            if (p1 != null && p2 != null) {
                // Dibuja la línea
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                // --- [CORRECCIÓN] ---
                // Cambiamos a color negro y la fuente para el texto
                g2.setColor(Color.BLACK);
                g2.setFont(costoFont);

                // Dibuja el costo
                String costo = String.valueOf(c.getCosto());
                int textX = (p1.x + p2.x) / 2;
                int textY = (p1.y + p2.y) / 2;
                // Añadimos un pequeño offset para que no quede justo sobre la línea
                g2.drawString(costo, textX + 5, textY - 5);

                // Volvemos al color gris para la siguiente línea
                g2.setColor(Color.LIGHT_GRAY);
                // --- [FIN DE LA CORRECCIÓN] ---
            }
        }

        // 2. Dibujar los usuarios (Nodos)
        for (Usuario u : usuarios) {
            Point p = posiciones.get(u);
            if (p != null) {
                int diametro = 40;

                // Colorear según el resultado de Dijkstra
                if (distanciasDijkstra.containsKey(u)) {
                    Integer dist = distanciasDijkstra.get(u);
                    if (dist == 0) {
                        g2.setColor(Color.ORANGE); // Nodo de inicio
                    } else if (dist == Integer.MAX_VALUE) {
                        g2.setColor(Color.RED); // Inalcanzable
                    } else {
                        g2.setColor(new Color(0, 153, 51)); // Verde (alcanzable)
                    }
                } else {
                    g2.setColor(new Color(0, 102, 204)); // Azul (por defecto)
                }

                g2.fillOval(p.x - diametro / 2, p.y - diametro / 2, diametro, diametro);

                // Dibujar el nombre del usuario
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(u.getNombre(), p.x - 10, p.y + 4);
            }
        }
    }
}