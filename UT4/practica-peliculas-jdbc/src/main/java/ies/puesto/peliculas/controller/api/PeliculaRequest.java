package ies.puesto.peliculas.controller.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Cuerpo del POST
public record PeliculaRequest(
        String codigo,
        String titulo,
        Integer anioEstreno,
        Integer duracionMinutos) {

}
