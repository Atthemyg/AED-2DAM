package aula.tareas.controller;

import aula.tareas.modelo.Tarea;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/xml/tareas")
@Tag(name = "Tareas XML", description = "Operaciones crud de tareas en xml")
public class TareaControllerXml {

    private final ITareaService serviceXml;

    public TareaControllerXml(@Qualifier("tareaServiceXml") ITareaService serviceXml) {
        this.serviceXml = serviceXml;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tareas desde el xml")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<Tarea> listar() {
        return serviceXml.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    ResponseEntity<Tarea> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(serviceXml.buscar(id));
    }

    @PostMapping
    @Operation(summary = "Crear una tarea nueva")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "Titulo en blanco")
    })
    ResponseEntity<Tarea> crear(@Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        Tarea tarea = serviceXml.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/xml/tareas/" + tarea.id())).body(tarea);
    }
}