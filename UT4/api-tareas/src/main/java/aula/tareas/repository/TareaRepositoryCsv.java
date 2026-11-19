package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryCsv")
public class TareaRepositoryCsv implements ITareaRepository{
    private  Path path;
    private final AtomicLong secuencia = new AtomicLong();
    private final List<TareaRequest> tareaRequests = new ArrayList<>();

    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true).build();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo")
            .build();

    public TareaRepositoryCsv() {
        path = Path.of("data", "tarea.csv");
    }

    @Override
    public TareaRequest create(String titulo) {
        long id = secuencia.incrementAndGet();
        TareaRequest tareaRequest = new TareaRequest(id, titulo.strip());
        tareaRequests.add(tareaRequest);
        return tareaRequest;
    }

    @Override
    public boolean delete(long identificador) {
        for (int i = 0; i < tareaRequests.size(); i++) {
            if (tareaRequests.get(i).id() == identificador) {
                tareaRequests.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<TareaRequest> findAll() {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }
        try {
            List<TareaRequest> result = new ArrayList<>();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                 CSVParser parser = inputFormat.parse(reader)) {
                for (CSVRecord row : parser) {
                    result.add(new TareaRequest(
                            Long.parseLong(row.get("id")),
                            row.get("titulo")));
                }
            }
            return result;
        }catch (Exception e) {
            return List.of();
        }

    }

    @Override
    public TareaRequest findById(long identificador) {
        for (TareaRequest tareaRequest : tareaRequests) {
            if (tareaRequest.id() == identificador) {
                return tareaRequest;
            }
        }
        return null;
    }

    @Override
    public TareaRequest update(TareaRequest tareaRequest) {
        for (int i = 0; i < tareaRequests.size(); i++) {
            if (tareaRequests.get(i).id() == tareaRequest.id()) {
                TareaRequest actualizada = new TareaRequest(tareaRequest.id(), tareaRequest.titulo().strip());
                tareaRequests.set(i, actualizada);
                return actualizada;
            }
        }
        return null;
    }
}
