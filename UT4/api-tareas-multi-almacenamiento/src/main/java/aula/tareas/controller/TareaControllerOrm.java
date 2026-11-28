package aula.tareas.controller;

import aula.tareas.api.TareaRequest;
import aula.tareas.api.TareaResponse;
import aula.tareas.mapper.TareaMapperApi;
import aula.tareas.modelo.Tarea;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orm/tareas")
@Tag(name = "ORM", description = "Gestion de tareas almacenadas con ORM")
public class TareaControllerOrm {

    private final ITareaService servicio;

    public TareaControllerOrm(@Qualifier("tareaServiceOrm") ITareaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar las tareas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas")
    public List<TareaResponse> listar() {
        List<TareaResponse> respuestas = new ArrayList<>();
        for (Tarea tarea : servicio.listar()) {
            respuestas.add(TareaMapperApi.toResponse(tarea));
        }
        return respuestas;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una tarea por ID")
    @ApiResponse(responseCode = "200", description = "Tarea encontrada")
    @ApiResponse(responseCode = "404", description = "No existe una tarea con ese ID")
    public ResponseEntity<TareaResponse> buscar(@PathVariable long id) {
        Optional<Tarea> tarea = servicio.buscar(id);
        if (tarea.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(TareaMapperApi.toResponse(tarea.get()));
    }

    @PostMapping
    @Operation(summary = "Crear una tarea")
    @ApiResponse(responseCode = "201", description = "Tarea creada")
    @ApiResponse(responseCode = "400", description = "Titulo en blanco o cuerpo invalido")
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        TareaResponse creada = TareaMapperApi.toResponse(servicio.crear(request));
        return ResponseEntity.created(URI.create("/api/orm/tareas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    @ApiResponse(responseCode = "200", description = "Tarea actualizada")
    @ApiResponse(responseCode = "400", description = "Titulo en blanco o cuerpo invalido")
    @ApiResponse(responseCode = "404", description = "No existe una tarea con ese ID")
    public ResponseEntity<TareaResponse> actualizar(@PathVariable long id,
                                                    @Valid @RequestBody TareaRequest request) {
        Optional<Tarea> tarea = servicio.actualizar(id, request);
        if (tarea.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(TareaMapperApi.toResponse(tarea.get()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada")
    @ApiResponse(responseCode = "404", description = "No existe una tarea con ese ID")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        if (servicio.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}