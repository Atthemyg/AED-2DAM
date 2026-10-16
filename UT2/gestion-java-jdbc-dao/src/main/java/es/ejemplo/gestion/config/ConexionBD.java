package es.ejemplo.gestion.config;

import es.ejemplo.gestion.exception.AplicationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre una conexión en la base SQLite indicada por el constructor. */
public class ConexionBD {

    private final String url;

    // Verifica la url con Path y Files: SQLite crearía un fichero vacío si no existiera
    public ConexionBD(String url) {
        this.url = url;
        Path path = Path.of(url.replace("jdbc:sqlite:", ""));
        if (!Files.exists(path)) {
            throw new AplicationException("No existe la base de datos: " + path);
        }
    }

    // Abrir conexión
    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new AplicationException("Error conectando con SQLite", e);
        }
    }
}
