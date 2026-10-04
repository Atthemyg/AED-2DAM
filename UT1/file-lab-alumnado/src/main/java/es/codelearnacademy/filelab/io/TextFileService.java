package es.codelearnacademy.filelab.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class TextFileService {

    public boolean escribir(Path path, String contenido) {
        try {
            Files.writeString(path, contenido, StandardCharsets.UTF_8); // Escribe contenido en el fichero path utilizando UTF-8
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String leer(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8); // Lee el contenido y lo devuelve como un String -> "Hola\nEsto es una prueba"
        } catch (IOException e) {
            return "";
        }
    }

    public boolean escribirLineas(Path path, List<String> lineas) {
        try {
            Files.write(path, lineas, StandardCharsets.UTF_8); // Cada elemento de la lista se escribe como una línea diferente
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> leerLineas(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8); // Devuelve una lista -> List.of("Hola", "Adiós", "Hasta luego")
        } catch (IOException e) {
            return List.of();
        }
    }

    public boolean anexar(Path path, String contenido) {
        try {
            Files.writeString(path, contenido,
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND); // APPEND -> no borra lo que había anteriormente, sino que escribe después
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
