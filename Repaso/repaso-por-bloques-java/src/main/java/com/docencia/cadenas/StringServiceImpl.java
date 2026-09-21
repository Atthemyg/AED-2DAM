package com.docencia.cadenas;

public class StringServiceImpl implements StringService {

    @Override
    public String normalizarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }
        texto = texto.replaceAll("\\s+", " ").trim().toLowerCase();

        return texto;
    }

    @Override
    public Boolean esPalindromo(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        String normalizado = texto.replaceAll("\\s+", "").toLowerCase();

        for (int i = 0; i < normalizado.length() / 2; i++) {
            if (normalizado.charAt(i) != normalizado.charAt(normalizado.length() - 1 - i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Integer contarVocales(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        Integer contador = 0;

        for (int i = 0; i < texto.length(); i++) {
            char c = Character.toLowerCase(texto.charAt(i));

            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                contador++;
            }
        }

        return contador;
    }

    @Override
    public String extraerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException();
        }
        String[] palabras = nombreCompleto.split(" ");
        String resultado = "";
        for (String palabra : palabras) {
            resultado += palabra.charAt(0);
        }
        return resultado.toUpperCase();
    }

    @Override
    public String invertirTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        String textoInvertido = "";

        for (int i = texto.length() - 1; i >= 0; i--) {
            textoInvertido += texto.charAt(i);
        }

        return textoInvertido;
    }

    @Override
    public Boolean contieneSoloLetras(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException();
        }

        String letras = "abcdeABCDE";

        for (int i = 0; i < texto.length(); i++) {
            if (letras.indexOf(texto.charAt(i)) == -1) { // si indexOf no encuentra algo devuelve -1, por eso el == -1
                return false;
            }
        }
        return true;
    }
    
}
