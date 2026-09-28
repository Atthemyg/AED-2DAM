package com.docencia.ficheros;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvServiceImpl implements CsvService {

    @Override
    public List<String> leerLineasCsv(Path ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException();
        }
        if (!Files.exists(ruta)) {
            throw new IllegalArgumentException();
        }

        List<String> lineas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta.toFile()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lineas;
    }

    @Override
    public List<String[]> leerRegistrosCsv(Path ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException();
        }
        if (!Files.exists(ruta)) {
            throw new IllegalArgumentException();
        }

        List<String[]> registros = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta.toString()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                registros.add(linea.split(","));
            }
            return registros;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void escribirLineasCsv(Path ruta, List<String> lineas) {
        if (ruta == null || lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta.toFile()))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer contarRegistrosCsv(Path ruta) {
        return leerRegistrosCsv(ruta).size();
    }

    @Override
    public List<String[]> filtrarRegistrosPorValor(Path ruta, Integer columna, String valor) {
        if (ruta == null || columna == null || valor == null || columna < 0) {
            throw new IllegalArgumentException();
        }
        if (!Files.exists(ruta)) {
            throw new IllegalArgumentException();
        }

        List<String[]> registros = leerRegistrosCsv(ruta);

        for (String[] registro : registros) {
            if (columna >= registro.length) {
                throw new IllegalArgumentException("Columna invalida");
            }
        }

        List<String[]> filtrados = new ArrayList<>();
        for (String[] registro : registros) {
            if (registro[columna].equals(valor)) {
                filtrados.add(registro);
            }
        }

        return filtrados;
    }

    @Override
    public Map<String, Integer> contarFrecuenciaColumna(Path ruta, Integer columna) {
        if (ruta == null || columna == null || columna < 0) {
            throw new IllegalArgumentException();
        }
        if (!Files.exists(ruta)) {
            throw new IllegalArgumentException();
        }

        Map<String, Integer> frecuencia = new LinkedHashMap<>();

        List<String[]> registros = leerRegistrosCsv(ruta);

        for (String[] registro : registros) {
            if (registro.length > columna) {
                String valor = registro[columna];
                frecuencia.put(valor, frecuencia.getOrDefault(valor, 0) + 1);
            }
        }

        return frecuencia;
    }
    
}
