package es.codelearnacademy.filelab.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ProductoJsonRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;
    private final ObjectMapper mapper;

    public ProductoJsonRepository(Path path) {
        this(path, new ObjectMapper());
    }

    public ProductoJsonRepository(Path path, ObjectMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    // Como el JSON contiene directamente un array de productos, Jackson puede convertirlo directamente a una lista
    @Override
    protected List<Producto> readAll() throws IOException {
        return mapper.readValue(  // Lee el contenido del fichero y conviértelo a objetos Java
                path.toFile(), // Convierte el Path que tenemos en un File, que es una forma que ObjectMapper puede utilizar para leer el archivo
                mapper.getTypeFactory().constructCollectionType(List.class, Producto.class)); // Le indicamos a Jackson que queremos una lista cuyos elementos son objetos Producto
    }

    // ObjectMapper puede convertir directamente la lista completa de Producto en JSON y escribirla en el fichero
    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        mapper.writeValue(path.toFile(), productos); // Coge este objeto Java y conviértelo a JSON para guardarlo en un fichero
    }
}
