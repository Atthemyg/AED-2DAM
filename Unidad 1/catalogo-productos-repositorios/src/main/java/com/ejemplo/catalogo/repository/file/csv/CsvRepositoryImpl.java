package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CsvRepositoryImpl implements IRepository {

    private Path path;

    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "nombre", "precio")
            .get();

    List<Producto> productos;

    CsvRepositoryImpl(Path path) {
        if (path == null) {
            throw new RuntimeException("El path es null");
        }
        this.path = path;
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        productos = load();
    }

    public List<Producto> load() {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord row : parser) {
                productos.add(new Producto(
                        Long.parseLong(row.get("id")),
                        row.get("nombre"),
                        Double.parseDouble(row.get("precio"))));
            }
        } catch (IOException e) {
            //Logger.ERROR //FINE
        }
        return productos;
    }

    @Override
    public List<Producto> findAll() {
        return productos;
    }

    @Override
    public Optional<Producto> findById(long id) {
        return productos.stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public void create(Producto producto) {

    }

    @Override
    public boolean update(Producto producto) {
        return false;
    }

    @Override
    public boolean delete(long id) {
        boolean removed = productos.removeIf(p -> p.id() == id);
        if (removed) saveAll(items);
        return removed;
    }
}
