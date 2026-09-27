package com.docencia.colecciones;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ListServiceImpl implements ListService {

    @Override
    public List<String> filtrarPalabrasPorLongitud(List<String> palabras, Integer longitudMinima) {
        if (palabras == null || longitudMinima == null || longitudMinima <= 0){
            throw new IllegalArgumentException();
        }
        List<String> resultado = new ArrayList<>();
        if (palabras.isEmpty()){
            return resultado;
        }
        for (String palabra : palabras) {
            if (palabra.length() >= longitudMinima){
                resultado.add(palabra);
            }
        }
        return resultado;
    }

    @Override
    public List<Integer> ordenarNumerosAscendente(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>(numeros);
        resultado.sort(null);
        return resultado;
    }

    @Override
    public Integer sumarElementosLista(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        Integer resultado = 0;
        for (Integer numero : numeros) {
            resultado += numero;
        }
        return resultado;
    }

    @Override
    public Double calcularMediaLista(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        Double suma = 0.0;
        Double contador = 0.0;
        for (Integer numero : numeros) {
            suma += numero;
            contador++;
        }
        if (contador == 0){
            return suma;
        }
        return suma/contador;
    }

    @Override
    public List<Integer> eliminarNumerosDuplicados(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()){
            throw new IllegalArgumentException();
        }
        List<Integer> resultado = new ArrayList<>();
        for (Integer numero : numeros) {
            if (!resultado.contains(numero))
                resultado.add(numero);
        }
        return resultado;
    }

}
