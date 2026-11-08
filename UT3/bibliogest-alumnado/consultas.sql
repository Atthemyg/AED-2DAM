-- =====================================================================
-- BIBLIOGEST - consultas.sql
-- Las 25 consultas del enunciado, identificadas del 01 al 25.
-- Cada una muestra el SQL equivalente a la consulta JPQL (o a la operación)
-- que implementa el método indicado de los DAO. Los parámetros de los
-- métodos Java se sustituyen aquí por valores de ejemplo, indicados en
-- el comentario "Parámetros".
-- Las fechas son texto en formato YYYY-MM-DD, por lo que la comparación
-- alfabética coincide con la cronológica.
-- Un préstamo está ACTIVO cuando fecha_devolucion IS NULL y VENCIDO cuando
-- además fecha_limite es anterior a la fecha de referencia.
-- =====================================================================

-- ---------------------------------------------------------------------
-- NIVEL 1 - Selección, filtros y ordenación
-- ---------------------------------------------------------------------

-- 01. Libros ordenados por título.            (LibroDao.listar)
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
ORDER BY titulo, id_libro;

-- 02. Socios activos, de la fecha de alta más reciente a la más antigua.   (SocioDao.listarActivos)
SELECT id_socio, nombre, email, telefono, fecha_alta, activo
FROM socios
WHERE activo = 1
ORDER BY fecha_alta DESC, id_socio;

-- 03. Libros cuyo título contiene un texto.   (LibroDao.buscarPorTitulo)
-- Parámetros: texto = 'Libro 0'
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
WHERE LOWER(titulo) LIKE LOWER('%' || 'Libro 0' || '%')
ORDER BY titulo, id_libro;

-- 04. Libros publicados desde un año y de un género.   (LibroDao.librosDesdeAnio)
-- Parámetros: anio = 1995, genero = 'Novela'
SELECT id_libro, titulo, isbn, genero, anio_publicacion, ejemplares, id_autor
FROM libros
WHERE anio_publicacion >= 1995 AND genero = 'Novela'
ORDER BY anio_publicacion, titulo;

-- 05. Géneros distintos del catálogo.   (LibroDao.generos)
SELECT DISTINCT genero
FROM libros
ORDER BY genero;

-- ---------------------------------------------------------------------
-- NIVEL 2 - Relaciones entre tablas
-- ---------------------------------------------------------------------

-- 06. Libros con nombre y nacionalidad del autor.   (LibroDao.librosConAutor)
SELECT l.id_libro, l.titulo, a.nombre AS autor, a.nacionalidad
FROM libros l
JOIN autores a ON a.id_autor = l.id_autor
ORDER BY l.titulo, l.id_libro;

-- 07. Préstamos con socio, libro y fechas.   (PrestamoDao.prestamosConSocioYLibro)
SELECT p.id_prestamo, s.nombre AS socio, l.titulo AS libro,
       p.fecha_prestamo, p.fecha_limite, p.fecha_devolucion
FROM prestamos p
JOIN socios s ON s.id_socio = p.id_socio
JOIN libros l ON l.id_libro = p.id_libro
ORDER BY p.fecha_prestamo, p.id_prestamo;

-- 08. Préstamos activos de un socio.   (PrestamoDao.listarPorSocio filtrado por fecha_devolucion IS NULL)
-- Parámetros: idSocio = 1
SELECT p.id_prestamo, p.id_libro, p.fecha_prestamo, p.fecha_limite
FROM prestamos p
WHERE p.id_socio = 1 AND p.fecha_devolucion IS NULL
ORDER BY p.fecha_prestamo DESC, p.id_prestamo DESC;

-- 09. Socios que nunca han realizado préstamos.   (SocioDao.sociosSinPrestamos)
SELECT s.id_socio, s.nombre, s.email, s.telefono, s.fecha_alta, s.activo
FROM socios s
WHERE NOT EXISTS (SELECT 1 FROM prestamos p WHERE p.id_socio = s.id_socio)
ORDER BY s.id_socio;

-- 10. Reservas pendientes con socio, título y autor.   (ReservaDao.reservasConDetalles)
SELECT r.id_reserva, s.nombre AS socio, l.titulo, a.nombre AS autor, r.fecha_reserva
FROM reservas r
JOIN socios  s ON s.id_socio = r.id_socio
JOIN libros  l ON l.id_libro = r.id_libro
JOIN autores a ON a.id_autor = l.id_autor
WHERE r.estado = 'PENDIENTE'
ORDER BY r.fecha_reserva, r.id_reserva;

-- ---------------------------------------------------------------------
-- NIVEL 3 - Agregaciones y estadísticas
-- ---------------------------------------------------------------------

-- 11. Cantidad de libros por género.   (LibroDao.contarPorGenero)
SELECT genero, COUNT(*) AS total_libros
FROM libros
GROUP BY genero
ORDER BY genero;

-- 12. Cantidad de libros por autor, incluidos autores sin libros.   (AutorDao.autoresConRecuento)
-- LEFT JOIN conserva los autores sin libros; COUNT(l.id_libro) no cuenta los NULL.
SELECT a.id_autor, a.nombre, COUNT(l.id_libro) AS total_libros
FROM autores a
LEFT JOIN libros l ON l.id_autor = a.id_autor
GROUP BY a.id_autor, a.nombre
ORDER BY a.nombre, a.id_autor;

-- 13. Cinco socios con más préstamos.   (SocioDao.rankingSocios)
-- Parámetros: limite = 5
SELECT s.id_socio, s.nombre, COUNT(p.id_prestamo) AS total_prestamos
FROM socios s
JOIN prestamos p ON p.id_socio = s.id_socio
GROUP BY s.id_socio, s.nombre
ORDER BY total_prestamos DESC, s.id_socio
LIMIT 5;

-- 14. Géneros con más de cinco préstamos históricos.   (LibroDao.prestamosPorGeneroMinimo)
-- Parámetros: minimo = 5
SELECT l.genero, COUNT(p.id_prestamo) AS total_prestamos
FROM prestamos p
JOIN libros l ON l.id_libro = p.id_libro
GROUP BY l.genero
HAVING COUNT(p.id_prestamo) > 5
ORDER BY total_prestamos DESC, l.genero;

-- 15. Por libro: ejemplares totales, prestados actualmente y disponibles.   (LibroDao.disponibilidadPorLibro)
-- La condición de préstamo activo va en el ON del LEFT JOIN para no perder libros sin préstamos.
SELECT l.id_libro, l.titulo, l.ejemplares,
       COUNT(p.id_prestamo)                    AS prestados,
       l.ejemplares - COUNT(p.id_prestamo)     AS disponibles
FROM libros l
LEFT JOIN prestamos p ON p.id_libro = l.id_libro AND p.fecha_devolucion IS NULL
GROUP BY l.id_libro, l.titulo, l.ejemplares
ORDER BY l.titulo, l.id_libro;

-- ---------------------------------------------------------------------
-- NIVEL 4 - Subconsultas y casos especiales
-- ---------------------------------------------------------------------

-- 16. Libros que nunca se han prestado.   (LibroDao.nuncaPrestados)
SELECT l.id_libro, l.titulo, l.isbn, l.genero, l.anio_publicacion, l.ejemplares, l.id_autor
FROM libros l
WHERE NOT EXISTS (SELECT 1 FROM prestamos p WHERE p.id_libro = l.id_libro)
ORDER BY l.titulo, l.id_libro;

-- 17. Socios con préstamos vencidos en una fecha de referencia.   (SocioDao.listarConPrestamosVencidos)
-- Parámetros: fecha = '2025-06-01'
SELECT s.id_socio, s.nombre, s.email, s.telefono, s.fecha_alta, s.activo
FROM socios s
WHERE EXISTS (SELECT 1
              FROM prestamos p
              WHERE p.id_socio = s.id_socio
                AND p.fecha_devolucion IS NULL
                AND p.fecha_limite < '2025-06-01')
ORDER BY s.id_socio;

-- 18. Libros con más préstamos históricos que la media del catálogo.   (LibroDao.masPrestadosQueMedia)
-- La media se calcula sobre TODOS los libros: el LEFT JOIN incluye a los que tienen 0 préstamos.
-- El DAO usa la forma equivalente sin decimales: préstamos_libro * nº_libros > total_préstamos.
SELECT l.id_libro, l.titulo, l.isbn, l.genero, l.anio_publicacion, l.ejemplares, l.id_autor
FROM libros l
WHERE (SELECT COUNT(*) FROM prestamos p WHERE p.id_libro = l.id_libro) >
      (SELECT AVG(n) FROM (SELECT COUNT(p2.id_prestamo) AS n
                           FROM libros l2
                           LEFT JOIN prestamos p2 ON p2.id_libro = l2.id_libro
                           GROUP BY l2.id_libro))
ORDER BY l.titulo, l.id_libro;

-- 19. Autores cuyos libros han sido prestados por al menos tres socios distintos.   (AutorDao.autoresLeidosPorTresSocios)
SELECT a.id_autor, a.nombre, a.nacionalidad, a.anio_nacimiento
FROM autores a
WHERE a.id_autor IN (SELECT l.id_autor
                     FROM libros l
                     JOIN prestamos p ON p.id_libro = l.id_libro
                     GROUP BY l.id_autor
                     HAVING COUNT(DISTINCT p.id_socio) >= 3)
ORDER BY a.id_autor;

-- 20. Libros con reservas pendientes que no tienen ejemplares disponibles.   (LibroDao.reservadosSinDisponibilidad)
SELECT l.id_libro, l.titulo, l.isbn, l.genero, l.anio_publicacion, l.ejemplares, l.id_autor
FROM libros l
WHERE EXISTS (SELECT 1 FROM reservas r WHERE r.id_libro = l.id_libro AND r.estado = 'PENDIENTE')
  AND l.ejemplares <= (SELECT COUNT(*) FROM prestamos p
                       WHERE p.id_libro = l.id_libro AND p.fecha_devolucion IS NULL)
ORDER BY l.titulo, l.id_libro;

-- ---------------------------------------------------------------------
-- NIVEL 5 - Informes y transacciones
-- ---------------------------------------------------------------------

-- 21. Ranking de los tres libros más prestados de un mes, con autor.   (PrestamoDao.rankingMensual)
-- Parámetros: anioMes = '2025-02', limite = 3
SELECT l.id_libro, l.titulo, a.nombre AS autor, COUNT(p.id_prestamo) AS total_prestamos
FROM prestamos p
JOIN libros  l ON l.id_libro = p.id_libro
JOIN autores a ON a.id_autor = l.id_autor
WHERE p.fecha_prestamo LIKE '2025-02' || '%'
GROUP BY l.id_libro, l.titulo, a.nombre
ORDER BY total_prestamos DESC, l.titulo, l.id_libro
LIMIT 3;

-- 22. Historial completo de préstamos de un socio, con devoluciones y retrasos.   (SocioDao.historialSocio)
-- Parámetros: idSocio = 1; hoy = fecha actual (el DAO la obtiene con LocalDate.now())
SELECT p.id_prestamo, l.titulo, p.fecha_prestamo, p.fecha_limite, p.fecha_devolucion,
       CASE
           WHEN p.fecha_devolucion IS NULL THEN
               CASE WHEN p.fecha_limite < DATE('now') THEN 'ACTIVO CON RETRASO' ELSE 'ACTIVO' END
           WHEN p.fecha_devolucion > p.fecha_limite THEN 'DEVUELTO CON RETRASO'
           ELSE 'DEVUELTO'
       END AS estado
FROM prestamos p
JOIN libros l ON l.id_libro = p.id_libro
WHERE p.id_socio = 1
ORDER BY p.fecha_prestamo, p.id_prestamo;

-- 23. Reserva pendiente más antigua de cada libro.   (ReservaDao.prioridadReservas)
-- Se toma la menor fecha y, si hay empate, el menor id_reserva, para devolver una sola fila por libro.
SELECT l.id_libro, l.titulo, r.id_reserva, s.nombre AS socio, r.fecha_reserva
FROM reservas r
JOIN libros l ON l.id_libro = r.id_libro
JOIN socios s ON s.id_socio = r.id_socio
WHERE r.estado = 'PENDIENTE'
  AND r.id_reserva = (SELECT MIN(r2.id_reserva)
                      FROM reservas r2
                      WHERE r2.id_libro = r.id_libro
                        AND r2.estado = 'PENDIENTE'
                        AND r2.fecha_reserva = (SELECT MIN(r3.fecha_reserva)
                                                FROM reservas r3
                                                WHERE r3.id_libro = r.id_libro
                                                  AND r3.estado = 'PENDIENTE'))
ORDER BY l.titulo, l.id_libro;

-- 24. Estadísticas por género: títulos, ejemplares, préstamos históricos y activos.   (LibroDao.estadisticasGenero)
-- Los préstamos se cuentan en subconsultas para que el JOIN no multiplique títulos ni ejemplares.
SELECT l.genero,
       COUNT(*)           AS titulos,
       SUM(l.ejemplares)  AS ejemplares,
       (SELECT COUNT(*) FROM prestamos p JOIN libros x ON x.id_libro = p.id_libro
         WHERE x.genero = l.genero)                                   AS prestamos_historicos,
       (SELECT COUNT(*) FROM prestamos q JOIN libros y ON y.id_libro = q.id_libro
         WHERE y.genero = l.genero AND q.fecha_devolucion IS NULL)    AS prestamos_activos
FROM libros l
GROUP BY l.genero
ORDER BY l.genero;

-- 25. Registro atómico de un préstamo.   (PrestamoDao.registrarPrestamo)
-- Parámetros: idSocio = 1, idLibro = 31, inicio = '2025-01-01', limite = '2025-02-01'
-- Reglas: el socio debe existir y estar activo, el libro debe existir y debe quedar
--         algún ejemplar libre (ejemplares > préstamos activos del libro).
--
-- Pasos SQL:
--   1. BEGIN IMMEDIATE  -> abre la transacción y adquiere ya el bloqueo de escritura de SQLite.
--      Así dos registros simultáneos se ejecutan uno detrás de otro y el segundo ve
--      el préstamo que acaba de confirmar el primero (no puede haber lectura "vieja").
--   2. INSERT ... SELECT -> comprueba socio activo, libro existente y ejemplar libre y
--      crea el préstamo en UNA sola sentencia: si alguna condición falla no inserta nada.
--   3. Si changes() = 1 se confirma con COMMIT; si es 0 (o hay cualquier error,
--      por ejemplo una clave foránea inexistente) se anula con ROLLBACK y el método devuelve 0.
--
-- Límite transaccional: todo ocurre entre BEGIN y COMMIT/ROLLBACK. En Java lo hace
-- PrestamoDao.escribir() con tx.begin()/tx.commit()/tx.rollback(), y además un
-- cerrojo (synchronized) en el DAO serializa las llamadas dentro de la misma aplicación.
BEGIN IMMEDIATE;

INSERT INTO prestamos (id_socio, id_libro, fecha_prestamo, fecha_limite)
SELECT s.id_socio, l.id_libro, '2025-01-01', '2025-02-01'
FROM socios s
JOIN libros l ON l.id_libro = 31
WHERE s.id_socio = 1
  AND s.activo = 1
  AND l.ejemplares > (SELECT COUNT(*) FROM prestamos p
                      WHERE p.id_libro = l.id_libro AND p.fecha_devolucion IS NULL);

-- Comprobación: 1 = préstamo registrado, 0 = rechazado (entonces ejecutar ROLLBACK en lugar de COMMIT).
SELECT changes() AS filas_insertadas;

COMMIT;
