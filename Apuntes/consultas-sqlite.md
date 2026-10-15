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

Una subconsulta es una consulta dentro de otra. Puede utilizarse, por ejemplo, para comparar valores con un promedio, comprobar si existen registros o crear una tabla temporal para la consulta principal.

### Subconsulta en WHERE

Este ejemplo muestra los alumnos con una nota superior a la media:

```sql
SELECT nombre, nota
FROM alumnos
WHERE nota > (
    SELECT AVG(nota)
    FROM alumnos
);
```

### Subconsulta con IN

```sql
SELECT nombre
FROM alumnos
WHERE edad IN (
    SELECT edad
    FROM alumnos
    WHERE nota >= 8
);
```

Esta consulta muestra los alumnos cuya edad coincide con la edad de algún alumno que tiene una nota de 8 o más.

### Subconsulta en FROM

```sql
SELECT resumen.edad, resumen.total
FROM (
    SELECT edad, COUNT(*) AS total
    FROM alumnos
    GROUP BY edad
) AS resumen
WHERE resumen.total > 1;
```

La subconsulta calcula cuántos alumnos hay por edad y la consulta exterior muestra solo las edades con más de un alumno. El alias `resumen` permite referirse al resultado de la subconsulta.

## 19. Expresiones de tabla comunes: CTE (`WITH`)

Una CTE (*Common Table Expression*) es un resultado temporal con nombre que se define al principio de una consulta mediante `WITH`. Puede utilizarse dentro de la consulta principal como si fuera una tabla, lo que ayuda a organizar consultas complejas y hacerlas más legibles.

### CTE básica

```sql
WITH alumnos_aprobados AS (
    SELECT nombre, nota
    FROM alumnos
    WHERE nota >= 5
)
SELECT nombre, nota
FROM alumnos_aprobados;
```

En este ejemplo:

- `WITH` inicia la definición de la CTE.
- `alumnos_aprobados` es el nombre que se le da al resultado temporal.
- La consulta entre paréntesis define los datos que contiene.
- La consulta principal utiliza la CTE como fuente de datos.

### CTE con una función de agregación

```sql
WITH promedio AS (
    SELECT AVG(nota) AS nota_media
    FROM alumnos
)
SELECT alumnos.nombre, alumnos.nota, promedio.nota_media
FROM alumnos
CROSS JOIN promedio
WHERE alumnos.nota > promedio.nota_media;
```

Esta consulta muestra los alumnos cuya nota está por encima de la media. La CTE calcula la media una vez y permite utilizar ese resultado en la consulta principal.

### Varias CTE en una misma consulta

Se pueden definir varias CTE separándolas con comas:

```sql
WITH aprobados AS (
    SELECT nombre, nota
    FROM alumnos
    WHERE nota >= 5
), notas_altas AS (
    SELECT nombre, nota
    FROM aprobados
    WHERE nota >= 8
)
SELECT nombre, nota
FROM notas_altas;
```

Las CTE se definen en orden y una CTE posterior puede utilizar una anterior.

### CTE recursiva

`WITH RECURSIVE` permite crear consultas que se refieren a sí mismas, por ejemplo, para generar una secuencia de números:

```sql
WITH RECURSIVE numeros(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1
    FROM numeros
    WHERE n < 5
)
SELECT n
FROM numeros;
```

El resultado es una columna con los números del 1 al 5. Las CTE recursivas son útiles para secuencias y datos jerárquicos, como árboles o categorías relacionadas.

Una CTE existe únicamente durante la ejecución de la instrucción SQL en la que se define; no crea una tabla permanente.

---

## 20. Expresiones condicionales: CASE

`CASE` permite devolver un resultado distinto según se cumplan determinadas condiciones. Puede utilizarse dentro de una consulta `SELECT`, por ejemplo, para clasificar datos.

### CASE con WHEN y THEN

```sql
SELECT nombre, nota,
       CASE
           WHEN nota >= 9 THEN 'Sobresaliente'
           WHEN nota >= 7 THEN 'Notable'
           WHEN nota >= 5 THEN 'Aprobado'
           ELSE 'Suspenso'
       END AS calificacion
FROM alumnos;
```

- `CASE`: inicia la expresión condicional.
- `WHEN`: indica una condición que se evalúa.
- `THEN`: indica el resultado si la condición se cumple.
- `ELSE`: indica el resultado si ninguna condición anterior se cumple. Es opcional.
- `END`: termina la expresión `CASE`.
- `AS calificacion`: da un nombre a la columna resultante.

Las condiciones se evalúan en orden y se devuelve el resultado de la primera que se cumpla.

### CASE para clasificar valores

```sql
SELECT nombre, edad,
       CASE
           WHEN edad < 18 THEN 'Menor de edad'
           ELSE 'Adulto'
       END AS grupo_edad
FROM alumnos;
```

### CASE con una expresión

También se puede comparar una misma expresión con varios valores:

```sql
SELECT nombre, edad,
       CASE edad
           WHEN 18 THEN 'Tiene 18 años'
           WHEN 19 THEN 'Tiene 19 años'
           WHEN 20 THEN 'Tiene 20 años'
           ELSE 'Otra edad'
       END AS mensaje
FROM alumnos;
```

### CASE dentro de ORDER BY

```sql
SELECT nombre, nota
FROM alumnos
ORDER BY
    CASE
        WHEN nota IS NULL THEN 1
        ELSE 0
    END,
    nota DESC;
```

Este ejemplo coloca primero las notas conocidas y después las filas cuya nota es `NULL`, ordenando las notas conocidas de mayor a menor.

---

## 21. Funciones de texto: SUBSTR e INSTR

SQLite incluye funciones para extraer partes de una cadena y localizar texto dentro de otra cadena.

### SUBSTR()

`SUBSTR(cadena, inicio, longitud)` devuelve una parte de una cadena.

- `cadena`: texto del que se quiere extraer una parte.
- `inicio`: posición desde la que empieza la extracción. La primera posición es `1`.
- `longitud`: cantidad de caracteres que se extraen; es opcional si se quiere obtener el resto de la cadena.

```sql
SELECT SUBSTR('SQLite', 1, 3);
-- Resultado: SQL

SELECT SUBSTR('SQLite', 4);
-- Resultado: ite
```

Ejemplo con la tabla `alumnos`, para mostrar las tres primeras letras de cada nombre:

```sql
SELECT nombre, SUBSTR(nombre, 1, 3) AS inicio_nombre
FROM alumnos;
```

### INSTR()

`INSTR(cadena, texto)` devuelve la posición de la primera aparición de `texto` dentro de `cadena`. La posición empieza en `1`; si no encuentra el texto, devuelve `0`.

```sql
SELECT INSTR('Base de datos', 'de');
-- Resultado: 6

SELECT INSTR('SQLite', 'xyz');
-- Resultado: 0
```

Ejemplo para encontrar nombres que contienen la letra `a`:

```sql
SELECT nombre
FROM alumnos
WHERE INSTR(nombre, 'a') > 0;
```

`SUBSTR` extrae caracteres; `INSTR` localiza la posición de un texto dentro de otro.

---

## 22. Funciones de ventana: OVER, PARTITION BY, RANK, SUM y AVG

Las funciones de ventana calculan un resultado a partir de un conjunto de filas relacionadas con cada fila, sin reducir cada grupo a una sola fila como suele ocurrir con `GROUP BY`. Se escriben con `OVER (...)`.

### PARTITION BY

`PARTITION BY` divide las filas en grupos para calcular la función por separado en cada grupo. En este ejemplo, cada edad forma un grupo y se cuenta cuántos alumnos tienen esa edad:

```sql
SELECT nombre, edad,
       COUNT(*) OVER (PARTITION BY edad) AS alumnos_misma_edad
FROM alumnos;
```

A diferencia de `GROUP BY`, se mantiene una fila por alumno.

### AVG() OVER (PARTITION BY)

Calcula la media de la nota dentro de cada grupo de edad y muestra esa media junto a cada alumno:

```sql
SELECT nombre, edad, nota,
       AVG(nota) OVER (PARTITION BY edad) AS media_edad
FROM alumnos;
```

### SUM() OVER

`SUM() OVER` permite mostrar una suma acumulada o una suma de grupo sin agrupar las filas en una sola. Este ejemplo calcula la suma de notas por edad:

```sql
SELECT nombre, edad, nota,
       SUM(nota) OVER (PARTITION BY edad) AS suma_notas_edad
FROM alumnos;
```

Para obtener una suma acumulada ordenada por nota, se puede especificar una ventana con orden:

```sql
SELECT nombre, nota,
       SUM(nota) OVER (
           ORDER BY nota
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS suma_acumulada
FROM alumnos
WHERE nota IS NOT NULL;
```

### RANK() OVER

`RANK()` asigna una posición a cada fila según el orden indicado en `OVER (ORDER BY ...)`. Las filas empatadas reciben el mismo rango y el siguiente rango deja un salto.

```sql
SELECT nombre, nota,
       RANK() OVER (ORDER BY nota DESC) AS puesto
FROM alumnos
WHERE nota IS NOT NULL;
```

Por ejemplo, si dos alumnos empatan en el primer puesto, ambos reciben rango 1 y el siguiente recibe rango 3.

### RANK() con PARTITION BY

Se puede reiniciar la clasificación dentro de cada grupo. Este ejemplo clasifica las notas dentro de cada edad:

```sql
SELECT nombre, edad, nota,
       RANK() OVER (
           PARTITION BY edad
           ORDER BY nota DESC
       ) AS puesto_en_edad
FROM alumnos
WHERE nota IS NOT NULL;
```

El rango empieza de nuevo para cada valor de `edad`. `ORDER BY` dentro de `OVER` determina el orden usado para calcular el rango; no garantiza el orden final de las filas. Para ordenar la salida, añade un `ORDER BY` al final de la consulta.

**Diferencia clave:** `GROUP BY` suele devolver una fila por grupo; las funciones de ventana con `OVER (PARTITION BY ...)` conservan las filas originales y añaden el cálculo a cada una.

---

## 23. Índices: CREATE INDEX

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

## 24. Buenas prácticas

1. Utilizar nombres claros para tablas y columnas.
2. Seleccionar solo las columnas necesarias.
3. Comprobar las condiciones antes de ejecutar `UPDATE` o `DELETE`.
4. Utilizar consultas parametrizadas en aplicaciones.
5. Crear índices cuando aporten una mejora.
6. Comprobar valores nulos con `IS NULL`.
7. Hacer copias de seguridad antes de cambios importantes.


