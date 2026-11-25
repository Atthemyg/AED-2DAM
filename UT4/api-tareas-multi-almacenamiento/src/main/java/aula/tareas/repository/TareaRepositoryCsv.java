package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import aula.tareas.modelo.Tarea;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.apache.commons.csv.CSVFormat;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Qualifier("tareaRepositoryCsv")
public class TareaRepositoryCsv implements ITareaRepository {

    private final Path path;
    private final AtomicLong secuencia = new AtomicLong();
    private final List<Tarea> tarea = new ArrayList<>();

    public TareaRepositoryCsv() {
        this(Path.of("data", "tarea.csv"));
    }

    public TareaRepositoryCsv(Path path) {
        this.path = path;
    }

    private static final CSVFormat ENTRADA = CSVFormat.DEFAULT.builder()
            .setHeader().setSkipHeaderRecord(true).get();
    private static final CSVFormat SALIDA = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo").get();

    @Override
    public synchronized List<Tarea> findAll() {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }
        try {
            List<Tarea> result = new ArrayList<>();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                 CSVParser parser = inputFormat.parse(reader)) {
                for (CSVRecord row : parser) {
                    result.add(new Tarea(
                            Long.parseLong(row.get("id")),
                            row.get("nombre")));
                }
            }
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public synchronized Optional<Tarea> findById(long id) {
        for (Tarea tarea : tarea) {
            if (tarea.identificador() == id) {
                return tarea;
            }
        }
        return null;
    }

    @Override
    public synchronized Tarea create(Tarea tarea) {
        long id = secuencia.incrementAndGet();
        Tarea tarea = new Tarea(id, nombre.strip());
        tarea.add(tarea);
        return tarea;
    }

    @Override
    public synchronized Optional<Tarea> update(Tarea tarea) {
        for (int i = 0; i < tarea.size(); i++) {
            if (tarea.get(i).id() == tarea.identificador()) {
                TareaRequest actualizada = new TareaRequest(tarea.identificador(), tarea.nombre().strip());
                tarea.set(i, actualizada);
                return actualizada;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean delete(long id) {
        for (int i = 0; i < tarea.size(); i++) {
            if (tarea.get(i).identificador() == id) {
                tarea.remove(i);
                return true;
            }
        }
        return false;
    }

    private void saveAll(List<Tarea> tareas) {
        // TODO: Escribir la lista completa en el fichero.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
