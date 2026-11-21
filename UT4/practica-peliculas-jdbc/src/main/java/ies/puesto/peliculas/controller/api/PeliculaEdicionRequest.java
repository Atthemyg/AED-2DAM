package ies.puesto.peliculas.controller.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// El codigo llega en la URL
public record PeliculaEdicionRequest(
        @NotBlank @Size(max = 120) String titulo,
        @NotNull @Min(1888) @Max(2100) Integer anioEstreno,
        @NotNull @Min(1) @Max(1000) Integer duracionMinutos) {

}
