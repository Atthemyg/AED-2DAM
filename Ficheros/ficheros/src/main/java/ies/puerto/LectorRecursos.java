package ies.puerto;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class LectorRecursos {
    public static void main(String[] args) {
        Path path = Path.of("src/main/resources", "archivo.txt");
        String ruta = path.toAbsolutePath().toString();
        System.out.println("Ruta del fichero: "+ruta);
        File file = new File(ruta);
        if (file.exists()) {
            System.out.println("El fichero existe");
        } else {
            System.out.println("El fichero no existe");
        }
        //URL url = LectorRecursos.class.getResource("archivo.txt");
        URL url = LectorRecursos.class.getClassLoader().getResource("archivo.txt");
        System.out.println("Path dentro de resources: "+url.getPath().toString());
    }
}