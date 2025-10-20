package persistencia;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Este adaptador le enseña a Gson cómo convertir objetos LocalDateTime
 * a un String y viceversa, para poder guardarlos en JSON.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    // Define el formato en que se guardará la fecha. ISO_LOCAL_DATE_TIME es un estándar (ej: "2025-10-20T15:30:00")
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}