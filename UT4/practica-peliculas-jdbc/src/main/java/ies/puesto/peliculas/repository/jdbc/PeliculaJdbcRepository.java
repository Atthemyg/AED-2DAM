package ies.puesto.peliculas.repository.jdbc;

import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.repository.PeliculaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class PeliculaJdbcRepository implements PeliculaRepository {

    private final JdbcTemplate jdbc;
    private final PeliculaRowMapper rowMapper;
    private final PeliculaEntityMapper entityMapper;

    public PeliculaJdbcRepository(JdbcTemplate jdbc, PeliculaRowMapper rowMapper, PeliculaEntityMapper entityMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<Pelicula> buscarTodas() {
        String sql = """
                SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos
                FROM peliculas ORDER BY pelicula_codigo""";
        return jdbc.query(sql, rowMapper).stream()
                .map(entityMapper::aPelicula)
                .toList();
    }

    @Override
    public Optional<Pelicula> buscarPorCodigo(String identificador) {
        String sql = """
                SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos
                FROM peliculas WHERE pelicula_codigo = ?""";
        return jdbc.query(sql, rowMapper, identificador).stream()
                .findFirst()
                .map(entityMapper::aPelicula);
    }

    @Override
    public boolean insertarSiAusente(Pelicula pelicula) {
        String sql = """
                INSERT INTO peliculas (pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos)
                VALUES (?, ?, ?, ?) ON CONFLICT(pelicula_codigo) DO NOTHING""";
        PeliculaEntity fila = entityMapper.aEntity(pelicula);
        int filasAfectadas = jdbc.update(sql,
                fila.getCodigoFila(), fila.getTituloFila(), fila.getAnioFila(), fila.getMinutosFila());
        return filasAfectadas == 1;
    }

    @Override
    public boolean actualizarSiExiste(Pelicula pelicula) {
        String sql = """
                UPDATE peliculas SET titulo_texto = ?, estreno_anio = ?, duracion_minutos = ?
                WHERE pelicula_codigo = ?""";
        PeliculaEntity fila = entityMapper.aEntity(pelicula);
        int filasAfectadas = jdbc.update(sql,
                fila.getTituloFila(), fila.getAnioFila(), fila.getMinutosFila(), fila.getCodigoFila());
        return filasAfectadas == 1;
    }

    @Override
    public boolean eliminar(String identificador) {
        String sql = "DELETE FROM peliculas WHERE pelicula_codigo = ?";
        return jdbc.update(sql, identificador) == 1;
    }
}