package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductoCsvRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;

    public ProductoCsvRepository(Path path) {
        this.path = path;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<Producto> productos = new ArrayList<>();

        for (int i = 1; i < lineas.size(); i++) { // Posición 0 es la cabecera -> id,nombre,precio,stock por eso empieza en 1
            String[] datos = lineas.get(i).split(",");

            long id = Long.parseLong(datos[0]); // Convierte el String "1" en Long 1
            String nombre = datos[1];
            double precio = Double.parseDouble(datos[2]);
            int stock = Integer.parseInt(datos[3]);

            Producto producto = new Producto(id, nombre, precio, stock);
            productos.add(producto);
        }
        return productos;
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8); // Abrimos el fichero en UTF-8

        CSVPrinter csvPrinter = new CSVPrinter( // CSVPrinter es la clase de Apache Commons CSV que se encarga de escribir correctamente el CSV
                writer,
                CSVFormat.DEFAULT.builder()
                        .setHeader("id", "nombre", "precio", "stock") // Primera linea -> id,nombre,precio,stock
                        .build()
        );
        for (Producto producto : productos) {
            csvPrinter.printRecord(  // new Producto(1, "Teclado", 25.99, 10) -> 1,Teclado,25.99,10
                    producto.id(),
                    producto.nombre(),
                    producto.precio(),
                    producto.stock()
            );
        }
        csvPrinter.close();
    }
}
