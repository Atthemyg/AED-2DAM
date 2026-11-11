
-- CINEFEST - CONSULTAS SQL

-- C01. Peliculas ordenadas alfabeticamente
SELECT titulo, genero, anio
FROM peliculas
ORDER BY titulo ASC;


-- C02. Espectadores activos por antiguedad
SELECT nombre, fecha_alta
FROM espectadores
WHERE activo = 1
ORDER BY fecha_alta ASC, nombre ASC;


-- C03. Peliculas de un genero (Drama)
SELECT titulo, pais, anio
FROM peliculas
WHERE genero = 'Drama'
ORDER BY titulo ASC;


-- C04. Sesiones entre dos fechas
SELECT fecha, hora, id_sesion, precio
FROM sesiones
WHERE fecha BETWEEN '2026-11-13' AND '2026-11-14'
ORDER BY fecha ASC, hora ASC, id_sesion ASC;


-- C05. Generos distintos
SELECT DISTINCT genero
FROM peliculas
ORDER BY genero ASC;


-- C06. Salas con su sede
SELECT se.nombre AS sede, sa.nombre AS sala, sa.aforo
FROM salas sa
         INNER JOIN sedes se ON sa.id_sede = se.id_sede
ORDER BY sede ASC, sala ASC;


-- C07. Sesiones con pelicula, sala y sede
SELECT s.id_sesion, p.titulo AS pelicula, se.nombre AS sede,
       sa.nombre AS sala, s.fecha, s.hora
FROM sesiones s
         INNER JOIN peliculas p ON s.id_pelicula = p.id_pelicula
         INNER JOIN salas sa ON s.id_sala = sa.id_sala
         INNER JOIN sedes se ON sa.id_sede = se.id_sede
ORDER BY s.id_sesion ASC;


-- C08. Entradas con espectador y pelicula
SELECT en.id_entrada, es.nombre AS espectador,
       p.titulo AS pelicula, en.estado
FROM entradas en
         INNER JOIN espectadores es ON en.id_espectador = es.id_espectador
         INNER JOIN sesiones s ON en.id_sesion = s.id_sesion
         INNER JOIN peliculas p ON s.id_pelicula = p.id_pelicula
ORDER BY en.id_entrada ASC;


-- C09. Espectadores que nunca compraron entradas
SELECT es.nombre, es.email
FROM espectadores es
WHERE NOT EXISTS (
    SELECT 1
    FROM entradas en
    WHERE en.id_espectador = es.id_espectador
)
ORDER BY es.nombre ASC;


-- C10. Valoraciones con pelicula y espectador
SELECT p.titulo AS pelicula, es.nombre AS espectador, v.puntuacion
FROM valoraciones v
         INNER JOIN peliculas p ON v.id_pelicula = p.id_pelicula
         INNER JOIN espectadores es ON v.id_espectador = es.id_espectador
ORDER BY pelicula ASC, espectador ASC;


-- C11. Numero de peliculas por genero
SELECT genero, COUNT(*) AS total_peliculas
FROM peliculas
GROUP BY genero
ORDER BY genero ASC;


-- C12. Numero de salas y aforo total por sede
SELECT se.nombre AS sede,
       COUNT(sa.id_sala) AS total_salas,
       COALESCE(SUM(sa.aforo), 0) AS aforo_total
FROM sedes se
         LEFT JOIN salas sa ON se.id_sede = sa.id_sede
GROUP BY se.id_sede, se.nombre
ORDER BY sede ASC;


-- C13. Ranking de espectadores por entradas validas
SELECT es.nombre AS espectador,
       COUNT(en.id_entrada) AS total_entradas
FROM espectadores es
         INNER JOIN entradas en ON es.id_espectador = en.id_espectador
WHERE en.estado IN ('PAGADA', 'USADA')
GROUP BY es.id_espectador, es.nombre
ORDER BY total_entradas DESC, espectador ASC;


-- C14. Peliculas con al menos dos valoraciones y su media
SELECT p.titulo AS pelicula,
       COUNT(v.id_valoracion) AS numero_valoraciones,
       ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
         INNER JOIN valoraciones v ON p.id_pelicula = v.id_pelicula
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(v.id_valoracion) >= 2
ORDER BY media DESC, pelicula ASC;


-- C15. Plazas vendidas y libres por sesion
SELECT s.id_sesion, sa.aforo,
       COUNT(en.id_entrada) AS vendidas,
       sa.aforo - COUNT(en.id_entrada) AS libres
FROM sesiones s
         INNER JOIN salas sa ON s.id_sala = sa.id_sala
         LEFT JOIN entradas en ON s.id_sesion = en.id_sesion
    AND en.estado IN ('PAGADA', 'USADA')
GROUP BY s.id_sesion, sa.aforo
ORDER BY s.id_sesion ASC;


-- C16. Peliculas sin sesiones
SELECT p.titulo
FROM peliculas p
WHERE NOT EXISTS (
    SELECT 1
    FROM sesiones s
    WHERE s.id_pelicula = p.id_pelicula
)
ORDER BY p.titulo ASC;


-- C17. Sesiones programadas sin ventas validas
SELECT s.id_sesion, p.titulo AS pelicula, s.fecha
FROM sesiones s
         INNER JOIN peliculas p ON s.id_pelicula = p.id_pelicula
WHERE s.estado = 'PROGRAMADA'
  AND NOT EXISTS (
    SELECT 1
    FROM entradas en
    WHERE en.id_sesion = s.id_sesion
      AND en.estado IN ('PAGADA', 'USADA')
)
ORDER BY s.id_sesion ASC;


-- C18. Sesiones agotadas
SELECT s.id_sesion, p.titulo AS pelicula, sa.aforo
FROM sesiones s
         INNER JOIN peliculas p ON s.id_pelicula = p.id_pelicula
         INNER JOIN salas sa ON s.id_sala = sa.id_sala
WHERE s.estado = 'PROGRAMADA'
  AND (
          SELECT COUNT(*)
          FROM entradas en
          WHERE en.id_sesion = s.id_sesion
            AND en.estado IN ('PAGADA', 'USADA')
      ) >= sa.aforo
ORDER BY s.id_sesion ASC;


-- C19. Peliculas con mas ventas validas que la media
SELECT p.titulo AS pelicula,
       COUNT(en.id_entrada) AS entradas_validas
FROM peliculas p
         LEFT JOIN sesiones s ON p.id_pelicula = s.id_pelicula
         LEFT JOIN entradas en ON s.id_sesion = en.id_sesion
    AND en.estado IN ('PAGADA', 'USADA')
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(en.id_entrada) > (
    SELECT AVG(total_entradas)
    FROM (
             SELECT p2.id_pelicula,
                    COUNT(en2.id_entrada) AS total_entradas
             FROM peliculas p2
                      LEFT JOIN sesiones s2 ON p2.id_pelicula = s2.id_pelicula
                      LEFT JOIN entradas en2 ON s2.id_sesion = en2.id_sesion
                 AND en2.estado IN ('PAGADA', 'USADA')
             GROUP BY p2.id_pelicula
         ) AS ventas_por_pelicula
)
ORDER BY entradas_validas DESC, pelicula ASC;


-- C20. Espectadores con entradas validas en al menos dos sedes
SELECT es.nombre AS espectador,
       COUNT(DISTINCT sa.id_sede) AS sedes_distintas
FROM espectadores es
         INNER JOIN entradas en ON es.id_espectador = en.id_espectador
         INNER JOIN sesiones s ON en.id_sesion = s.id_sesion
         INNER JOIN salas sa ON s.id_sala = sa.id_sala
WHERE en.estado IN ('PAGADA', 'USADA')
GROUP BY es.id_espectador, es.nombre
HAVING COUNT(DISTINCT sa.id_sede) >= 2
ORDER BY sedes_distintas DESC, espectador ASC;


-- C21. Top 3 de peliculas por recaudacion
SELECT p.titulo AS pelicula,
       COUNT(en.id_entrada) AS entradas_validas,
       ROUND(SUM(s.precio), 2) AS recaudacion
FROM peliculas p
         INNER JOIN sesiones s ON p.id_pelicula = s.id_pelicula
         INNER JOIN entradas en ON s.id_sesion = en.id_sesion
WHERE en.estado IN ('PAGADA', 'USADA')
GROUP BY p.id_pelicula, p.titulo
ORDER BY recaudacion DESC, pelicula ASC
    LIMIT 3;


-- C22. Recaudacion por sede, incluyendo sedes sin ventas
SELECT se.nombre AS sede,
       ROUND(COALESCE(SUM(
                              CASE
                                  WHEN en.estado IN ('PAGADA', 'USADA') THEN s.precio
                                  ELSE 0
                                  END
                      ), 0), 2) AS recaudacion
FROM sedes se
         LEFT JOIN salas sa ON se.id_sede = sa.id_sede
         LEFT JOIN sesiones s ON sa.id_sala = s.id_sala
         LEFT JOIN entradas en ON s.id_sesion = en.id_sesion
GROUP BY se.id_sede, se.nombre
ORDER BY recaudacion DESC, sede ASC;


-- C23. Ventas diarias sin entradas canceladas
SELECT en.fecha_compra,
       COUNT(en.id_entrada) AS entradas_validas,
       ROUND(SUM(s.precio), 2) AS total
FROM entradas en
         INNER JOIN sesiones s ON en.id_sesion = s.id_sesion
WHERE en.estado IN ('PAGADA', 'USADA')
GROUP BY en.fecha_compra
ORDER BY en.fecha_compra ASC;


-- C24. Historial de entradas de un espectador (id = 1)
SELECT p.titulo AS pelicula,
       s.fecha AS fecha_sesion,
       en.estado AS estado_entrada,
       s.precio
FROM entradas en
         INNER JOIN sesiones s ON en.id_sesion = s.id_sesion
         INNER JOIN peliculas p ON s.id_pelicula = p.id_pelicula
WHERE en.id_espectador = 1
ORDER BY fecha_sesion ASC, pelicula ASC;


-- C25. Informe por fecha de sesion, incluidos dias sin ventas
SELECT s.fecha,
       COUNT(DISTINCT CASE
                          WHEN s.estado = 'PROGRAMADA' THEN s.id_sesion
           END) AS sesiones_programadas,
       COUNT(en.id_entrada) AS entradas_validas,
       ROUND(COALESCE(SUM(
                              CASE
                                  WHEN en.id_entrada IS NOT NULL THEN s.precio
                                  ELSE 0
                                  END
                      ), 0), 2) AS recaudacion
FROM sesiones s
         LEFT JOIN entradas en ON s.id_sesion = en.id_sesion
    AND en.estado IN ('PAGADA', 'USADA')
GROUP BY s.fecha
ORDER BY s.fecha ASC;


-- C26. Media y cantidad de valoraciones por pelicula
SELECT p.titulo AS pelicula,
       COUNT(v.id_valoracion) AS numero_valoraciones,
       ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
         LEFT JOIN valoraciones v ON p.id_pelicula = v.id_pelicula
GROUP BY p.id_pelicula, p.titulo
ORDER BY pelicula ASC;


-- C27. Titulos, sesiones y ventas validas por genero
SELECT p.genero,
       COUNT(DISTINCT p.id_pelicula) AS titulos,
       COUNT(DISTINCT s.id_sesion) AS sesiones,
       COUNT(en.id_entrada) AS entradas_validas
FROM peliculas p
         LEFT JOIN sesiones s ON p.id_pelicula = s.id_pelicula
         LEFT JOIN entradas en ON s.id_sesion = en.id_sesion
    AND en.estado IN ('PAGADA', 'USADA')
GROUP BY p.genero
ORDER BY p.genero ASC;


-- C28. Salas sin sesiones programadas
SELECT se.nombre AS sede, sa.nombre AS sala
FROM salas sa
         INNER JOIN sedes se ON sa.id_sede = se.id_sede
WHERE NOT EXISTS (
    SELECT 1
    FROM sesiones s
    WHERE s.id_sala = sa.id_sala
      AND s.estado = 'PROGRAMADA'
)
ORDER BY sede ASC, sala ASC;


-- C29. Registro atomico de entrada con control de aforo
-- Ejecutar en una copia de la base de datos inicial.
-- Sesion = 1, espectador = 8, fecha_compra = 2026-11-10.

BEGIN IMMEDIATE;

INSERT INTO entradas (id_sesion, id_espectador, fecha_compra, estado)
SELECT 1, 8, '2026-11-10', 'PAGADA'
    WHERE EXISTS (
    SELECT 1
    FROM sesiones s
    INNER JOIN salas sa ON s.id_sala = sa.id_sala
    INNER JOIN espectadores es ON es.id_espectador = 8
    WHERE s.id_sesion = 1
    AND s.estado = 'PROGRAMADA'
    AND es.activo = 1
    AND NOT EXISTS (
        SELECT 1
        FROM entradas en
        WHERE en.id_sesion = 1
        AND en.id_espectador = 8
    )
    AND (
        SELECT COUNT(*)
        FROM entradas en
        WHERE en.id_sesion = 1
        AND en.estado IN ('PAGADA', 'USADA')
    ) < sa.aforo
);

SELECT changes() AS resultado;

COMMIT;


-- C30. Cancelacion de entrada y liberacion de plaza
-- Ejecutar en otra copia fresca de la base de datos inicial.
-- Entrada = 1.

BEGIN IMMEDIATE;

UPDATE entradas
SET estado = 'CANCELADA'
WHERE id_entrada = 1
  AND estado <> 'CANCELADA';

SELECT changes() AS resultado;

SELECT estado AS estado_final
FROM entradas
WHERE id_entrada = 1;

SELECT sa.aforo - (
    SELECT COUNT(*)
    FROM entradas en
    WHERE en.id_sesion = 1
      AND en.estado IN ('PAGADA', 'USADA')
) AS plazas_libres
FROM sesiones s
         INNER JOIN salas sa ON s.id_sala = sa.id_sala
WHERE s.id_sesion = 1;

COMMIT;
