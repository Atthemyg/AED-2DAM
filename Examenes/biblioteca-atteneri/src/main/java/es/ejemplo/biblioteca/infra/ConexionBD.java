package es.ejemplo.biblioteca.infra;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    public final String url;

    public ConexionBD(String url) {
        this.url = url;
    }

    public Connection obtenerConexion() throws SQLException {
        return abrir(url);
    }

    public static Connection abrir(String url) throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL SQLite no válida");
        }
        String ruta = url.substring("jdbc:sqlite:".length());
        if (!ruta.equals(":memory:") && !ruta.startsWith("file:") && !Files.isRegularFile(Path.of(ruta))) {
            throw new IllegalArgumentException("La base de datos no existe: " + ruta);
        }
        return DriverManager.getConnection(url);
    }
}
