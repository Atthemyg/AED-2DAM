package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.config.PropertiesConfig;

import java.nio.file.Path;
import java.util.Optional;

public class ConfiguredDataBridge {

    private final PropertiesConfig config;
    private final DataBridgeService bridge;

    public ConfiguredDataBridge(PropertiesConfig config, DataBridgeService bridge) {
        this.config = config;
        this.bridge = bridge;
    }

    /**
     * Ejecuta la conversión descrita en el .properties (input.format, input.file, output.format, output.file).
     *
     * @return número de productos convertidos; 0 si falta algún dato de configuración
     *         o si algún formato o ruta no es válido
     */
    public int execute() {
        Optional<String> formatoEntrada = config.get("input.format");
        Optional<String> ficheroEntrada = config.get("input.file");
        Optional<String> formatoSalida = config.get("output.format");
        Optional<String> ficheroSalida = config.get("output.file");

        if (formatoEntrada.isEmpty() || ficheroEntrada.isEmpty()
                || formatoSalida.isEmpty() || ficheroSalida.isEmpty()) {
            return 0;
        }

        try {
            return bridge.convert(
                    FileFormat.from(formatoEntrada.get()),
                    Path.of(ficheroEntrada.get()),
                    FileFormat.from(formatoSalida.get()),
                    Path.of(ficheroSalida.get()));
        } catch (IllegalArgumentException e) { // Formato desconocido o ruta inválida (InvalidPathException)
            return 0;
        }
    }
}