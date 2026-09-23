package ies.puerto;

import java.nio.file.Path;

public interface IFichero {
    /**
     * Funcion que verifica si existe un path
     * @param path Path de entrada
     * @return true/false si existe o no
     */
    public boolean existe(Path path);

}
