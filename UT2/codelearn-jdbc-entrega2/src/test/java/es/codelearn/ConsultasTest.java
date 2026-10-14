package es.codelearn;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas autocorrectoras: cada test tiene una BD SQLite temporal e independiente. */
class ConsultasTest {
    @TempDir Path temporal;
    private static final String URL_ORIGINAL = "jdbc:sqlite:datos/codelearn_modelos.sqlite3";

    @BeforeEach
    void preparar() throws SQLException {
        String url = "jdbc:sqlite:" + temporal.resolve("prueba.sqlite3").toAbsolutePath();
        BaseDatos.configurar(url);
        try (Connection conn = DriverManager.getConnection(url);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE Productos (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, precio REAL NOT NULL)");
            st.executeUpdate("CREATE TABLE Vehiculos (matricula TEXT PRIMARY KEY, marca TEXT NOT NULL, modelo TEXT NOT NULL, anio INTEGER NOT NULL)");
            st.executeUpdate("INSERT INTO Productos VALUES (1,'Teclado',10.0),(2,'Ratón',20.0),(3,'Monitor',100.0),(4,'Pantalla',100.0),(5,'Cable',5.0)");
            st.executeUpdate("INSERT INTO Vehiculos VALUES ('AAA1','Toyota','Yaris',2015),('BBB2','Toyota','Corolla',2018),('CCC3','Ford','Focus',2011),('DDD4','Ford','Fiesta',2016),('EEE5','Seat','Ibiza',2020)");
        }
    }

    @AfterEach
    void restaurar() { BaseDatos.configurar(URL_ORIGINAL); }

    @Test void t01_contarProductos() { assertEquals(5, Consultas.contarProductos()); }
    @Test void t02_contarProductosVacia() { ejecutar("DELETE FROM Productos"); assertEquals(0, Consultas.contarProductos()); }
    @Test void t03_contarVehiculos() { assertEquals(5, Consultas.contarVehiculos()); }
    @Test void t04_contarVehiculosVacia() { ejecutar("DELETE FROM Vehiculos"); assertEquals(0, Consultas.contarVehiculos()); }
    @Test void t05_existeProducto() { assertTrue(Consultas.existeProducto(3)); }
    @Test void t06_noExisteProducto() { assertFalse(Consultas.existeProducto(999)); }
    @Test void t07_existeVehiculo() { assertTrue(Consultas.existeVehiculo("BBB2")); }
    @Test void t08_noExisteVehiculo() { assertFalse(Consultas.existeVehiculo("ZZZ9")); }
    @Test void t09_contarProductosCaros() { assertEquals(2, Consultas.contarProductosCaros(20.0)); }
    @Test void t10_contarProductosCarosEstricto() { assertEquals(0, Consultas.contarProductosCaros(100.0)); }
    @Test void t11_contarVehiculosMarca() { assertEquals(2, Consultas.contarVehiculosMarca("Toyota")); }
    @Test void t12_contarVehiculosMarcaInexistente() { assertEquals(0, Consultas.contarVehiculosMarca("Volvo")); }
    @Test void t13_precioMedio() { assertEquals(47.0, Consultas.obtenerPrecioMedio(), 0.00001); }
    @Test void t14_precioMedioVacia() { ejecutar("DELETE FROM Productos"); assertEquals(0.0, Consultas.obtenerPrecioMedio(), 0.00001); }
    @Test void t15_precioMaximo() { assertEquals(100.0, Consultas.obtenerPrecioMaximo(), 0.00001); }
    @Test void t16_precioMaximoVacia() { ejecutar("DELETE FROM Productos"); assertEquals(0.0, Consultas.obtenerPrecioMaximo(), 0.00001); }
    @Test void t17_precioMinimo() { assertEquals(5.0, Consultas.obtenerPrecioMinimo(), 0.00001); }
    @Test void t18_precioMinimoVacia() { ejecutar("DELETE FROM Productos"); assertEquals(0.0, Consultas.obtenerPrecioMinimo(), 0.00001); }
    @Test void t19_listarMarcas() { assertEquals(List.of("Ford","Seat","Toyota"), Consultas.listarMarcas()); }
    @Test void t20_listarMarcasVacia() { ejecutar("DELETE FROM Vehiculos"); assertEquals(List.of(), Consultas.listarMarcas()); }
    @Test void t21_listarProductosCaros() { assertEquals(List.of("Monitor","Pantalla","Ratón"), Consultas.listarProductosCaros(10.0)); }
    @Test void t22_listarProductosCarosVacia() { assertEquals(List.of(), Consultas.listarProductosCaros(200.0)); }
    @Test void t23_marcasFrecuentes() { assertEquals(List.of("Ford","Toyota"), Consultas.listarMarcasFrecuentes(2)); }
    @Test void t24_marcasFrecuentesVacia() { assertEquals(List.of(), Consultas.listarMarcasFrecuentes(3)); }

    private void ejecutar(String sql) {
        try (Connection conn = BaseDatos.conectar(); Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException ex) {
            fail("No se pudo preparar el caso de prueba: " + ex.getMessage());
        }
    }
}
