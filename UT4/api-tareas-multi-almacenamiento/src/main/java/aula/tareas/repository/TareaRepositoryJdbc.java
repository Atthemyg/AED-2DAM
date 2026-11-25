package aula.tareas.repository;

import aula.tareas.entity.TareaEntityJdbc;
import aula.tareas.mapper.TareaMapperJdbc;
import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryJdbc")
public class TareaRepositoryJdbc implements ITareaRepository {

    private final JdbcTemplate jdbc;

    public TareaRepositoryJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Tarea> findAll() {
        String sql = "SELECT id, titulo FROM tareas_jdbc ORDER BY id";
        List<Tarea> tareas = new ArrayList<>();
        for (Map<String, Object> fila : jdbc.queryForList(sql)) {
            TareaEntityJdbc entity = new TareaEntityJdbc(
                    ((Number) fila.get("id")).longValue(),
                    (String) fila.get("titulo"));
            tareas.add(TareaMapperJdbc.toDomain(entity));
        }
        return tareas;
    }

    @Override
    public Optional<Tarea> findById(long id) {
        String sql = "SELECT id, titulo FROM tareas_jdbc WHERE id = ?";
        List<Map<String, Object>> filas = jdbc.queryForList(sql, id);
        if (filas.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Object> fila = filas.get(0);
        TareaEntityJdbc entity = new TareaEntityJdbc(
                ((Number) fila.get("id")).longValue(),
                (String) fila.get("titulo"));
        return Optional.of(TareaMapperJdbc.toDomain(entity));
    }

    @Override
    public Tarea create(Tarea tarea) {
        String sql = "INSERT INTO tareas_jdbc (titulo) VALUES (?)";
        TareaEntityJdbc entity = TareaMapperJdbc.toEntity(tarea);
        jdbc.update(sql, entity.nombreTarea());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        return new Tarea(id, tarea.nombre());
    }

    @Override
    public Optional<Tarea> update(Tarea tarea) {
        String sql = "UPDATE tareas_jdbc SET titulo = ? WHERE id = ?";
        TareaEntityJdbc entity = TareaMapperJdbc.toEntity(tarea);
        int filasAfectadas = jdbc.update(sql, entity.nombreTarea(), entity.idTarea());
        if (filasAfectadas == 1) {
            return Optional.of(tarea);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM tareas_jdbc WHERE id = ?";
        return jdbc.update(sql, id) == 1;
    }
}