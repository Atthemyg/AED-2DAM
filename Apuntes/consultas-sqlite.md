# Consultas en SQLite

## 1. Introducción

SQLite es un sistema de gestión de bases de datos relacional ligero que almacena información en una base de datos sin necesitar un servidor independiente. SQL (*Structured Query Language*) permite consultar y modificar esos datos.

## 2. Instrucciones principales

- `SELECT`: consultar datos.
- `INSERT INTO`: insertar registros.
- `UPDATE`: modificar registros.
- `DELETE`: eliminar registros.
- `CREATE TABLE`: crear tablas.
- `ALTER TABLE`: modificar la estructura de una tabla.
- `DROP TABLE`: eliminar una tabla.

## 3. Crear una tabla

```sql
CREATE TABLE alumnos (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    edad INTEGER,
    nota REAL
);
```

`id` identifica cada registro, `nombre` almacena texto, `edad` un entero y `nota` un número que puede tener decimales.

## 4. Insertar datos: INSERT INTO

```sql
INSERT INTO alumnos (nombre, edad, nota)
VALUES ('Ana', 20, 8.5);

INSERT INTO alumnos (nombre, edad, nota)
VALUES ('Luis', 19, 6.0);
```

Cada instrucción añade un registro a la tabla.

## 5. Consultar datos: SELECT

Mostrar todas las columnas:

```sql
SELECT * FROM alumnos;
```

Mostrar columnas concretas:

```sql
SELECT nombre, nota
FROM alumnos;
```

Usar alias:

```sql
SELECT nombre AS alumno, nota AS calificacion
FROM alumnos;
```

## 6. Eliminar duplicados: DISTINCT

`DISTINCT` devuelve valores o filas únicos, evitando duplicados en el resultado.

```sql
SELECT DISTINCT edad
FROM alumnos;
```

Para contar cuántas edades diferentes hay:

```sql
SELECT COUNT(DISTINCT edad)
FROM alumnos;
```

Si se seleccionan varias columnas, se eliminan las filas que tengan duplicada la misma combinación de valores.

## 7. Filtrar resultados: WHERE

`WHERE` selecciona los registros que cumplen una condición.

```sql
SELECT *
FROM alumnos
WHERE edad > 19;
```

```sql
SELECT nombre, nota
FROM alumnos
WHERE nota >= 5;
```

Operadores habituales:

| Operador | Significado |
|---|---|
| `=` | Igual |
| `!=` o `<>` | Distinto |
| `>` | Mayor |
| `<` | Menor |
| `>=` | Mayor o igual |
| `<=` | Menor o igual |

## 8. Operadores lógicos

`AND` exige que se cumplan todas las condiciones:

```sql
SELECT *
FROM alumnos
WHERE edad >= 20 AND nota >= 8;
```

`OR` acepta que se cumpla alguna condición:

```sql
SELECT *
FROM alumnos
WHERE edad = 18 OR edad = 19;
```

`NOT` niega una condición:

```sql
SELECT *
FROM alumnos
WHERE NOT edad = 18;
```

## 9. Ordenar resultados: ORDER BY

```sql
SELECT nombre, nota
FROM alumnos
ORDER BY nota ASC;
```

- `ASC`: orden ascendente.
- `DESC`: orden descendente.

Para ordenar de mayor a menor, se utiliza `ORDER BY nota DESC`.

## 10. Limitar resultados: LIMIT

`LIMIT` limita la cantidad de filas devueltas.

```sql
SELECT nombre, nota
FROM alumnos
ORDER BY nota DESC
LIMIT 3;
```

`OFFSET` permite saltar filas:

```sql
SELECT *
FROM alumnos
LIMIT 2 OFFSET 1;
```

## 11. Buscar patrones: LIKE

`LIKE` permite buscar texto usando comodines:

- `%`: cero o más caracteres.
- `_`: exactamente un carácter.

Nombres que empiezan por M:

```sql
SELECT *
FROM alumnos
WHERE nombre LIKE 'M%';
```

Nombres que terminan en a:

```sql
SELECT *
FROM alumnos
WHERE nombre LIKE '%a';
```

## 12. IN, BETWEEN e IS NULL

`IN` comprueba si un valor pertenece a una lista:

```sql
SELECT *
FROM alumnos
WHERE edad IN (18, 20, 21);
```

`BETWEEN` busca valores dentro de un intervalo, incluidos sus límites:

```sql
SELECT *
FROM alumnos
WHERE nota BETWEEN 6 AND 9;
```

`IS NULL` busca campos sin valor conocido:

```sql
SELECT *
FROM alumnos
WHERE nota IS NULL;
```

Para buscar valores no nulos, se usa `IS NOT NULL`. No se debe utilizar `= NULL`.

## 13. Funciones de agregación

| Función | Utilidad |
|---|---|
| `COUNT()` | Cuenta registros o valores |
| `SUM()` | Suma valores numéricos |
| `AVG()` | Calcula la media |
| `MAX()` | Obtiene el máximo |
| `MIN()` | Obtiene el mínimo |

Ejemplos:

```sql
SELECT COUNT(*) FROM alumnos;
SELECT AVG(nota) FROM alumnos;
SELECT MAX(nota) FROM alumnos;
SELECT MIN(nota) FROM alumnos;
```

## 14. Agrupar: GROUP BY y HAVING

`GROUP BY` agrupa filas con valores comunes y permite calcular estadísticas por grupo.

```sql
SELECT edad, COUNT(*) AS total
FROM alumnos
GROUP BY edad;
```

`HAVING` filtra los grupos después de agruparlos:

```sql
SELECT edad, COUNT(*) AS total
FROM alumnos
GROUP BY edad
HAVING COUNT(*) > 1;
```

## 15. Modificar datos: UPDATE

```sql
UPDATE alumnos
SET nota = 9.0
WHERE nombre = 'Ana';
```

**Importante:** si se omite `WHERE`, se modificarán todos los registros.

## 16. Eliminar datos: DELETE

```sql
DELETE FROM alumnos
WHERE nombre = 'Luis';
```

Si se omite `WHERE`, se eliminan todos los registros, pero la tabla sigue existiendo.

## 17. Combinar tablas: JOIN

`JOIN` combina información de tablas relacionadas.

```sql
SELECT alumnos_cursos.nombre, cursos.nombre AS curso
FROM alumnos_cursos
INNER JOIN cursos
ON alumnos_cursos.curso_id = cursos.id;
```

- `INNER JOIN`: devuelve coincidencias en ambas tablas.
- `LEFT JOIN`: devuelve todas las filas de la tabla izquierda y las coincidencias de la derecha.
- `CROSS JOIN`: combina cada fila de una tabla con cada fila de la otra.

## 18. Subconsultas

Una subconsulta es una consulta dentro de otra. Este ejemplo muestra los alumnos con una nota superior a la media:

```sql
SELECT nombre, nota
FROM alumnos
WHERE nota > (
    SELECT AVG(nota)
    FROM alumnos
);
```

## 19. Índices: CREATE INDEX

Los índices pueden acelerar ciertas consultas, especialmente las búsquedas frecuentes.

```sql
CREATE INDEX idx_alumnos_nombre
ON alumnos(nombre);
```

Para eliminar el índice:

```sql
DROP INDEX idx_alumnos_nombre;
```

Los índices ocupan espacio y pueden ralentizar algunas modificaciones.

## 20. Buenas prácticas

1. Utilizar nombres claros para tablas y columnas.
2. Seleccionar solo las columnas necesarias.
3. Comprobar las condiciones antes de ejecutar `UPDATE` o `DELETE`.
4. Utilizar consultas parametrizadas en aplicaciones.
5. Crear índices cuando aporten una mejora.
6. Comprobar valores nulos con `IS NULL`.
7. Hacer copias de seguridad antes de cambios importantes.

## 21. Ejercicios

1. Crear una tabla `productos` con `id`, `nombre`, `precio` y `stock`.
2. Insertar cinco productos.
3. Mostrar los productos con precio superior a 20.
4. Ordenar los productos por precio descendente.
5. Mostrar los tres productos más caros.
6. Calcular el precio medio.
7. Buscar los productos cuyo nombre empiece por A.
8. Mostrar los precios diferentes con `DISTINCT`.
9. Modificar el stock de un producto.
10. Eliminar los productos cuyo stock sea cero.

## 22. Conclusión

Las consultas SQL permiten recuperar, insertar, modificar y eliminar datos en SQLite. Cláusulas como `WHERE`, `DISTINCT`, `ORDER BY`, `GROUP BY` y `JOIN` ayudan a obtener resultados concretos y realizar operaciones más avanzadas.

## 23. Bibliografía

- [Documentación oficial de SQLite](https://www.sqlite.org/docs.html)
- [Tutorial de SQLite](https://www.sqlitetutorial.net/)
- [Referencia del lenguaje SQL de SQLite](https://www.sqlite.org/lang.html)
