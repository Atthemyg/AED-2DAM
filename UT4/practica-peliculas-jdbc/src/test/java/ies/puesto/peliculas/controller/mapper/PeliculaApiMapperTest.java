package ies.puesto.peliculas.controller.mapper;

import ies.puesto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puesto.peliculas.controller.api.PeliculaRequest;
import ies.puesto.peliculas.controller.api.PeliculaResponse;
import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puesto.peliculas.service.modelo.DatosEdicionPelicula;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeliculaApiMapperTest {

    private final PeliculaApiMapper mapper = new PeliculaApiMapper();

    @Test
    void aDatosCreacionConservaLosCuatroValores() {
        PeliculaRequest peticion = new PeliculaRequest("PEL010", "Akira", 1988, 124);

        DatosCreacionPelicula datos = mapper.aDatosCreacion(peticion);

        assertEquals("PEL010", datos.referenciaSolicitada());
        assertEquals("Akira", datos.nombreSolicitado());
        assertEquals(1988, datos.anioSolicitado());
        assertEquals(124, datos.minutosSolicitados());
    }

    @Test
    void aDatosEdicionConservaLosTresValoresSinCodigo() {
        PeliculaEdicionRequest peticion = new PeliculaEdicionRequest("Chihiro (remasterizada)", 2001, 126);

        DatosEdicionPelicula datos = mapper.aDatosEdicion(peticion);

        assertEquals("Chihiro (remasterizada)", datos.nombreSolicitado());
        assertEquals(2001, datos.anioSolicitado());
        assertEquals(126, datos.minutosSolicitados());
    }

    @Test
    void aRespuestaConservaLosCuatroValores() {
        Pelicula pelicula = new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125);

        PeliculaResponse respuesta = mapper.aRespuesta(pelicula);

        assertEquals("PEL001", respuesta.codigo());
        assertEquals("El viaje de Chihiro", respuesta.titulo());
        assertEquals(2001, respuesta.anioEstreno());
        assertEquals(125, respuesta.duracionMinutos());
    }

    @Test
    void aRespuestasConvierteTodasLasPeliculasEnOrden() {
        List<Pelicula> peliculas = List.of(
                new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125),
                new Pelicula("PEL002", "Mi vecino Totoro", 1988, 86));

        List<PeliculaResponse> respuestas = mapper.aRespuestas(peliculas);

        assertEquals(2, respuestas.size());
        assertEquals(new PeliculaResponse("PEL001", "El viaje de Chihiro", 2001, 125), respuestas.get(0));
        assertEquals(new PeliculaResponse("PEL002", "Mi vecino Totoro", 1988, 86), respuestas.get(1));
    }

    @Test
    void aRespuestasConListaVaciaDevuelveListaVacia() {
        assertTrue(mapper.aRespuestas(List.of()).isEmpty());
    }
}
