package com.docencia.herencia;

import java.util.Objects;

public class Alumno extends Persona {
    private final String expediente;

    public Alumno(String nombre, String apellidos, Integer edad, String expediente) {
        super(nombre, apellidos, edad);
        this.expediente = expediente;
    }

    public String getExpediente() { return expediente; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Alumno alumno = (Alumno) o;
        return Objects.equals(expediente, alumno.expediente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expediente);
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "expediente='" + expediente + '\'' +
                '}';
    }
}
