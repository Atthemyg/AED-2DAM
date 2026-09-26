package com.docencia.algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdenacionServiceImpl implements OrdenacionService {

    @Override
    public List<Integer> ordenarBurbujaAscendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        // [5, 2, 8, 1]
        for (int i = 0; i < numeros.size() - 1; i++) {
            for (int j = 0; j < numeros.size() - 1 - i; j++) {
                if (numeros.get(j) > numeros.get(j + 1)) {
                    Integer temporal = numeros.get(j);
                    numeros.set(j, numeros.get(j + 1));
                    numeros.set(j + 1, temporal);
                }
            }
        }
        return numeros;
    }

    @Override
    public List<Integer> ordenarBurbujaDescendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < numeros.size() - 1; i++) {
            for (int j = 0; j < numeros.size() - 1 - i; j++) {
                if (numeros.get(j) < numeros.get(j + 1)) {
                    Integer temporal = numeros.get(j);
                    numeros.set(j, numeros.get(j + 1));
                    numeros.set(j + 1, temporal);
                }
            }
        }
        return numeros;
    }

    @Override
    public List<String> ordenarPalabrasAlfabeticamente(List<String> palabras) {
        if (palabras == null || palabras.isEmpty()) {
            throw new IllegalArgumentException();
        }

        palabras.sort(null);

        return palabras;
    }

    @Override
    public List<Integer> invertirLista(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>();

        for (int i = numeros.size() - 1; i >= 0; i--) {
            resultado.add(numeros.get(i));
        }

        return resultado;
    }

    @Override
    public Boolean estaOrdenadaAscendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i) > numeros.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
    
}
