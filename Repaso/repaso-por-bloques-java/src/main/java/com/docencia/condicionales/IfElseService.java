package com.docencia.condicionales;

public interface IfElseService {
    /**
     * Funcion que realiza la clasificacion de la edad
     * @param edad entero
     * @return valor de la edad
     * */
    String clasificarEdad(Integer edad);
    String evaluarNota(Integer nota);
    Boolean puedeAcceder(Boolean activo, Integer edad);
    String clasificarTemperatura(Double temperatura);
    String calcularResultadoComparacion(Integer primero, Integer segundo);
}
