package com.docencia.regex;

public class RegexServiceImpl implements RegexService {

    @Override
    public Boolean validarDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }
        return dni.matches("^[0-9]{8}[A-Z]$");
    }

    @Override
    public Boolean validarEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[a-z]+@[a-z]+\\.[a-z]+$");
    }

    @Override
    public Boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return false;
        }
        return telefono.matches("^[6][0-9]{8}$");
    }

    @Override
    public Boolean validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        return nombre.matches("^[A-ZÁÉÍÓÚ][a-záéíóú]+ [A-ZÁÉÍÓÚ][a-záéíóú]+$");
    }

    @Override
    public Boolean validarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isBlank()) {
            return false;
        }
        return codigoPostal.matches("^[0-9]{5}$");
    }

    @Override
    public Boolean validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return false;
        }
        return matricula.matches("[0-9]{4}[A-Z]{3}");
    }
    
}
