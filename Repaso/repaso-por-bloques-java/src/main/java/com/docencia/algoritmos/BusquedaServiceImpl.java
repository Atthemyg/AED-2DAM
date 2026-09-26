package com.docencia.algoritmos;

import java.util.Collections;
import java.util.List;

public class BusquedaServiceImpl implements BusquedaService {

    @Override
    public Integer buscarIndiceElemento(List<Integer> numeros, Integer valor) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return numeros.indexOf(valor);
    }

    @Override
    public Boolean existePalabra(List<String> palabras, String palabra) {
        if (palabras == null || palabras.isEmpty() || palabra == null) {
            return false;
        }

        for (String string : palabras) {
            if (string.equals(palabra)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer encontrarMaximo(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Integer maximo = numeros.get(0);
        for (Integer integer : numeros) {
            if (integer > maximo) {
                maximo = integer;
            }
        }
        return maximo;
    }

    @Override
    public Integer encontrarMinimo(List<Integer> numeros) {
        Integer minimo = numeros.get(0);
        for (Integer integer : numeros) {
            if (integer < minimo) {
                minimo = integer;
            }
        }
        return minimo;
    }

    @Override
    public Integer contarApariciones(List<Integer> numeros, Integer valor) {
        Integer contador = 0;
        for (Integer integer : numeros) {
            if (integer.equals(valor)) {
                contador++;
            }
        }
        return contador;
    }

}
