package aula.tareas.controller;

import aula.tareas.api.TareaRequest;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

public class TareaControllerJdbc {

    @RestController
    @RequestMapping("/api/jdbc/tareas")
    @Tag(name = "Tareas Jdbc", description = "Operaciones crud de tareas en jdbc")
    public class TareaControllerJson {

        private final ITareaService serviceJdbc;

        public TareaControllerJson(@Qualifier("tareaServiceJdbc") ITareaService serviceJdbc) {
            this.serviceJdbc = serviceJdbc;
        }

        @GetMapping
        @Operation(summary = "Obtener todas las tareas")
        @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
        List<TareaRequest> listar() {
            return serviceJdbc.listar();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener la tarea a traves de su id")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
                @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
        })
        ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
            return ResponseEntity.ofNullable(serviceJdbc.buscar(id));
        }

        @PostMapping
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Tarea creada"),
                @ApiResponse(responseCode = "400", description = "Tarea existe")
        })
        ResponseEntity<TareaRequest> crear(@Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
            TareaRequest tareaRequest = serviceJdbc.crear(peticion.titulo());
            return ResponseEntity.created(URI.create("/api/jdbc/tareas" + tareaRequest.id())).body(tareaRequest);
        }

        @PutMapping("/{id}")
        ResponseEntity<TareaRequest> update(@PathVariable long id, @Valid @RequestBody TareaRequest tarea) {
            return null;
        }

        public record NuevaTarea(@NotBlank String titulo) {}
    }
}
