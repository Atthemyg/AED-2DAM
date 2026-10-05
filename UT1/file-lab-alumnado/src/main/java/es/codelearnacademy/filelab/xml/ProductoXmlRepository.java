package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ProductoXmlRepository
        extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final Path path;
    private final XmlMapper mapper;

    public ProductoXmlRepository(Path path) {
        this(path, new XmlMapper());
    }

    public ProductoXmlRepository(Path path, XmlMapper mapper) {
        this.path = path;
        this.mapper = mapper;
    }

    @Override
    protected Long getId(Producto producto) {
        return producto.id();
    }

    @Override
    protected List<Producto> readAll() throws IOException {
        DocumentoProductos documento = mapper.readValue(
                path.toFile(), DocumentoProductos.class); // Lee este XML y conviértelo en un objeto DocumentoProductos
        return documento.getProductos(); // Devuelve la lista que está dentro de DocumentoProductos
    }

    @Override
    protected void writeAll(List<Producto> productos) throws IOException {
        DocumentoProductos documento = new DocumentoProductos(productos); // Construir un DocumentoProductos con la lista usando el constructor que ya tiene la clase
        mapper.writeValue(path.toFile(), documento); // writeValue() convierte el objeto DocumentoProductos a XML
    }
}
