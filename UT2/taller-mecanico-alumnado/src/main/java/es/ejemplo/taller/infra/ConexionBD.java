package es.ejemplo.taller.infra;

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
        Path path = Path.of(url.replace("jdbc:sqlite:", ""));
        if (!Files.exists(path)) {
            throw new SQLException("No existe la base de datos: " + path);
        }
        Connection con = DriverManager.getConnection(url);
        try (Statement st = con.createStatement()) {
            st.execute("SELECT 1 FROM clientes, vehiculos, mecanicos, reparaciones, piezas_reparacion LIMIT 1");
            return con;
        } catch (SQLException e) {
            con.close();
            throw e;
        }
    }
}