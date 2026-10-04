package es.codelearnacademy.filelab.io;

import java.nio.file.Path;

public class PathService {

    public Path crear(String primero, String... partes) {
        return Path.of(primero, partes); // data/productos.csv
    }

    public String nombre(Path path) {
        return path.getFileName().toString(); // data/productos.csv → productos.csv
    }

    public Path padre(Path path) {
        return path.getParent(); // data/productos.csv → data
    }

    public Path absoluto(Path path) {
        return path.toAbsolutePath(); // /home/alumno/proyecto/data/productos.csv
    }

    public Path normalizar(Path path) {
        return path.normalize(); // data/./temp/../productos.csv -> data/productos.csv (elimina los elementos redundantes)
    }

    public boolean esAbsoluto(Path path) {
        return path.isAbsolute(); // data/productos.csv -> false | /home/alumno/data/productos.csv -> true
    }

    public Path resolver(Path base, String otro) {
        return base.resolve(otro); // base = data otro = productos.csv -> data/productos.csv
    }

    public Path relativizar(Path base, Path destino) {
        return base.relativize(destino); // base = data  destino = data/productos.csv -> productos.csv (calcula qué recorrido hay que hacer desde base para llegar a destino)
    }

    public String extension(Path path) {
        String nombre = path.getFileName().toString();
        int posicion = nombre.lastIndexOf(".");

        if (posicion == -1) { // -1 valor por defecto de Java
            return "";
        }

        return nombre.substring(posicion + 1);
    }
}
