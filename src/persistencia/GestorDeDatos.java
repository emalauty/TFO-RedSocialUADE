package persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import modelo.muro.Publicacion;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de guardar y cargar la lista de publicaciones en un archivo JSON.
 */
public class GestorDeDatos {

    // El nombre del archivo donde se guardarán los datos.
    private static final String RUTA_ARCHIVO = "publicaciones.json";
    private Gson gson;

    public GestorDeDatos() {
        // Usamos GsonBuilder para que el JSON se guarde con un formato legible (pretty printing).
        // Le decimos a GsonBuilder que, cada vez que vea un LocalDateTime, use nuestro adaptador.
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public void guardarPublicaciones(List<Publicacion> publicaciones) {
        // Usamos try-with-resources para que el FileWriter se cierre automáticamente.
        try (FileWriter writer = new FileWriter(RUTA_ARCHIVO)) {
            gson.toJson(publicaciones, writer);
            System.out.println("Publicaciones guardadas correctamente en " + RUTA_ARCHIVO);
        } catch (IOException e) {
            System.err.println("Error al guardar las publicaciones: " + e.getMessage());
        }
    }

    public List<Publicacion> cargarPublicaciones() {
        try (FileReader reader = new FileReader(RUTA_ARCHIVO)) {
            // Gson necesita saber que queremos convertir el JSON a una Lista de Publicaciones.
            // Para esto se usa un TypeToken.
            Type tipoLista = new TypeToken<ArrayList<Publicacion>>() {}.getType();

            List<Publicacion> publicaciones = gson.fromJson(reader, tipoLista);

            System.out.println("Publicaciones cargadas correctamente desde " + RUTA_ARCHIVO);
            // Si el archivo JSON está vacío, publicaciones podría ser null.
            return publicaciones != null ? publicaciones : new ArrayList<>();
        } catch (IOException e) {
            // Esto es normal la primera vez que se ejecuta el programa.
            System.out.println("No se encontró el archivo de publicaciones, se iniciará con una lista vacía.");
            return new ArrayList<>();
        }
    }
}