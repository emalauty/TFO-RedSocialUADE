package modelo.grafo;

public class Conexion {
    private Usuario usuarioA;
    private Usuario usuarioB;
    private int costo;

    public Conexion(Usuario usuarioA, Usuario usuarioB, int costo) {
        this.usuarioA = usuarioA;
        this.usuarioB = usuarioB;
        this.costo = costo;
    }

    public Usuario getUsuarioA() {
        return usuarioA;
    }

    public Usuario getUsuarioB() {
        return usuarioB;
    }

    public int getCosto() {
        return costo;
    }
}