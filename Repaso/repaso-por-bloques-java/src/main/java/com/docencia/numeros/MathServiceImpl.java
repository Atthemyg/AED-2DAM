package com.docencia.numeros;

public class MathServiceImpl implements MathService {

    @Override
    public Double calcularAreaCirculo(Double radio) {
        if (radio == null || radio <= 0) {
            throw new IllegalArgumentException();
        }
        return radio * 2 * Math.PI;
    }

    @Override
    public Integer calcularPotencia(Integer base, Integer exponente) {
        if (base == null || exponente == null) {
            throw new IllegalArgumentException();
        }
        return (int) Math.pow(base, exponente);
    }

    @Override
    public Integer redondearHaciaArriba(Double numero) {
        if (numero == null || numero == 0) {
            throw new IllegalArgumentException();
        }
        return (int) Math.ceil(numero);
    }

    @Override
    public Double calcularRaizCuadrada(Double numero) {
        if (numero == null || numero == 0) {
            throw new IllegalArgumentException();
        }
        return Math.sqrt(numero);
    }

    @Override
    public Integer obtenerValorAbsoluto(Integer numero) {
        if (numero == null || numero == 0) {
            throw new IllegalArgumentException();
        }
        return Math.abs(numero);
    }
    
}
