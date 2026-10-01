package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.model.ProductosXml;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class XmlRepository extends AbstractProductoRepository {

    // private final ObjectMapper mapper = new ObjectMapper();
    private final XmlMapper mapper;

    public XmlRepository(Path path) {
        super(path);
        productos = load();
        mapper = new XmlMapper();
    }

    @Override
    public void saveAll(List<Producto> productos) {
        Path temporal = null;

        try {
            ProductosXml productosXml = new ProductosXml();
            productosXml.setProductos(productos);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(getPath().toFile(),productosXml);

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
            ProductosXml productosXml = mapper.readValue(getPath().toFile(), ProductosXml.class);
            productos.clear();
            productos.addAll(productosXml.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return productos;
    }
}
