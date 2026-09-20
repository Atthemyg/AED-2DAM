package com.docencia.condicionales;

public class IfElseServiceImpl implements IfElseService {

    @Override
    public String clasificarEdad(Integer edad) {
        if (edad == null || edad < 0) {
            throw new IllegalArgumentException();
        }
        if (edad < 18) {
            return "MENOR";
        }
        return "ADULTO";
    }

    @Override
    public String evaluarNota(Integer nota) {
        if (nota == null || nota < 0 || nota > 10) {
            throw new IllegalArgumentException();
        }
        if (nota < 5) {
            return "SUSPENSO";
        }
        return "APROBADO";
    }

    @Override
    public Boolean puedeAcceder(Boolean activo, Integer edad) {
        if (edad == null || activo == null || edad < 0) {
            throw new IllegalArgumentException();
        }
        if (edad < 18 || activo == false) {
            return false;
        }
        return true;
    }

    @Override
    public String clasificarTemperatura(Double temperatura) {
        if (temperatura == null) {
            throw new IllegalArgumentException();
        }
        if (temperatura >= 30) {
            return "CALOR";
        } else if (temperatura > 15 && temperatura < 30) {
            return "TEMPLADO";
        }
        return "FRIO";
    }

    @Override
    public String calcularResultadoComparacion(Integer primero, Integer segundo) {
        if (primero == null || segundo == null) {
            throw new IllegalArgumentException();
        }
        if (primero.equals(segundo)){
            return "IGUALES";
        }
        return "DIFERENTES";
        }
    }
    
}
