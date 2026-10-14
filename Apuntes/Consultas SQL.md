Consultas en SQLite
1. Introducción
SQLite es un sistema de gestión de bases de datos relacional que permite almacenar, organizar y consultar información. Se caracteriza por ser ligero, rápido y no necesitar un servidor independiente para funcionar.
Las consultas SQL (Structured Query Language) permiten interactuar con las bases de datos para obtener información, insertar registros, modificar datos y eliminar información.
2. ¿Qué es una consulta SQL?
Una consulta SQL es una instrucción que se utiliza para realizar operaciones sobre una base de datos. Las más habituales son:
    • SELECT: consultar datos.
    • INSERT INTO: insertar registros.
    • UPDATE: modificar registros.
    • DELETE: eliminar registros.
    • CREATE TABLE: crear tablas.
    • ALTER TABLE: modificar la estructura de una tabla.
    • DROP TABLE: eliminar una tabla.
3. Crear una tabla
La instrucción CREATE TABLE permite crear una tabla en la que almacenar datos.
CREATE TABLE alumnos (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    edad INTEGER,
    nota REAL
);
    • id: identificador único.
    • nombre: nombre del alumno.
    • edad: edad del alumno.
    • nota: calificación del alumno.
4. Insertar datos con INSERT INTO
La instrucción INSERT INTO permite añadir nuevos registros a una tabla.
INSERT INTO alumnos (nombre, edad, nota)
VALUES ('Ana', 20, 8.5);

INSERT INTO alumnos (nombre, edad, nota)
VALUES ('Luis', 19, 6.0);
Cada instrucción añade un registro a la tabla.
5. Consultar datos con SELECT
La instrucción SELECT permite recuperar información de una tabla.
Mostrar todos los datos:
SELECT * FROM alumnos;
Mostrar columnas específicas:
SELECT nombre, nota
FROM alumnos;
Utilizar alias para cambiar el nombre de las columnas en el resultado:
SELECT nombre AS alumno, nota AS calificacion
FROM alumnos;
6. Eliminar duplicados con DISTINCT
La cláusula DISTINCT permite obtener valores únicos, evitando que se repitan en los resultados.
SELECT DISTINCT edad
FROM alumnos;
Esta consulta muestra las edades diferentes de los alumnos sin repetirlas.
También puede combinarse con COUNT() para contar los valores únicos:
SELECT COUNT(DISTINCT edad)
FROM alumnos;
7. Filtrar resultados con WHERE
La cláusula WHERE permite seleccionar los registros que cumplen una condición.
Mostrar alumnos mayores de 19 años:
SELECT *
FROM alumnos
WHERE edad > 19;
Mostrar alumnos aprobados:
SELECT nombre, nota
FROM alumnos
WHERE nota >= 5;
Los operadores de comparación más habituales son:
Operador	Significado
=	Igual a
!= o <>	Distinto de
>	Mayor que
<	Menor que
>=	Mayor o igual que
<=	Menor o igual que
8. Operadores lógicos
Los operadores lógicos permiten combinar condiciones.
AND: deben cumplirse todas las condiciones.
SELECT *
FROM alumnos
WHERE edad >= 20 AND nota >= 8;
OR: debe cumplirse al menos una condición.
SELECT *
FROM alumnos
WHERE edad = 18 OR edad = 19;
NOT: niega una condición.
SELECT *
FROM alumnos
WHERE NOT edad = 18;
9. Ordenar resultados con ORDER BY
La cláusula ORDER BY permite ordenar los resultados.
Orden ascendente:
SELECT nombre, nota
FROM alumnos
ORDER BY nota ASC;
Orden descendente:
SELECT nombre, nota
FROM alumnos
ORDER BY nota DESC;
    • ASC: orden ascendente.
    • DESC: orden descendente.
10. Limitar resultados con LIMIT
La cláusula LIMIT establece el número máximo de registros que se mostrarán.
SELECT nombre, nota
FROM alumnos
ORDER BY nota DESC
LIMIT 3;
Esta consulta muestra los tres alumnos con las notas más altas.
También puede utilizarse OFFSET para saltar registros:
SELECT *
FROM alumnos
LIMIT 2 OFFSET 1;
11. Buscar patrones con LIKE
El operador LIKE permite buscar textos que coincidan con un patrón.
    • %: representa cero o más caracteres.
    • _: representa exactamente un carácter.
Buscar nombres que comiencen por M:
SELECT *
FROM alumnos
WHERE nombre LIKE 'M%';
Buscar nombres que terminen en a:
SELECT *
FROM alumnos
WHERE nombre LIKE '%a';
12. Operadores IN, BETWEEN e IS NULL
IN: comprueba si un valor pertenece a una lista.
SELECT *
FROM alumnos
WHERE edad IN (18, 20, 21);
BETWEEN: busca valores dentro de un intervalo, incluidos sus límites.
SELECT *
FROM alumnos
WHERE nota BETWEEN 6 AND 9;
IS NULL: busca registros cuyo campo no contiene un valor conocido.
SELECT *
FROM alumnos
WHERE nota IS NULL;
Para buscar campos que sí tienen valor, se utiliza IS NOT NULL.
13. Funciones de agregación
Las funciones de agregación permiten realizar cálculos sobre varios registros.
Función	Utilidad
COUNT()	Cuenta registros o valores.
SUM()	Suma valores numéricos.
AVG()	Calcula la media.
MAX()	Obtiene el valor máximo.
MIN()	Obtiene el valor mínimo.
Ejemplos:
SELECT COUNT(*) FROM alumnos;
SELECT AVG(nota) FROM alumnos;
SELECT MAX(nota) FROM alumnos;
14. Agrupar resultados con GROUP BY y HAVING
GROUP BY permite agrupar registros que comparten un mismo valor.
SELECT edad, COUNT(*) AS total
FROM alumnos
GROUP BY edad;
Esta consulta cuenta cuántos alumnos hay de cada edad.
HAVING permite filtrar los grupos según una condición.
SELECT edad, COUNT(*) AS total
FROM alumnos
GROUP BY edad
HAVING COUNT(*) > 1;
Esta consulta muestra las edades que aparecen en más de un registro.
15. Modificar datos con UPDATE
La instrucción UPDATE permite modificar los datos existentes.
UPDATE alumnos
SET nota = 9.0
WHERE nombre = 'Ana';
Esta consulta cambia la nota de Ana a 9.0.
Es importante utilizar WHERE, ya que, si se omite, se modificarán todos los registros de la tabla.
16. Eliminar datos con DELETE
La instrucción DELETE permite eliminar registros.
DELETE FROM alumnos
WHERE nombre = 'Luis';
Esta consulta elimina los registros que cumplen la condición. Si se omite WHERE, se eliminarán todos los registros, pero la tabla seguirá existiendo.
17. Consultas con varias tablas: JOIN
La cláusula JOIN permite combinar información de diferentes tablas relacionadas.
SELECT alumnos.nombre, cursos.nombre AS curso
FROM alumnos
INNER JOIN cursos
ON alumnos.curso_id = cursos.id;
    • INNER JOIN: devuelve los registros que tienen coincidencias en ambas tablas.
    • LEFT JOIN: devuelve todos los registros de la tabla izquierda y las coincidencias de la derecha.
    • CROSS JOIN: combina cada registro de una tabla con cada registro de la otra.
18. Subconsultas
Una subconsulta es una consulta SQL incluida dentro de otra consulta.
Por ejemplo, mostrar los alumnos cuya nota es superior a la media:
SELECT nombre, nota
FROM alumnos
WHERE nota > (
    SELECT AVG(nota)
    FROM alumnos
);
La consulta interna calcula la media y la externa selecciona los alumnos que la superan.
19. Crear índices con CREATE INDEX
Los índices permiten mejorar la velocidad de determinadas consultas.
CREATE INDEX idx_alumnos_nombre
ON alumnos(nombre);
Este índice puede mejorar las búsquedas por nombre. Para eliminarlo, se utiliza DROP INDEX.
DROP INDEX idx_alumnos_nombre;
20. Buenas prácticas
Para trabajar correctamente con SQLite, es recomendable:
    1. Utilizar nombres claros para las tablas y columnas.
    2. Seleccionar únicamente las columnas necesarias.
    3. Comprobar las condiciones antes de modificar o eliminar registros.
    4. Utilizar consultas parametrizadas cuando se reciben datos de usuarios.
    5. Crear índices cuando mejoren el rendimiento.
    6. Utilizar IS NULL para comprobar valores nulos.
    7. Realizar copias de seguridad antes de efectuar cambios importantes.
21. Conclusión
Las consultas SQL son fundamentales para gestionar bases de datos en SQLite. Mediante instrucciones como SELECT, INSERT, UPDATE y DELETE, es posible recuperar, añadir, modificar y eliminar información.
Además, las cláusulas WHERE, DISTINCT, ORDER BY, GROUP BY y JOIN permiten realizar consultas más avanzadas y obtener resultados específicos.
Dominar estas instrucciones facilita el desarrollo de aplicaciones que necesitan almacenar y procesar datos.

