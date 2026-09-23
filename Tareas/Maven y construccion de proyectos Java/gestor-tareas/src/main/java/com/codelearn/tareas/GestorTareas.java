package com.codelearn.tareas;

import java.util.ArrayList;
import java.util.List;

public class GestorTareas {
    private final List<String> titulos = new ArrayList<>();

    public void anadir(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        titulos.add(titulo);
    }

    /*public void anadir(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        titulos.add(titulo);
    }*/

    public List<String> listar() {
        return List.copyOf(titulos);
    }
}
