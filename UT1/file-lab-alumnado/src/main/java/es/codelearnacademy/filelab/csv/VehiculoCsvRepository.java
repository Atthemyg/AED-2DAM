package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VehiculoCsvRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;

    public VehiculoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<Vehiculo> vehiculos = new ArrayList<>();

        for (int i = 1; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(",");

            String matricula = datos[0];
            String marca = datos[1];
            String modelo = datos[2];
            int anio = Integer.parseInt(datos[3]);

            Vehiculo vehiculo = new Vehiculo(matricula, marca, modelo, anio);
            vehiculos.add(vehiculo);
        }
        return vehiculos;
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

        CSVPrinter csvPrinter = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder()
                        .setHeader("matricula", "marca", "modelo", "anio")
                        .build()
        );
        for (Vehiculo vehiculo : vehiculos) {
            csvPrinter.printRecord(
                    vehiculo.matricula(),
                    vehiculo.marca(),
                    vehiculo.modelo(),
                    vehiculo.anio()
            );
        }
        csvPrinter.close();
    }
}
