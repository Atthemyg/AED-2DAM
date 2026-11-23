package ies.puesto.peliculas.controller.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// El codigo llega en la URL
public record PeliculaEdicionRequest(
        String titulo,
        Integer anioEstreno,
        Integer duracionMinutos) {

}
