package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.model.ProductosDocument;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class XmlProductoRepository extends AbstractProductoRepository<Producto, Long> {

    // private final ObjectMapper mapper = new ObjectMapper();
    private final XmlMapper mapper;

    public XmlProductoRepository(Path path) {
        super(path);
        list = readAll();
        mapper = new XmlMapper();
    }

    @Override
    public void saveAll(List<Producto> productos) {
        Path temporal = null;

        try {
            ProductosDocument productosDocument = new ProductosDocument();
            productosDocument.setProductos(productos);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(getPath().toFile(), productosDocument);

            /*Path destino = getPath().toAbsolutePath();
            Path directorio = destino.getParent();
            Files.createDirectories(directorio);
            temporal = Files.createTempFile(directorio, "productos-", ".json.tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporal.toFile(), productos);
            try {
                Files.move(temporal, destino,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }*/

        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        } finally {
            /*if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ignored) {
                }
            }*/
        }
    }

    @Override
    public List<Producto> load() {
        try {
            ProductosDocument productosDocument = mapper.readValue(getPath().toFile(), ProductosDocument.class);
            list.clear();
            list.addAll(productosDocument.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return list;
    }
}
