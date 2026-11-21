package ies.puesto.peliculas.controller.api;

// Respuesta de GET, POST y PUT
public record PeliculaResponse(String codigo, String titulo, int anioEstreno, int duracionMinutos) {

}
