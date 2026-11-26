package ies.puesto.peliculas.model.error;

public class Conflicto extends RuntimeException {
    public Conflicto(String mensaje) {
        super(mensaje);
    }
}
