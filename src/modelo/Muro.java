package modelo;

import java.util.*;

public class Muro {
    private List<Publicacion> listaDeTodasLasPublicaciones;
    private PriorityQueue<Publicacion> publicacionesPorRelevancia;

    public Muro() {
        this.listaDeTodasLasPublicaciones = new ArrayList<>();
        this.publicacionesPorRelevancia = new PriorityQueue<>(new Comparator<Publicacion>() {
            @Override
            public int compare(Publicacion p1, Publicacion p2) {
                return Long.compare(p2.calcularPuntajeDeRelevancia(), p1.calcularPuntajeDeRelevancia());
            }
        });
    }

    public void addPublicacion(Publicacion publicacion) {
        this.listaDeTodasLasPublicaciones.add(publicacion);
        this.publicacionesPorRelevancia.add(publicacion);
    }

    public List<Publicacion> getPublicacionesOrdenadasPorFecha() {
        // 1. Creamos una copia para no modificar la lista original.
        List<Publicacion> publicacionesInvertidas = new ArrayList<>(this.listaDeTodasLasPublicaciones);

        // 2. Invertimos la copia. Lo que estaba al final ahora está al principio.
        Collections.reverse(publicacionesInvertidas);

        // 3. Devolvemos la lista ya invertida.
        return publicacionesInvertidas;
    }


    public List<Publicacion> getPublicacionesPorRelevancia() {
        List<Publicacion> resultado = new ArrayList<>();
        PriorityQueue<Publicacion> copia = new PriorityQueue<>(this.publicacionesPorRelevancia);
        while (!copia.isEmpty()) {
            resultado.add(copia.poll());
        }
        return resultado;
    }
}

