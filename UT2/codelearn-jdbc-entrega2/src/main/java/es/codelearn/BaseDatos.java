package es.codelearn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Configuración interna de la base de datos. Los métodos de Consultas no reciben conexiones. */
public final class BaseDatos {
    private static String url = "jdbc:sqlite:datos/codelearn_modelos.sqlite3";

    private BaseDatos() { }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /** Permite usar una base independiente en los tests. */
    public static void configurar(String nuevaUrl) {
        if (nuevaUrl == null || !nuevaUrl.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("URL SQLite no válida");
        }
        url = nuevaUrl;
    }
}
