# Recorrido de una petición: POST y GET por código

Este documento explica qué pasa, paso a paso, cuando llega un **POST** (crear una película) y un **GET por código** (consultar una película) a `/api/v1/jdbc/peliculas`.

Las capas, de fuera hacia dentro, son: **API** (`controller`) → **servicio** (`service`) → **repositorio** (`repository.jdbc`) → **SQLite**. Un mismo dato cambia de nombre al cruzar cada frontera, y siempre lo traduce un mapper.

## Cómo se llama cada dato en cada capa

| Dato | JSON y `controller.api` | `service.modelo` | `model` | `repository.jdbc` | Columna SQLite |
|---|---|---|---|---|---|
| Identificador | `codigo` | `referenciaSolicitada` | `identificador` | `codigoFila` | `pelicula_codigo` |
| Título | `titulo` | `nombreSolicitado` | `nombre` | `tituloFila` | `titulo_texto` |
| Año | `anioEstreno` | `anioSolicitado` | `anioLanzamiento` | `anioFila` | `estreno_anio` |
| Duración | `duracionMinutos` | `minutosSolicitados` | `minutosProyeccion` | `minutosFila` | `duracion_minutos` |

## POST: crear una película

Ejemplo de cuerpo:

```json
{ "codigo": "PEL010", "titulo": "Akira", "anioEstreno": 1988, "duracionMinutos": 124 }
```

### 1. Clases por las que pasa la petición (ida)

1. **`PeliculaJdbcController.crear`**: recibe el JSON como `PeliculaRequest`. Antes de entrar en el método, `@Valid` comprueba las reglas de validación del `record`. Si alguna falla, Spring responde `400` y el método no llega a ejecutarse.
2. **`PeliculaService.crear`**: recibe un `DatosCreacionPelicula`.
3. **`PeliculaRepository.insertarSiAusente`** (contrato), implementado por **`PeliculaJdbcRepository`**: recibe una `Pelicula`.
4. **`JdbcTemplate.update`**: ejecuta el `INSERT ... ON CONFLICT(pelicula_codigo) DO NOTHING` sobre SQLite.

### 2. Clases por las que pasa la respuesta (vuelta)

`update` devuelve el número de filas insertadas → `PeliculaJdbcRepository` lo convierte en un `boolean` (`== 1`) → `PeliculaService` lo convierte en `Optional<Pelicula>` → `PeliculaJdbcController` lo convierte en una respuesta HTTP con un `PeliculaResponse` (o un `ProblemDetail` si hay error).

### 3. Mapper de cada frontera

| Frontera | Mapper | Método | Conversión |
|---|---|---|---|
| API → servicio | `PeliculaApiMapper` (`controller.mapper`) | `aDatosCreacion` | `PeliculaRequest` → `DatosCreacionPelicula` |
| Servicio → dominio | `PeliculaDominioMapper` (`service.mapper`) | `aPelicula(DatosCreacionPelicula)` | `DatosCreacionPelicula` → `Pelicula` |
| Dominio → repositorio | `PeliculaEntityMapper` (`repository.jdbc`) | `aEntity` | `Pelicula` → `PeliculaEntity` |
| Dominio → API (respuesta) | `PeliculaApiMapper` | `aRespuesta` | `Pelicula` → `PeliculaResponse` |

### 4. Nombre de cada dato a lo largo del recorrido (ejemplo `titulo`)

`titulo` (JSON y `PeliculaRequest`) → `nombreSolicitado` (`DatosCreacionPelicula`) → `nombre` (`Pelicula`) → `tituloFila` (`PeliculaEntity`) → `titulo_texto` (columna). Los otros tres datos siguen el mismo camino, como muestra la tabla de arriba. En la respuesta el recorrido es el inverso: `nombre` → `titulo` en `PeliculaResponse`.

### 5. Cómo se decide el código HTTP

| Situación | Quién lo decide | Código |
|---|---|---|
| El JSON está mal formado o incumple una regla (código sin patrón, título en blanco o de más de 120 caracteres, año fuera de 1888–2100, duración fuera de 1–1000, campo ausente) | Spring, al leer y validar el cuerpo, antes de ejecutar el método | `400` |
| `insertarSiAusente` devuelve `false`: el `INSERT` no modificó ninguna fila porque el código ya existía | El servicio devuelve `Optional.empty()` y el controlador lo traduce a `ProblemDetail` | `409` |
| `insertarSiAusente` devuelve `true`: se insertó una fila | El servicio devuelve la misma `Pelicula` y el controlador devuelve el `PeliculaResponse` | `201` |

## GET por código: consultar una película

Ejemplo: `GET /api/v1/jdbc/peliculas/PEL001`. No hay cuerpo: el código viaja en la URL como `String codigo`, así que en la entrada no interviene ningún mapper.

### 1. Clases por las que pasa la petición (ida)

1. **`PeliculaJdbcController.obtener`**: recibe el `codigo` con `@PathVariable`. `@Pattern(regexp = "^PEL[0-9]{3}$")` comprueba su formato antes de entrar en el método.
2. **`PeliculaService.obtener`**: recibe el `String codigo`.
3. **`PeliculaRepository.buscarPorCodigo`**, implementado por **`PeliculaJdbcRepository`**: recibe el `String identificador`.
4. **`JdbcTemplate.query`**: ejecuta `SELECT ... FROM peliculas WHERE pelicula_codigo = ?` y usa `PeliculaRowMapper` por cada fila.

### 2. Clases por las que pasa la respuesta (vuelta)

`ResultSet` → `PeliculaEntity` → `Pelicula` → `Optional<Pelicula>` (con `stream().findFirst()`) → `PeliculaResponse` → JSON. Si no hay fila, el `Optional` queda vacío en todo el recorrido y no se lanza ninguna excepción.

### 3. Mapper de cada frontera

| Frontera | Mapper | Método | Conversión |
|---|---|---|---|
| SQLite → repositorio | `PeliculaRowMapper` (`repository.jdbc`) | `mapRow` | `ResultSet` → `PeliculaEntity` |
| Repositorio → dominio | `PeliculaEntityMapper` | `aPelicula` | `PeliculaEntity` → `Pelicula` |
| Dominio → API | `PeliculaApiMapper` | `aRespuesta` | `Pelicula` → `PeliculaResponse` |

### 4. Nombre de cada dato a lo largo del recorrido (ejemplo `titulo`)

`titulo_texto` (columna) → `tituloFila` (`PeliculaEntity`) → `nombre` (`Pelicula`) → `titulo` (`PeliculaResponse` y JSON). Entre repositorio y servicio no hay mapper de servicio, porque el servicio ya trabaja directamente con `Pelicula`.

### 5. Cómo se decide el código HTTP

| Situación | Quién lo decide | Código |
|---|---|---|
| El código no cumple `^PEL[0-9]{3}$` | Spring, al validar `@Pattern`, antes de ejecutar el método | `400` |
| `obtener` devuelve `Optional.empty()`: no hay fila con ese código | El controlador lo traduce a `ProblemDetail` con `detail` "No existe la película PEL099" | `404` |
| `obtener` devuelve una `Pelicula` | El controlador devuelve el `PeliculaResponse` | `200` |

## Por qué el servicio no conoce HTTP

`PeliculaService` no importa `ResponseEntity`, `HttpStatus` ni las clases de `controller.api`, y no lanza excepciones. Solo informa del resultado con `Optional` (con valor o vacío) y `boolean`. La traducción de esa información a un código HTTP se hace únicamente en `PeliculaJdbcController`, que es lo que permite cambiar la capa web sin tocar el servicio.
