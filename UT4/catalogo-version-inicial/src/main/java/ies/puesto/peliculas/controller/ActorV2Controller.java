package ies.puesto.peliculas.controller;


import ies.puesto.peliculas.controller.api.ActorEdicionRequest;
import ies.puesto.peliculas.controller.api.ActorRequest;
import ies.puesto.peliculas.controller.api.ActorResponse;
import ies.puesto.peliculas.mapper.ActorMapper;
import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.service.ActorService;
import ies.puesto.peliculas.service.IActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Actores - v2")
@RestController
@RequestMapping("/api/v2/actores")
public class ActorV2Controller {

    private final IActorService service;

    public ActorV2Controller(@Qualifier("actorService") IActorService service) {
        this.service = service;
    }

    @Operation(summary = "Crear actor")
    @ApiResponse(responseCode = "201", description = "Operacion completada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping("/{idActor}")
    public ActorResponse crear(@Valid @RequestBody ActorRequest peticion) {
        Actor actor = service.crear(peticion.idActor(), ActorMapper.toDomain(peticion));
        return ActorMapper.toApi(actor);
    }

    @Operation(summary = "Editar actor")
    @ApiResponse(responseCode = "200", description = "Operacion completada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{idActor}")
    public ActorResponse editar(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,32}") String idActor,
                                @Valid @RequestBody ActorEdicionRequest peticion) {
        Actor actor = service.editar(idActor, ActorMapper.toDomain(peticion));
        return ActorMapper.toApi(actor);
    }

    @Operation(summary = "Eliminar actor")
    @ApiResponse(responseCode = "204", description = "Operacion completada", content = @Content)
    @ApiResponse(responseCode = "400", description = "Petición inválida", content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    @ApiResponse(responseCode = "404", description = "Recurso inexistente", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{idActor}")
    public void eliminar(@PathVariable @Pattern(regexp = "[A-Za-z0-9-]{1,32}") String idActor) {
        service.eliminar(idActor);
    }
}
