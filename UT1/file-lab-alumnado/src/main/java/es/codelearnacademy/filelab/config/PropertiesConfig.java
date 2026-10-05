package es.codelearnacademy.filelab.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class PropertiesConfig {

    private final Path path;

    public PropertiesConfig(Path path) {
        this.path = path;
    }

    public Optional<String> get(String key) {
        Properties properties = new Properties();
         try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
             properties.load(fileInputStream);
             String valor = properties.getProperty(key);
             return Optional.of(valor);
         } catch (Exception e) {
             return Optional.empty();
         }
    }

    public String getOrDefault(String key, String defaultValue) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            properties.load(fileInputStream);
            return properties.getProperty(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Map<String, String> findAll() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            properties.load(fileInputStream);
            Map<String, String> map = new HashMap<>();

            for (String stringPropertyNames : properties.stringPropertyNames()) {
                map.put(stringPropertyNames, properties.getProperty(stringPropertyNames));
            }
            return Map.copyOf(map);
        } catch (IOException e) {
            return Map.of();
        }
    }

    public boolean put(String key, String value) {
        Properties properties = new Properties();

        if (Files.exists(path)) {
            try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                properties.load(fileInputStream);
            } catch (Exception e) {
                return false;
            }
        }
            properties.setProperty(key, value);

            try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
                properties.store(fileOutputStream, "Configuracion actualizada");
                return true;
            } catch (Exception e) {
                return false;
            }
    }

    public boolean remove(String key) {
        Properties properties = new Properties();

        if (Files.notExists(path)) {
            return true;
        }
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            return false;
        }
        properties.remove(key);

        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            properties.store(fileOutputStream, "Configuracion actualizada");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
