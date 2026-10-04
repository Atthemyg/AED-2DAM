package es.codelearnacademy.filelab.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

public class FilesService {

    public boolean existe(Path path) {
        return Files.exists(path);
    }

    public Optional<Path> crearDirectorio(Path path) { // Crea solo uno
        try {
            Files.createDirectory(path);
            return Optional.of(path);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Path> crearDirectorios(Path path) { // Puede crear toda la jerarquía
        try {
            Files.createDirectories(path);
            return Optional.of(path);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Path> crearArchivo(Path path) {
        try {
            Files.createFile(path);
            return Optional.of(path);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Path> copiar(Path origen, Path destino) {
        try {
            Files.copy(origen, destino);
            return Optional.of(destino); // El original sigue existiendo
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Path> mover(Path origen, Path destino) {
        try {
            Files.move(origen, destino); // El original ya no existe
            return Optional.of(destino);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean eliminar(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public OptionalLong tamanio(Path path) {
        try {
            return OptionalLong.of(Files.size(path)); // OptionalLong.of(0) → el archivo mide 0 bytes | OptionalLong.empty()  → no hemos podido obtener el tamaño
        } catch (IOException e) {
            return OptionalLong.empty();
        }
    }
}
