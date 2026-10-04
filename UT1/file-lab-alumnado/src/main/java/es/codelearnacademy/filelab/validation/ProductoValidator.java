package es.codelearnacademy.filelab.validation;

import es.codelearnacademy.filelab.model.Producto;

public final class ProductoValidator {

    private ProductoValidator() {
    }

    // Como producto es record usamos producto.id()...
    public static void validar(Producto producto) {
        if (producto == null || producto.id() <= 0 || producto.nombre() == null
        || producto.nombre().isBlank() || producto.precio() <= 0 || producto.stock() <= 0) {
            throw new IllegalArgumentException();
        } // void no devuelve ningun valor
    }
}
