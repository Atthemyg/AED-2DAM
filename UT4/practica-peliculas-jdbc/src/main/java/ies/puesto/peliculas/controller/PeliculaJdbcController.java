package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.controller.api.PeliculaEdicionRequest;
import ies.puesto.peliculas.controller.api.PeliculaRequest;
import ies.puesto.peliculas.controller.api.PeliculaResponse;
import ies.puesto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.PeliculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Profile("jdbc")
@RequestMapping("/api/v1/jdbc/peliculas")
@Tag(name = "Películas Jdbc", description = "Operaciones crud de películas en jdbc")
public class PeliculaJdbcController {

    private final PeliculaService service;
    private final PeliculaApiMapper mapper;

    public PeliculaJdbcController(PeliculaService service, PeliculaApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las películas")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Listado de películas"))
    public List<PeliculaResponse> listar() {
        return mapper.aRespuestas(service.listar());
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener una película por su código")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película encontrada"),
            @ApiResponse(responseCode = "400", description = "Código inválido"),
            @ApiResponse(responseCode = "404", description = "La película no existe")
    })
    public ResponseEntity<?> obtener(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        Optional<Pelicula> pelicula = service.obtener(codigo);
        if (pelicula.isEmpty()) {
            return noExiste(codigo);
        }
        return ResponseEntity.ok(mapper.aRespuesta(pelicula.get()));
    }

    @PostMapping
    @Operation(summary = "Crear una película")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Película creada"),
            @ApiResponse(responseCode = "400", description = "Cuerpo inválido"),
            @ApiResponse(responseCode = "409", description = "El código ya existe")
    })
    public ResponseEntity<?> crear(@Valid @RequestBody PeliculaRequest peticion) {
        Optional<Pelicula> pelicula = service.crear(mapper.aDatosCreacion(peticion));
        if (pelicula.isEmpty()) {
            ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    "Ya existe la película " + peticion.codigo());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.aRespuesta(pelicula.get()));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Modificar una película")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Película actualizada"),
            @ApiResponse(responseCode = "400", description = "Código o cuerpo inválido"),
            @ApiResponse(responseCode = "404", description = "La película no existe")
    })
    public ResponseEntity<?> actualizar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo,
                                        @Valid @RequestBody PeliculaEdicionRequest peticion) {
        Optional<Pelicula> pelicula = service.actualizar(codigo, mapper.aDatosEdicion(peticion));
        if (pelicula.isEmpty()) {
            return noExiste(codigo);
        }
        return ResponseEntity.ok(mapper.aRespuesta(pelicula.get()));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar una película")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Película eliminada"),
            @ApiResponse(responseCode = "400", description = "Código inválido"),
            @ApiResponse(responseCode = "404", description = "La película no existe")
    })
    public ResponseEntity<?> eliminar(@PathVariable @Pattern(regexp = "^PEL[0-9]{3}$") String codigo) {
        if (!service.eliminar(codigo)) {
            return noExiste(codigo);
        }
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ProblemDetail> noExiste(String codigo) {
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No existe la película " + codigo);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}