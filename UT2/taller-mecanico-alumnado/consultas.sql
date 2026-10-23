-- 1. Lista los clientes ordenados por nombre.
SELECT id_cliente, nombre, email, telefono
FROM clientes
ORDER BY nombre;

-- 2. Consulta matrícula, marca, modelo y año de los vehículos de una marca elegida.
SELECT matricula, marca, modelo, anio
FROM vehiculos
WHERE marca = 'Seat';

-- 3. Obtén los vehículos de un cliente utilizando su identificador.
SELECT matricula, marca, modelo, anio, id_cliente
FROM vehiculos
WHERE id_cliente = 1;

-- 4. Lista los mecánicos de una especialidad y ordénalos por tarifa.
SELECT id_mecanico, nombre, especialidad, tarifa_hora
FROM mecanicos
WHERE especialidad = 'Motor'
ORDER BY tarifa_hora;

-- 5. Obtén las reparaciones pendientes, ordenadas por fecha.
SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado
FROM reparaciones
WHERE estado = 'PENDIENTE'
ORDER BY fecha;

-- 6. Muestra la matrícula de cada vehículo y el nombre de su propietario. Resuélvelo una vez con `WHERE` y otra con `INNER JOIN`.
SELECT v.matricula, c.nombre
FROM vehiculos v, clientes c
WHERE v.id_cliente = c.id_cliente;


SELECT v.matricula, c.nombre
FROM vehiculos v
INNER JOIN clientes c ON v.id_cliente = c.id_cliente;

-- 7. Muestra las reparaciones con el nombre del cliente, la matrícula y el nombre del mecánico.
SELECT r.id_reparacion, c.nombre AS cliente, r.matricula, m.nombre AS mecanico
FROM reparaciones r
         INNER JOIN vehiculos v ON r.matricula = v.matricula
         INNER JOIN clientes c ON v.id_cliente = c.id_cliente
         INNER JOIN mecanicos m ON r.id_mecanico = m.id_mecanico
ORDER BY r.id_reparacion;

-- 8. Cuenta los vehículos de cada cliente, incluyendo clientes sin vehículos.
SELECT c.id_cliente, c.nombre, COUNT(v.matricula) AS num_vehiculos
FROM clientes c
         LEFT JOIN vehiculos v ON v.id_cliente = c.id_cliente
GROUP BY c.id_cliente, c.nombre
ORDER BY c.id_cliente;

-- 9. Calcula el coste de piezas por reparación, incluyendo las reparaciones sin piezas con coste cero.
SELECT r.id_reparacion,
       COALESCE(SUM(p.cantidad * p.precio_unitario), 0) AS coste_piezas
FROM reparaciones r
         LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
GROUP BY r.id_reparacion
ORDER BY r.id_reparacion;


SELECT r.id_reparacion,
       TOTAL(p.cantidad * p.precio_unitario) AS coste_piezas
FROM reparaciones r
         LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
GROUP BY r.id_reparacion
ORDER BY r.id_reparacion;


SELECT r.id_reparacion,
       CASE
           WHEN SUM(p.cantidad * p.precio_unitario) IS NULL THEN 0
           ELSE SUM(p.cantidad * p.precio_unitario)
           END AS coste_piezas
FROM reparaciones r
         LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
GROUP BY r.id_reparacion
ORDER BY r.id_reparacion;

-- 10. Obtén las reparaciones que no tienen piezas asociadas.
SELECT r.id_reparacion, r.matricula, r.descripcion, r.fecha, r.estado
FROM reparaciones r
         LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
WHERE p.id_pieza IS NULL
ORDER BY r.id_reparacion;