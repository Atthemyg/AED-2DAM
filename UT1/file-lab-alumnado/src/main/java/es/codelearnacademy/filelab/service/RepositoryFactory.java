package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.csv.ProductoCsvRepository;
import es.codelearnacademy.filelab.json.ProductoJsonRepository;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import es.codelearnacademy.filelab.xml.ProductoXmlRepository;

import java.nio.file.Path;

public class RepositoryFactory {

    public IProductoRepository create(FileFormat format, Path path) {
        switch (format) {
            case CSV:
                return new ProductoCsvRepository(path);
            case JSON:
                return new ProductoJsonRepository(path);
            case XML:
                return new ProductoXmlRepository(path);
            default:
                throw new IllegalArgumentException("Formato no valido");
        }
    }
}
