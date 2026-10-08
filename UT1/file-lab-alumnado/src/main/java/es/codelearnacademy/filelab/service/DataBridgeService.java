package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;

import java.nio.file.Path;
import java.util.List;

public class DataBridgeService {

    private final RepositoryFactory repositoryFactory;

    public DataBridgeService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    /**
     * Convierte los productos de un fichero a otro formato. Solo conoce IProductoRepository:
     * no sabe nada de CSV, JSON ni XML, de eso se encarga RepositoryFactory.
     * Si un producto ya existe en el destino se actualiza; el resto del destino no se toca.
     *
     * @return número de productos convertidos (0 si el origen está vacío o no se puede leer)
     */
    public int convert(FileFormat origenFormato, Path origen,
                       FileFormat destinoFormato, Path destino) {
        IProductoRepository repositorioOrigen = repositoryFactory.create(origenFormato, origen);
        List<Producto> productos = repositorioOrigen.findAll();

        IProductoRepository repositorioDestino = repositoryFactory.create(destinoFormato, destino);

        int convertidos = 0;
        for (Producto producto : productos) {
            if (repositorioDestino.create(producto) || repositorioDestino.update(producto)) {
                convertidos++;
            }
        }
        return convertidos;
    }
}