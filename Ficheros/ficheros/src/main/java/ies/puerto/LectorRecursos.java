package ies.puerto;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LectorRecursos extends FicheroImpl {
    public static void main(String[] args) {
        Path path = Path.of("src/main/resources", "csv.txt");
        String ruta = path.toAbsolutePath().toString();
        System.out.println("Ruta del fichero: "+ruta);
        File file = new File(ruta);

        if (file.exists()) {
            System.out.println("El fichero existe");
        } else {
            System.out.println("El fichero no existe");
        }

        /*if (existe(path)) {
            System.out.println("El fichero existe");
        } else {
            System.out.println("El fichero no existe");
        }*/

        /*URL url = LectorRecursos.class.getResource("archivo.txt");
        URL url = LectorRecursos.class.getClassLoader().getResource("archivo.txt");
        System.out.println("Path dentro de resources: "+url.getPath().toString());*/

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            //System.out.println(reader.readLine());
            String linea = reader.readLine();
            String[] valores = linea.split(",");
            System.out.println(valores[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Reader in = null;
        try {
            in = new FileReader(path.toAbsolutePath().toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();
        Iterable<CSVRecord> records = csvFormat.parse(in);
    }
}