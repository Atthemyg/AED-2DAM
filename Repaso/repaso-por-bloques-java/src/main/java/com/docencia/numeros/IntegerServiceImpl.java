package com.docencia.numeros;

public class IntegerServiceImpl implements IntegerService {

    @Override
    public Boolean esPar(Integer numero) {
        if (numero == null) {
            throw new IllegalArgumentException();
        }
        return numero % 2 == 0;
    }

    @Override
    public Integer sumarDigitos(Integer numero) {
        if (numero == null) {
            throw new IllegalArgumentException();
        }
        int suma = 0;
        String texto = numero.toString();

        for (int i = 0; i < texto.length(); i++) {
            suma += Integer.parseInt(String.valueOf(texto.charAt(i)));
        }
        return suma;
    }

    @Override
    public Integer convertirTextoAEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }
        return Integer.parseInt(texto);
    }

    @Override
    public Boolean esNumeroPrimo(Integer numero) { // solo se puede dividir entre 1 y el mismo
        if (numero == null || numero < 2) { // los numeros primos empiezan en 2
            throw new IllegalArgumentException();
        }
        for (int i = 2; i < numero; i++) {
            if (numero % i == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer calcularFactorial(Integer numero) {
        if (numero == null || numero < 0) {
            throw new IllegalArgumentException();
        }
        Integer resultado = 1;  // siempre empezar en 1

        for (int i = 1; i <= numero; i++) {
            resultado = resultado * i;
        }
        return resultado;

        /* si numero = 5 hace:
          i = 1 → resultado = 1 × 1 = 1
          i = 2 → resultado = 1 × 2 = 2
          i = 3 → resultado = 2 × 3 = 6
          i = 4 → resultado = 6 × 4 = 24
          i = 5 → resultado = 24 × 5 = 120 */
    }
}
