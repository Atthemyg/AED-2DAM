package ies.puesto.peliculas.service.mapper;

import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puesto.peliculas.service.modelo.DatosEdicionPelicula;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeliculaDominioMapperTest {

    private final PeliculaDominioMapper mapper = new PeliculaDominioMapper();

    @Test
    void aPeliculaDesdeCreacionConservaLosCuatroValores() {
        DatosCreacionPelicula datos = new DatosCreacionPelicula("PEL010", "Akira", 1988, 124);

        Pelicula pelicula = mapper.aPelicula(datos);

        assertEquals("PEL010", pelicula.getIdentificador());
        assertEquals("Akira", pelicula.getNombre());
        assertEquals(1988, pelicula.getAnioLanzamiento());
        assertEquals(124, pelicula.getMinutosProyeccion());
    }

    @Test
    void aPeliculaDesdeEdicionUsaElCodigoDeLaRuta() {
        DatosEdicionPelicula datos = new DatosEdicionPelicula("Chihiro (remasterizada)", 2001, 126);

        Pelicula pelicula = mapper.aPelicula("PEL001", datos);

        assertEquals("PEL001", pelicula.getIdentificador());
        assertEquals("Chihiro (remasterizada)", pelicula.getNombre());
        assertEquals(2001, pelicula.getAnioLanzamiento());
        assertEquals(126, pelicula.getMinutosProyeccion());
    }
}
