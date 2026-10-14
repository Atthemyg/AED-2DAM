# CodeLearn JDBC — Entrega 2

**Proyecto del alumnado** · Java 21 · Maven · SQLite3 · JDBC · JUnit 5

## Objetivo

Implementar **12 métodos estáticos** en `src/main/java/es/codelearn/Consultas.java` para practicar consultas SQL desde Java y autocorregir los resultados mediante **24 tests JUnit**.

No hay interfaces, DAO, repositorios ni objetos de modelo. Tampoco se debe pasar la conexión como argumento ni declarar `throws SQLException` en los métodos de `Consultas`.

## Requisitos

- Java JDK 21
- Apache Maven 3.9 o compatible
- Terminal abierta en la carpeta que contiene `pom.xml`
- Internet durante la primera ejecución de Maven para descargar dependencias (después quedan en caché)

## Datos

`datos/codelearn_modelos.sqlite3` contiene **100 productos y 100 vehículos**. Son datos de ejemplo de los modelos utilizados en las prácticas anteriores.

| Tabla | Columnas |
| --- | --- |
| `Productos` | `id` INTEGER, `nombre` TEXT, `precio` REAL |
| `Vehiculos` | `matricula` TEXT, `marca` TEXT, `modelo` TEXT, `anio` INTEGER |

Los tests usan una base **temporal con cinco registros por tabla** para verificar respuestas previsibles; no alteran los datos originales.

## Organización

```text
codelearn-jdbc-entrega2/
├── pom.xml
├── README.md
├── .gitignore
├── datos/codelearn_modelos.sqlite3
└── src/
    ├── main/java/es/codelearn/
    │   ├── BaseDatos.java
    │   ├── Consultas.java        # TODO: completar los 12 métodos
    │   └── Main.java
    └── test/java/es/codelearn/
        └── ConsultasTest.java   # 24 tests de autocorrección
```

## Comandos

```bash
mvn clean test
mvn compile exec:java
```

Para ejecutar un único test: `mvn -Dtest=ConsultasTest#t01_contarProductos test`.

Ver resultados y errores detallados en `target/surefire-reports/`.

**Importante:** inicialmente los métodos son plantillas con valores de reserva; varios tests fallarán. Eso forma parte de la actividad.

## Los 12 ejercicios

Cada método devuelve un **valor**, `boolean` o `List<String>` y gestiona sus propias conexiones mediante `BaseDatos.conectar()`.

| Nº | Método | Conceptos | Resultado esperado |
| --- | --- | --- | --- |
| 1 | `contarProductos()` | COUNT | Número total de productos |
| 2 | `contarVehiculos()` | COUNT | Número total de vehículos |
| 3 | `existeProducto(int id)` | WHERE / EXISTS | true solo si existe |
| 4 | `existeVehiculo(String matricula)` | WHERE / EXISTS | true solo si existe |
| 5 | `contarProductosCaros(double precio)` | WHERE, COUNT | Precio estrictamente mayor |
| 6 | `contarVehiculosMarca(String marca)` | WHERE, COUNT | Coincidencia exacta de marca |
| 7 | `obtenerPrecioMedio()` | AVG | Media de precios, 0 si no hay productos |
| 8 | `obtenerPrecioMaximo()` | MAX | Mayor precio, 0 si no hay productos |
| 9 | `obtenerPrecioMinimo()` | MIN | Menor precio, 0 si no hay productos |
| 10 | `listarMarcas()` | DISTINCT, ORDER BY | Marcas sin repetir, alfabéticas |
| 11 | `listarProductosCaros(double precio)` | WHERE, ORDER BY | Nombres con precio mayor, ordenados por precio DESC y nombre ASC |
| 12 | `listarMarcasFrecuentes(int minimo)` | GROUP BY, HAVING | Marcas con COUNT >= mínimo, ordenadas por cantidad DESC y marca ASC |

## Cómo resolver cada método

1. Escribe la sentencia SQL con los nombres de columnas reales.
2. Crea la conexión dentro del método usando `BaseDatos.conectar()`.
3. Usa `PreparedStatement` y `?` en los ejercicios con parámetros. **No concatenes parámetros en el SQL**.
4. Ejecuta `executeQuery()` y recorre `ResultSet`. Para listas usa `while (rs.next())` y `ArrayList<String>`.
5. Cierra `Connection`, `PreparedStatement` y `ResultSet` con `try-with-resources`.
6. Captura `SQLException` dentro del método y devuelve el valor acordado: `-1` para errores en `int`, `Double.NaN` en `double`, `false` en booleanos y lista vacía en listas. Para agregados sobre tablas vacías (`AVG`, `MIN`, `MAX`), devuelve **0.0**, no `NaN`.
7. Ejecuta `mvn test` y examina qué casos pasan y cuáles fallan.

### Ejemplo de patrón de trabajo (no es la solución de ningún ejercicio)

```java
public static boolean hayVehiculosDesde(int anio) {
    String sql = "SELECT 1 FROM Vehiculos WHERE anio >= ? LIMIT 1";
    try (var conn = BaseDatos.conectar();
         var stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, anio);
        try (var rs = stmt.executeQuery()) {
            return rs.next();
        }
    } catch (java.sql.SQLException e) {
        System.err.println("Error SQL: " + e.getMessage());
        return false;
    }
}
```

El ejemplo muestra cómo gestionar internamente JDBC. **No hay que añadirlo al proyecto.**

## Qué evalúan los tests

Hay dos casos por ejercicio: funcionamiento habitual y caso límite (tabla vacía, sin coincidencias, límite exacto u ordenación). Son pruebas funcionales: no obligan a escribir el SQL exactamente igual que un modelo.

En esta entrega se considera el paso de los 24 tests una **autocorrección formativa**. El código y los tests están visibles: no sustituye una evaluación supervisada con casos ocultos.

## Próxima entrega

Refactorizar el código de `Consultas.java` hacia clases de modelo, DAO e interfaces, cuando ya se entienda cómo funcionan SQL, JDBC y las pruebas.
