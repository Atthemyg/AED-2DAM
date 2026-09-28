package com.docencia.fechas;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class LocalDateServiceImpl implements LocalDateService {

    @Override
    public Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException();
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public Boolean esFechaFutura(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException();
        }
        return fecha.isAfter(LocalDate.now());
    }

    @Override
    public Long calcularDiasEntreFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException();
        }
        return (long) Period.between(inicio, fin).getDays();
    }

    @Override
    public LocalDate sumarDias(LocalDate fecha, Integer dias) {
        if (fecha == null || dias == null) {
            throw new IllegalArgumentException();
        }
        return fecha.plusDays(dias);
    }

    @Override
    public Boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException();
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() > 18;
    }

}
