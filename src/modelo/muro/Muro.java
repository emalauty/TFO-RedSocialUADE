package modelo.muro;

import persistencia.GestorDeDatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Gestiona la colección de publicaciones de la red social.
 */
public class Muro {

    private List<Publicacion> listaDeTodasLasPublicaciones;
    private PriorityQueue<Publicacion> publicacionesPorRelevancia;
    private GestorDeDatos gestorDeDatos; // [NUEVO] Atributo para manejar los datos

    public Muro() {
        // --- Cambios en el constructor ---
        this.gestorDeDatos = new GestorDeDatos();
        // En lugar de crear una lista vacía, la cargamos desde el archivo JSON
        this.listaDeTodasLasPublicaciones = gestorDeDatos.cargarPublicaciones();

        // Inicializamos la PriorityQueue
        this.publicacionesPorRelevancia = new PriorityQueue<>(
                (p1, p2) -> Long.compare(p2.calcularPuntajeDeRelevancia(), p1.calcularPuntajeDeRelevancia())
        );
        // [NUEVO] Añadimos todas las publicaciones cargadas a la cola de prioridad
        if (this.listaDeTodasLasPublicaciones != null) {
            this.publicacionesPorRelevancia.addAll(this.listaDeTodasLasPublicaciones);
        }
    }

    // [NUEVO] metodo para que la ventana pueda acceder a la lista para guardarla
    public List<Publicacion> getListaDeTodasLasPublicaciones() {
        return listaDeTodasLasPublicaciones;
    }


    /**
     * Añade una publicación a ambas colecciones.
     */
    public void agregarPublicacion(Publicacion publicacion) {
        this.listaDeTodasLasPublicaciones.add(publicacion);
        this.publicacionesPorRelevancia.add(publicacion);
    }

    /**
     * Devuelve una lista ordenada por fecha (más nueva primero) invirtiendo la lista.
     */
    public List<Publicacion> getPublicacionesOrdenadasPorFecha() {
        List<Publicacion> publicacionesInvertidas = new ArrayList<>(this.listaDeTodasLasPublicaciones);
        Collections.reverse(publicacionesInvertidas);
        return publicacionesInvertidas;
    }

    /**
     * Devuelve una lista ordenada por relevancia usando la PriorityQueue.
     */
    public List<Publicacion> getPublicacionesPorRelevancia() {
        List<Publicacion> resultado = new ArrayList<>();
        PriorityQueue<Publicacion> copiaCola = new PriorityQueue<>(this.publicacionesPorRelevancia);
        while (!copiaCola.isEmpty()) {
            resultado.add(copiaCola.poll());
        }
        return resultado;
    }
}