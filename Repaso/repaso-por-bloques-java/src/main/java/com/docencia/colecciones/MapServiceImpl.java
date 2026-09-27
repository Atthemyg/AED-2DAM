package com.docencia.colecciones;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapServiceImpl implements MapService {

    @Override
    public Map<String, Integer> contarFrecuenciaPalabras(List<String> palabras) {
        if (palabras == null) {
            throw new IllegalArgumentException();
        }

        Map<String, Integer> resultado = new HashMap<>();

        for (String palabra : palabras) {
            Integer cantidad = resultado.get(palabra);

            if (cantidad == null) {
                resultado.put(palabra, 1);
            } else {
                resultado.put(palabra, cantidad + 1);
            }
        }
        return resultado;
    }

    @Override
    public Integer obtenerValorPorClave(Map<String, Integer> mapa, String clave) {
        if (mapa == null || clave == null || clave.isBlank()) {
            throw new IllegalArgumentException();
        }

        Integer valor = mapa.get(clave);

        if (valor == null) {
            return 0;
        }

        return valor;
    }

    @Override
    public Map<String, Double> calcularMediaPorCategoria(Map<String, List<Integer>> datos) {
        if (datos == null) {
            throw new IllegalArgumentException();
        }

        Map<String, Double> resultado = new HashMap<>();

        for (String categoria : datos.keySet()) {
            List<Integer> numeros = datos.get(categoria);

            int suma = 0;

            for (Integer numero : numeros) {
                suma += numero;
            }

            double media = (double) suma / numeros.size();

            resultado.put(categoria, media);
        }
        return resultado;
    }

    @Override
    public String obtenerClaveConMayorValor(Map<String, Integer> mapa) {
        if (mapa == null || mapa.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String claveMayor = null;
        Integer valorMayor = null;

        for (String clave : mapa.keySet()) {
            Integer valor = mapa.get(clave);

            if (valorMayor == null || valor > valorMayor) {
                valorMayor = valor;
                claveMayor = clave;
            }
        }

        return claveMayor;
    }

    @Override
    public Map<String, Integer> filtrarPorValorMinimo(Map<String, Integer> mapa, Integer minimo) {
        if (mapa == null || minimo == null) {
            throw new IllegalArgumentException();
        }

        Map<String, Integer> resultado = new HashMap<>();

        for (String clave : mapa.keySet()) {
            Integer valor = mapa.get(clave);

            if (valor >= minimo) {
                resultado.put(clave, valor);
            }
        }

        return resultado;
    }

}
