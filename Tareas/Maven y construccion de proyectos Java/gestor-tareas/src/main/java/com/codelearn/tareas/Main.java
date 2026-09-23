package com.codelearn.tareas;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

    public class Main {
        public static void main(String[] args) throws IOException {
            var config = new Properties();
            try (var entrada = Main.class.getResourceAsStream("/aplicacion.properties")) {
                if (entrada == null) {
                    throw new IOException("Falta aplicacion.properties en el classpath");
                }
                config.load(entrada);
            }
            System.out.println(new Gson().toJson(Map.of("nombre", config.getProperty("nombre"))));
        }
    }

/*package com.codelearn.tareas;

import com.google.gson.Gson;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        var datos = Map.of("titulo", "Aprender Java", "completada", false);
        System.out.println(new Gson().toJson(datos));
        }
    }*/
