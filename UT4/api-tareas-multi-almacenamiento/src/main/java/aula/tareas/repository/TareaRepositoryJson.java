package aula.tareas.repository;

import aula.tareas.entity.TareaEntityJson;
import aula.tareas.mapper.TareaMapperJson;
import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryJson")
public class TareaRepositoryJson implements ITareaRepository {

    private final Path path;
    private final JsonMapper mapper;

    @Autowired
    public TareaRepositoryJson(JsonMapper mapper) {
        this(Path.of("data", "tarea.json"), mapper);
    }

    public TareaRepositoryJson(Path path, JsonMapper mapper) {
        this.path = path;
        this.mapper = mapper;
        crearFicheroSiNoExiste();
    }

    private void crearFicheroSiNoExiste() {
        try {
            Path carpeta = path.getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized List<Tarea> findAll() {
        List<Tarea> result = new ArrayList<>();
        File fichero = path.toFile();
        if (fichero.length() == 0) {
            return result;
        }
        TareaEntityJson[] entidades = mapper.readValue(fichero, TareaEntityJson[].class);
        for (TareaEntityJson entity : entidades) {
            result.add(TareaMapperJson.toDomain(entity));
        }
        return result;
    }

    @Override
    public synchronized Optional<Tarea> findById(long id) {
        for (Tarea tarea : findAll()) {
            if (tarea.identificador() != null && tarea.identificador() == id) {
                return Optional.of(tarea);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Tarea create(Tarea tarea) {
        List<Tarea> tareas = findAll();
        long maximo = 0;
        for (Tarea existente : tareas) {
            if (existente.identificador() != null && existente.identificador() > maximo) {
                maximo = existente.identificador();
            }
        }
        Tarea nueva = new Tarea(maximo + 1, tarea.nombre().strip());
        tareas.add(nueva);
        saveAll(tareas);
        return nueva;
    }

    @Override
    public synchronized Optional<Tarea> update(Tarea tarea) {
        List<Tarea> tareas = findAll();
        for (int i = 0; i < tareas.size(); i++) {
            Long actual = tareas.get(i).identificador();
            if (actual != null && actual.equals(tarea.identificador())) {
                Tarea actualizada = new Tarea(actual, tarea.nombre().strip());
                tareas.set(i, actualizada);
                saveAll(tareas);
                return Optional.of(actualizada);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized boolean delete(long id) {
        List<Tarea> tareas = findAll();
        for (int i = 0; i < tareas.size(); i++) {
            Long actual = tareas.get(i).identificador();
            if (actual != null && actual == id) {
                tareas.remove(i);
                saveAll(tareas);
                return true;
            }
        }
        return false;
    }

    private void saveAll(List<Tarea> tareas) {
        List<TareaEntityJson> entidades = new ArrayList<>();
        for (Tarea tarea : tareas) {
            entidades.add(TareaMapperJson.toEntity(tarea));
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), entidades);
    }
}