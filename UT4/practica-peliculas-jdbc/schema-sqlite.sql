CREATE TABLE IF NOT EXISTS peliculas (
    pelicula_codigo TEXT NOT NULL PRIMARY KEY
        CHECK (pelicula_codigo GLOB 'PEL[0-9][0-9][0-9]'),
    titulo_texto TEXT NOT NULL
        CHECK (length(trim(titulo_texto)) BETWEEN 1 AND 120),
    estreno_anio INTEGER NOT NULL
        CHECK (typeof(estreno_anio) = 'integer' AND estreno_anio BETWEEN 1888 AND 2100),
    duracion_minutos INTEGER NOT NULL
        CHECK (typeof(duracion_minutos) = 'integer' AND duracion_minutos BETWEEN 1 AND 1000)
);
