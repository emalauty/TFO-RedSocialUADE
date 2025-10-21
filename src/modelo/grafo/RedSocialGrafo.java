package modelo.grafo;

import java.util.*;

public class RedSocialGrafo {

    private final List<Usuario> usuarios;
    private final List<Conexion> conexiones;

    // --- Constructor y métodos que ya tenías ---
    public RedSocialGrafo() {
        this.usuarios = new ArrayList<>();
        this.conexiones = new ArrayList<>();
    }

    public void agregarUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    public void agregarConexion(Conexion conexion) {
        this.conexiones.add(conexion);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Conexion> getConexiones() {
        return conexiones;
    }

    // --- Método de Kruskal que ya tenías ---
    public List<Conexion> calcularRedMinima() {
        // ... (tu código de Kruskal va aquí)
        List<Conexion> conexionesOrdenadas = new ArrayList<>(this.conexiones);
        conexionesOrdenadas.sort(Comparator.comparingInt(Conexion::getCosto));
        List<Conexion> redMinima = new ArrayList<>();
        Map<Usuario, Usuario> parent = new HashMap<>();
        for (Usuario u : usuarios) {
            parent.put(u, u);
        }
        for (Conexion conexion : conexionesOrdenadas) {
            Usuario rootA = find(conexion.getUsuarioA(), parent);
            Usuario rootB = find(conexion.getUsuarioB(), parent);
            if (!rootA.equals(rootB)) {
                redMinima.add(conexion);
                union(rootA, rootB, parent);
            }
        }
        return redMinima;
    }

    private Usuario find(Usuario usuario, Map<Usuario, Usuario> parent) {
        if (parent.get(usuario).equals(usuario)) {
            return usuario;
        }
        parent.put(usuario, find(parent.get(usuario), parent));
        return parent.get(usuario);
    }

    private void union(Usuario rootA, Usuario rootB, Map<Usuario, Usuario> parent) {
        parent.put(rootA, rootB);
    }


    // --- CÓDIGO PARA ALGORITMO DE DIJKSTRA ---


    private static class NodoDistancia implements Comparable<NodoDistancia> {
        Usuario usuario;
        int distancia;

        NodoDistancia(Usuario usuario, int distancia) {
            this.usuario = usuario;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDistancia other) {
            // Compara por distancia (de menor a mayor)
            return Integer.compare(this.distancia, other.distancia);
        }
    }

    /**
     * Implementa el algoritmo de Dijkstra para encontrar los caminos más cortos
     * desde un usuario de inicio a todos los demás en el grafo.
     *
     * @param inicio El Usuario desde el cual se calculan las distancias.
     * @return Un Map donde la clave es cada Usuario y el valor es la distancia más corta
     * (costo total) desde el usuario de inicio.
     */
    public Map<Usuario, Integer> encontrarCaminosMasCortos(Usuario inicio) {
        // 1. Inicialización
        Map<Usuario, Integer> distancias = new HashMap<>();
        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>();
        Set<Usuario> visitados = new HashSet<>();

        // Inicializar todas las distancias como "infinito"
        for (Usuario u : this.usuarios) {
            distancias.put(u, Integer.MAX_VALUE);
        }

        // La distancia al nodo de inicio es 0
        distancias.put(inicio, 0);
        pq.add(new NodoDistancia(inicio, 0));

        // 2. Bucle principal del algoritmo
        while (!pq.isEmpty()) {
            // Sacamos el nodo con la menor distancia de la cola de prioridad
            NodoDistancia nodoActual = pq.poll();
            Usuario usuarioActual = nodoActual.usuario;

            // Si ya lo visitamos, lo ignoramos (esto evita procesar rutas más largas)
            if (visitados.contains(usuarioActual)) {
                continue;
            }
            visitados.add(usuarioActual);

            // 3. Revisar todos los vecinos del nodo actual
            // (Esta parte itera sobre todas las conexiones para encontrar vecinos)
            for (Conexion c : this.conexiones) {
                Usuario vecino = null;
                // Verificamos si la conexión 'c' involucra a nuestro 'usuarioActual'
                if (c.getUsuarioA().equals(usuarioActual) && !visitados.contains(c.getUsuarioB())) {
                    vecino = c.getUsuarioB();
                } else if (c.getUsuarioB().equals(usuarioActual) && !visitados.contains(c.getUsuarioA())) {
                    vecino = c.getUsuarioA();
                }

                // Si encontramos un vecino válido que no hemos visitado
                if (vecino != null) {
                    // 4. "Relajación": Calculamos la nueva distancia a este vecino
                    int nuevaDistancia = distancias.get(usuarioActual) + c.getCosto();

                    // Si este nuevo camino es más corto que el que ya teníamos...
                    if (nuevaDistancia < distancias.get(vecino)) {
                        // ...actualizamos la distancia más corta
                        distancias.put(vecino, nuevaDistancia);
                        // y añadimos al vecino a la cola de prioridad para explorarlo
                        pq.add(new NodoDistancia(vecino, nuevaDistancia));
                    }
                }
            }
        }

        return distancias;
    }
}