package es.codelearn;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/** Ejercicios del alumnado: completa los 12 métodos sin cambiar sus firmas. */
public final class Consultas {
    private Consultas() { }

    /** Ejercicio: COUNT(*) de Productos; debe devolver 0 si la tabla está vacía. */
    public static int contarProductos() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return -1;
    }

    /** Ejercicio: COUNT(*) de Vehiculos. */
    public static int contarVehiculos() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return -1;
    }

    /** Ejercicio: EXISTS o COUNT de Productos filtrando por id con ?. */
    public static boolean existeProducto(int id) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return false;
    }

    /** Ejercicio: EXISTS o COUNT de Vehiculos filtrando por matrícula con ?. */
    public static boolean existeVehiculo(String matricula) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return false;
    }

    /** Ejercicio: COUNT(*) con precio estrictamente MAYOR que el parámetro. */
    public static int contarProductosCaros(double precio) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return -1;
    }

    /** Ejercicio: COUNT(*) de la marca exacta proporcionada. */
    public static int contarVehiculosMarca(String marca) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return -1;
    }

    /** Ejercicio: AVG(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMedio() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return Double.NaN;
    }

    /** Ejercicio: MAX(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMaximo() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return Double.NaN;
    }

    /** Ejercicio: MIN(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMinimo() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return Double.NaN;
    }

    /** Ejercicio: DISTINCT marca de Vehiculos ordenadas alfabéticamente ASC. */
    public static List<String> listarMarcas() {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return List.of();
    }

    /** Ejercicio: Nombres de Productos con precio > parámetro, ORDER BY precio DESC, nombre ASC. */
    public static List<String> listarProductosCaros(double precio) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return List.of();
    }

    /** Ejercicio: Marcas agrupadas con COUNT(*) >= minimo; ORDER BY cantidad DESC, marca ASC. */
    public static List<String> listarMarcasFrecuentes(int minimo) {
        // TODO: abre la conexión con BaseDatos.conectar(),
        // usa PreparedStatement y ResultSet dentro de try-with-resources.
        // Captura SQLException aquí: no añadas throws SQLException.
        // Ante error SQL: int=-1, double=NaN, boolean=false, lista vacía.
        return List.of();
    }

}