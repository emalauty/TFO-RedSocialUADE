package modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Representa una única publicación en la red social.
 */
public class Publicacion {

    // Atributos privados de la clase
    private String autor;
    private String contenido;
    private LocalDateTime fechaDeCreacion;
    private int cantidadDeMeGusta;

    /**
     * Constructor para crear una nueva publicación.
     * La fecha de creación se asigna automáticamente al momento actual
     * y los "me gusta" se inician en cero.
     *
     * @param autor El nombre del usuario que crea la publicación.
     * @param contenido El texto de la publicación.
     */
    public Publicacion(String autor, String contenido) {
        this.autor = autor;
        this.contenido = contenido;
        this.fechaDeCreacion = LocalDateTime.now(); // Asigna la fecha y hora actual
        this.cantidadDeMeGusta = 0;                  // Inicia en 0 "me gusta"
    }

    // --- Métodos de la clase ---

    /**
     * Incrementa en uno la cantidad de "me gusta" de la publicación.
     * Este método simula la acción de un usuario al presionar el botón "Me gusta".
     */
    public void darMeGusta() {
        this.cantidadDeMeGusta++; // Incrementa el contador en 1
    }

    public void quitarMeGusta() {
        if (this.cantidadDeMeGusta > 0) {
            this.cantidadDeMeGusta--;
        }
    }


    // --- Getters y Setters ---
    // Los "getters" nos permiten leer los valores de los atributos desde fuera de la clase.

    public String getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public int getCantidadDeMeGusta() {
        return cantidadDeMeGusta;
    }

    // Opcional: Permitimos que el contenido de una publicación se pueda editar.
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long calcularPuntajeDeRelevancia(){
        LocalDateTime ahora = LocalDateTime.now();

        long horasDeAntiguedad = ChronoUnit.HOURS.between(this.fechaDeCreacion, ahora);

        return this.cantidadDeMeGusta - horasDeAntiguedad;
    }
}