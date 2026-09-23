package ies.puerto;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FicheroImpl implements IFichero {

    @Override
    public boolean existe(Path path) {
        if (path == null) {
            return false;
        }
        File file = new File(path.toFile().getAbsolutePath());
        /*if (Files.exists(path)) { //hace lo mismo
            return true;
        }*/
        if (file.exists()) {
            return true;
        }
        return false;
    }
}