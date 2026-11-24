package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeliculaEntityMapperTest {

    private final PeliculaEntityMapper mapper = new PeliculaEntityMapper();

    @Test
    void aEntityConservaLosCuatroValores() {
        Pelicula pelicula = new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125);

        PeliculaEntity fila = mapper.aEntity(pelicula);

        assertEquals("PEL001", fila.getCodigoFila());
        assertEquals("El viaje de Chihiro", fila.getTituloFila());
        assertEquals(2001, fila.getAnioFila());
        assertEquals(125, fila.getMinutosFila());
    }

    @Test
    void aPeliculaConservaLosCuatroValores() {
        PeliculaEntity fila = new PeliculaEntity("PEL002", "Mi vecino Totoro", 1988, 86);

        Pelicula pelicula = mapper.aPelicula(fila);

        assertEquals("PEL002", pelicula.getIdentificador());
        assertEquals("Mi vecino Totoro", pelicula.getNombre());
        assertEquals(1988, pelicula.getAnioLanzamiento());
        assertEquals(86, pelicula.getMinutosProyeccion());
    }

    @Test
    void ida_y_vuelta_devuelve_los_mismos_valores() {
        Pelicula original = new Pelicula("PEL010", "Akira", 1988, 124);

        Pelicula resultado = mapper.aPelicula(mapper.aEntity(original));

        assertEquals(original.getIdentificador(), resultado.getIdentificador());
        assertEquals(original.getNombre(), resultado.getNombre());
        assertEquals(original.getAnioLanzamiento(), resultado.getAnioLanzamiento());
        assertEquals(original.getMinutosProyeccion(), resultado.getMinutosProyeccion());
    }
}
