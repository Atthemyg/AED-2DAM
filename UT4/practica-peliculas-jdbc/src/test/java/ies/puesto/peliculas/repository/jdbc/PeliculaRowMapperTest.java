package ies.puesto.peliculas.repository.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeliculaRowMapperTest {

    private final PeliculaRowMapper mapper = new PeliculaRowMapper();

    @Test
    void mapRowLeeLasCuatroColumnasEnLaEntidad() throws Exception {
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement sentencia = conexion.createStatement();
             ResultSet rs = sentencia.executeQuery("""
                     SELECT 'PEL001' AS pelicula_codigo,
                            'El viaje de Chihiro' AS titulo_texto,
                            2001 AS estreno_anio,
                            125 AS duracion_minutos""")) {
            rs.next();

            PeliculaEntity fila = mapper.mapRow(rs, 0);

            assertEquals("PEL001", fila.getCodigoFila());
            assertEquals("El viaje de Chihiro", fila.getTituloFila());
            assertEquals(2001, fila.getAnioFila());
            assertEquals(125, fila.getMinutosFila());
        }
    }
}
