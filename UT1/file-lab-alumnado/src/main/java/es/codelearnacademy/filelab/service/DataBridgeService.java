package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.repository.IProductoRepository;

import java.nio.file.Path;

public class DataBridgeService {

    private final RepositoryFactory repositoryFactory;

    public DataBridgeService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public int convert(FileFormat origenFormato, Path origen,
                       FileFormat destinoFormato, Path destino) {
        IProductoRepository productoRepository = RepositoryFactory.create(origenFormato, origen);
    }
}
