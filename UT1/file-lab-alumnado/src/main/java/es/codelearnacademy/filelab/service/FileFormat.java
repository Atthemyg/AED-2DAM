package es.codelearnacademy.filelab.service;

public enum FileFormat {
    CSV,
    JSON,
    XML;

    public static FileFormat from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El formato del archivo no puede ser null");
        }
        try {
            return FileFormat.valueOf(value.toUpperCase().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de archivo invalido");
        }
    }
}
