package modelo;

import java.util.*;

public class RedSocialGrafo {

    private final List<Usuario> usuarios;
    private final List<Conexion> conexiones;

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

    public List<Conexion> calcularRedMinima() {
        // 1. Ordenar las conexiones por costo (de menor a mayor)
        List<Conexion> conexionesOrdenadas = new ArrayList<>(this.conexiones);
        conexionesOrdenadas.sort(Comparator.comparingInt(Conexion::getCosto));

        // Estructuras para el algoritmo
        List<Conexion> redMinima = new ArrayList<>(); // El resultado final
        Map<Usuario, Usuario> parent = new HashMap<>(); // Para rastrear los conjuntos de usuarios

        // Inicialmente, cada usuario es su propio "jefe" de grupo
        for (Usuario u : usuarios) {
            parent.put(u, u);
        }

        // 2. Recorrer las conexiones ordenadas
        for (Conexion conexion : conexionesOrdenadas) {
            Usuario usuarioA = conexion.getUsuarioA();
            Usuario usuarioB = conexion.getUsuarioB();

            // 3. Encontrar al "jefe" del grupo de cada usuario
            Usuario rootA = find(usuarioA, parent);
            Usuario rootB = find(usuarioB, parent);

            // 4. Si no pertenecen al mismo grupo, no se forma un ciclo
            if (!rootA.equals(rootB)) {
                redMinima.add(conexion); // Añadimos la conexión a nuestra red
                union(rootA, rootB, parent); // Unimos los dos grupos
            }
        }

        return redMinima;
    }

    private Usuario find(Usuario usuario, Map<Usuario, Usuario> parent) {
        if (parent.get(usuario).equals(usuario)) {
            return usuario;
        }
        // Compresión de camino: conecta directamente el nodo a la raíz
        parent.put(usuario, find(parent.get(usuario), parent));
        return parent.get(usuario);
    }

    /**
     * Método auxiliar (UNION): Une dos conjuntos de usuarios.
     */
    private void union(Usuario rootA, Usuario rootB, Map<Usuario, Usuario> parent) {
        parent.put(rootA, rootB);
    }

    // Getters para que la vista pueda acceder a los datos
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Conexion> getConexiones() {
        return conexiones;
    }
}