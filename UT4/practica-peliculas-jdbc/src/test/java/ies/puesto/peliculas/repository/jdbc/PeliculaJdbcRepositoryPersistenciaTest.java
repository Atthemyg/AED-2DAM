package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeliculaJdbcRepositoryPersistenciaTest {

    @TempDir
    Path directorio;

    private Path fichero;

    @BeforeEach
    void prepararBase() {
        fichero = directorio.resolve("peliculas-test.sqlite3");
        JdbcTemplate jdbc = nuevaConexion();
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS peliculas (
                    pelicula_codigo TEXT NOT NULL PRIMARY KEY
                        CHECK (pelicula_codigo GLOB 'PEL[0-9][0-9][0-9]'),
                    titulo_texto TEXT NOT NULL
                        CHECK (length(trim(titulo_texto)) BETWEEN 1 AND 120),
                    estreno_anio INTEGER NOT NULL
                        CHECK (typeof(estreno_anio) = 'integer' AND estreno_anio BETWEEN 1888 AND 2100),
                    duracion_minutos INTEGER NOT NULL
                        CHECK (typeof(duracion_minutos) = 'integer' AND duracion_minutos BETWEEN 1 AND 1000)
                )""");
    }

    private JdbcTemplate nuevaConexion() {
        DriverManagerDataSource origen = new DriverManagerDataSource("jdbc:sqlite:" + fichero);
        origen.setDriverClassName("org.sqlite.JDBC");
        return new JdbcTemplate(origen);
    }

    private PeliculaJdbcRepository nuevoRepositorio() {
        return new PeliculaJdbcRepository(nuevaConexion(), new PeliculaRowMapper(), new PeliculaEntityMapper());
    }

    private static Pelicula akira() {
        return new Pelicula("PEL010", "Akira", 1988, 124);
    }

    @Test
    void losDatosSiguenTrasReiniciar() {
        assertTrue(nuevoRepositorio().insertarSiAusente(akira()));

        PeliculaJdbcRepository trasReinicio = nuevoRepositorio();

        Optional<Pelicula> leida = trasReinicio.buscarPorCodigo("PEL010");
        assertTrue(leida.isPresent());
        assertEquals("PEL010", leida.get().getIdentificador());
        assertEquals("Akira", leida.get().getNombre());
        assertEquals(1988, leida.get().getAnioLanzamiento());
        assertEquals(124, leida.get().getMinutosProyeccion());
    }

    @Test
    void laActualizacionSigueTrasReiniciar() {
        nuevoRepositorio().insertarSiAusente(akira());
        assertTrue(nuevoRepositorio().actualizarSiExiste(new Pelicula("PEL010", "Akira (remasterizada)", 1988, 125)));

        Pelicula leida = nuevoRepositorio().buscarPorCodigo("PEL010").orElseThrow();

        assertEquals("Akira (remasterizada)", leida.getNombre());
        assertEquals(125, leida.getMinutosProyeccion());
    }

    @Test
    void elBorradoSigueTrasReiniciar() {
        nuevoRepositorio().insertarSiAusente(akira());
        assertTrue(nuevoRepositorio().eliminar("PEL010"));

        assertTrue(nuevoRepositorio().buscarPorCodigo("PEL010").isEmpty());
    }

    @Test
    void insertarUnCodigoExistenteDevuelveFalseYNoCambiaLaFila() {
        PeliculaJdbcRepository repositorio = nuevoRepositorio();
        assertTrue(repositorio.insertarSiAusente(akira()));

        boolean segundoIntento = repositorio.insertarSiAusente(new Pelicula("PEL010", "Otro titulo", 2000, 90));

        assertFalse(segundoIntento);
        assertEquals("Akira", repositorio.buscarPorCodigo("PEL010").orElseThrow().getNombre());
    }

    @Test
    void actualizarUnCodigoAusenteDevuelveFalseYNoInsertaFila() {
        PeliculaJdbcRepository repositorio = nuevoRepositorio();

        boolean resultado = repositorio.actualizarSiExiste(akira());

        assertFalse(resultado);
        assertTrue(repositorio.buscarPorCodigo("PEL010").isEmpty());
        assertTrue(repositorio.buscarTodas().isEmpty());
    }

    @Test
    void eliminarUnCodigoAusenteDevuelveFalse() {
        assertFalse(nuevoRepositorio().eliminar("PEL099"));
    }

    @Test
    void buscarUnCodigoAusenteDevuelveOptionalVacioSinExcepcion() {
        assertTrue(nuevoRepositorio().buscarPorCodigo("PEL099").isEmpty());
    }

    @Test
    void buscarTodasDevuelveLasPeliculasOrdenadasPorCodigo() {
        PeliculaJdbcRepository repositorio = nuevoRepositorio();
        repositorio.insertarSiAusente(new Pelicula("PEL020", "Totoro", 1988, 86));
        repositorio.insertarSiAusente(akira());

        List<Pelicula> todas = nuevoRepositorio().buscarTodas();

        assertEquals(2, todas.size());
        assertEquals("PEL010", todas.get(0).getIdentificador());
        assertEquals("PEL020", todas.get(1).getIdentificador());
    }
}
