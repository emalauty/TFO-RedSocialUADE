package modelo.grafo;

import modelo.grafo.Conexion;
import modelo.grafo.Usuario;

import java.util.*;

public class RedSocialGrafo {

    private final List<Usuario> usuarios;
    private final List<Conexion> conexiones;

    // --- Constructor y Métodos Base ---
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

    // --- Algoritmo de Kruskal (Problema 3) ---

    public List<Conexion> calcularRedMinima() {
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

    // --- Métodos Auxiliares DSU (Usados por Kruskal y Simulación) ---

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


    // --- Algoritmo de Dijkstra (Problema 4) ---

    private static class NodoDistancia implements Comparable<NodoDistancia> {
        Usuario usuario;
        int distancia;
        NodoDistancia(Usuario usuario, int distancia) { this.usuario = usuario; this.distancia = distancia; }
        @Override
        public int compareTo(NodoDistancia other) { return Integer.compare(this.distancia, other.distancia); }
    }

    public Map<Usuario, Integer> encontrarCaminosMasCortos(Usuario inicio) {
        Map<Usuario, Integer> distancias = new HashMap<>();
        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>();
        Set<Usuario> visitados = new HashSet<>();

        for (Usuario u : this.usuarios) {
            distancias.put(u, Integer.MAX_VALUE);
        }

        // Manejo de error si el usuario de inicio no está en el mapa
        if(distancias.containsKey(inicio)) {
            distancias.put(inicio, 0);
        } else {
            return distancias; // Devuelve distancias infinitas si el inicio no existe
        }

        pq.add(new NodoDistancia(inicio, 0));

        while (!pq.isEmpty()) {
            NodoDistancia nodoActual = pq.poll();
            Usuario usuarioActual = nodoActual.usuario;

            if (visitados.contains(usuarioActual)) continue;
            visitados.add(usuarioActual);

            for (Conexion c : this.conexiones) {
                Usuario vecino = null;
                if (c.getUsuarioA().equals(usuarioActual) && !visitados.contains(c.getUsuarioB())) {
                    vecino = c.getUsuarioB();
                } else if (c.getUsuarioB().equals(usuarioActual) && !visitados.contains(c.getUsuarioA())) {
                    vecino = c.getUsuarioA();
                }

                if (vecino != null) {
                    int nuevaDistancia = distancias.get(usuarioActual) + c.getCosto();
                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        pq.add(new NodoDistancia(vecino, nuevaDistancia));
                    }
                }
            }
        }
        return distancias;
    }


    // --- Algoritmo de Componentes Conexos (Opcional 1) ---

    public int contarComponentesConexos(Conexion conexionABloquear) {
        List<Conexion> conexionesActivas = new ArrayList<>();
        if (conexionABloquear != null) {
            for (Conexion c : this.conexiones) {
                if (!c.equals(conexionABloquear)) {
                    conexionesActivas.add(c);
                }
            }
        } else {
            conexionesActivas.addAll(this.conexiones);
        }

        if (this.usuarios.isEmpty()) {
            return 0;
        }

        Map<Usuario, Usuario> parent = new HashMap<>();
        for (Usuario u : usuarios) {
            parent.put(u, u);
        }

        for (Conexion c : conexionesActivas) {
            Usuario rootA = find(c.getUsuarioA(), parent);
            Usuario rootB = find(c.getUsuarioB(), parent);
            if (!rootA.equals(rootB)) {
                union(rootA, rootB, parent);
            }
        }

        Set<Usuario> raicesUnicas = new HashSet<>();
        for (Usuario u : this.usuarios) {
            raicesUnicas.add(find(u, parent));
        }

        return raicesUnicas.size();
    }


    // --- Algoritmo de Exploración de Rutas (Opcional 4 - Backtracking) ---

    public List<List<Usuario>> explorarRutasDeInfluencia(Usuario inicio, Usuario fin) {
        List<List<Usuario>> todosLosCaminos = new ArrayList<>();
        List<Usuario> caminoActual = new ArrayList<>();
        Set<Usuario> visitadosEnCamino = new HashSet<>();

        backtrack(inicio, fin, caminoActual, visitadosEnCamino, todosLosCaminos);

        return todosLosCaminos;
    }

    private void backtrack(Usuario actual, Usuario fin,
                           List<Usuario> caminoActual,
                           Set<Usuario> visitadosEnCamino,
                           List<List<Usuario>> todosLosCaminos) {

        caminoActual.add(actual);
        visitadosEnCamino.add(actual);

        if (actual.equals(fin)) {
            todosLosCaminos.add(new ArrayList<>(caminoActual));
        } else {
            for (Conexion c : this.conexiones) {
                Usuario vecino = null;
                if (c.getUsuarioA().equals(actual) && !visitadosEnCamino.contains(c.getUsuarioB())) {
                    vecino = c.getUsuarioB();
                } else if (c.getUsuarioB().equals(actual) && !visitadosEnCamino.contains(c.getUsuarioA())) {
                    vecino = c.getUsuarioA();
                }

                if (vecino != null) {
                    backtrack(vecino, fin, caminoActual, visitadosEnCamino, todosLosCaminos);
                }
            }
        }

        caminoActual.remove(caminoActual.size() - 1);
        visitadosEnCamino.remove(actual);
    }
}