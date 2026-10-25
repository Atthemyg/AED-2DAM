-- 1. Todos los clientes alfabéticamente
SELECT id, nombre, email, telefono
FROM clientes
ORDER BY nombre ASC;

-- 2. Mascotas de una especie (modifica 'Perro' si deseas)
SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente
FROM mascotas
WHERE especie = 'Perro'
ORDER BY nombre;

-- 3. Mascotas de un cliente
SELECT id, nombre, especie, raza, fecha_nacimiento
FROM mascotas
WHERE id_cliente = 1
ORDER BY nombre;

-- 4. Veterinarios de una especialidad
SELECT id, nombre, especialidad, tarifa_hora
FROM veterinarios
WHERE especialidad = 'Medicina general'
ORDER BY tarifa_hora, nombre;

-- 5. Consultas programadas
SELECT id, id_mascota, id_veterinario, fecha, horas, estado
FROM consultas
WHERE estado = 'PROGRAMADA'
ORDER BY fecha;

-- 6a. Mascota y propietario con WHERE
SELECT m.id AS id_mascota, m.nombre AS mascota, m.especie, c.nombre AS propietario
FROM mascotas m, clientes c
WHERE m.id_cliente = c.id
ORDER BY m.id;

-- 6b. Mascota y propietario con INNER JOIN
SELECT m.id AS id_mascota, m.nombre AS mascota, m.especie, c.nombre AS propietario
FROM mascotas m
    INNER JOIN clientes c ON c.id = m.id_cliente
ORDER BY m.id;

-- 7. Consultas, mascota, propietario y veterinario
SELECT q.id AS id_consulta, q.fecha, q.horas, q.estado,
       m.nombre AS mascota, c.nombre AS propietario, v.nombre AS veterinario
FROM consultas q
    INNER JOIN mascotas m     ON m.id = q.id_mascota
    INNER JOIN clientes c     ON c.id = m.id_cliente
    INNER JOIN veterinarios v ON v.id = q.id_veterinario
ORDER BY q.fecha;

-- 8. Cantidad de mascotas por cliente, incluidos los de cero mascotas
SELECT c.id, c.nombre, COUNT(m.id) AS num_mascotas
FROM clientes c
    LEFT JOIN mascotas m ON m.id_cliente = c.id
GROUP BY c.id, c.nombre
ORDER BY c.id;

-- 9. Importe tratamientos, incluyendo consultas sin tratamientos
SELECT q.id AS id_consulta, q.fecha,
       COALESCE(SUM(t.cantidad * t.precio_unitario), 0) AS importe_tratamientos
FROM consultas q
    LEFT JOIN tratamientos t ON t.id_consulta = q.id
GROUP BY q.id, q.fecha
ORDER BY q.id;


SELECT q.id AS id_consulta, q.fecha,
       CASE
           WHEN SUM(t.cantidad * t.precio_unitario) IS NULL THEN 0
           ELSE SUM(t.cantidad * t.precio_unitario)
           END AS importe_tratamientos
FROM consultas q
         LEFT JOIN tratamientos t ON t.id_consulta = q.id
GROUP BY q.id, q.fecha
ORDER BY q.id;

-- 10. Consultas sin tratamientos
SELECT q.id, q.id_mascota, q.id_veterinario, q.fecha, q.horas, q.estado
FROM consultas q
    LEFT JOIN tratamientos t ON t.id_consulta = q.id
WHERE t.id IS NULL
ORDER BY q.id;
