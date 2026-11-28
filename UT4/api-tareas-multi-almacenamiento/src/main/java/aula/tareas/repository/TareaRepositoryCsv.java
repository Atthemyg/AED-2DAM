package aula.tareas.repository;

import aula.tareas.entity.TareaEntityCsv;
import aula.tareas.mapper.TareaMapperCsv;
import aula.tareas.modelo.Tarea;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryCsv")
public class TareaRepositoryCsv implements ITareaRepository {

    private final Path path;

    private static final CSVFormat ENTRADA = CSVFormat.DEFAULT.builder()
            .setHeader().setSkipHeaderRecord(true).get();
    private static final CSVFormat SALIDA = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo").get();

    public TareaRepositoryCsv() {
        this(Path.of("data", "tarea.csv"));
    }

    public TareaRepositoryCsv(Path path) {
        this.path = path;
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
        if (Files.notExists(path)) {
            return result;
        }
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser parser = ENTRADA.parse(reader)) {
            for (CSVRecord row : parser) {
                TareaEntityCsv entity = new TareaEntityCsv(Long.parseLong(row.get("id")), row.get("titulo"));
                result.add(TareaMapperCsv.toDomain(entity));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
        try (Writer writer = Files.newBufferedWriter(path);
             CSVPrinter printer = new CSVPrinter(writer, SALIDA)) {
            for (Tarea tarea : tareas) {
                TareaEntityCsv entity = TareaMapperCsv.toEntity(tarea);
                printer.printRecord(entity.getIdTarea(), entity.getNombreTarea());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}