package com.ejemplo.catalogo.repository.database;

import java.nio.file.Path;

public class DatabaseInitializer {
    String url;
    Path path;

    public DatabaseInitializer(String url) {
        if (url == null || url.isBlank()) {
            url = "data/app.db";
        }
        path = Path.of(url);
    }
}
