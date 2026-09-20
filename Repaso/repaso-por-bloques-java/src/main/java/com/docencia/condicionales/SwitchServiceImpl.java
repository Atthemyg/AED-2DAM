package com.docencia.condicionales;

public class SwitchServiceImpl implements SwitchService {

    @Override
    public String obtenerNombreDia(Integer numeroDia) {
        if (numeroDia == null || numeroDia < 1 || numeroDia > 7) {
            throw new IllegalArgumentException();
        }
        switch (numeroDia) {
            case 1:
                return "LUNES";
                break;
            case 2:
                return "MARTES";
                break;
            case 3:
                return "MIERCOLES";
                break;
            case 4:
                return "JUEVES";
                break;
            case 5:
                return "VIERNES";
                break;
            case 6:
                return "SABADO";
                break;
            case 7:
                return "DOMINGO";
                break;
            default:
                System.out.println("NUMERO DE LA SEMANA NO VALIDO");
                break;
        }
    }

    @Override
    public Double calcularDescuentoPorTipo(String tipoCliente, Double importe) {
        if (tipoCliente == null || tipoCliente.isBlank() || importe == null || importe < 0) {
            throw new IllegalArgumentException();
        }

        Double descuento = 0.0;

        switch (tipoCliente) {
            case "SOCIO":
                descuento = importe * 0.20;
                break;
            case "NO SOCIO":
                descuento = importe * 0.05;
                break;
            default:
                System.out.println("TIPO DE CLIENTE NO VALIDO");
                break;
        }
        return descuento;
    }

    @Override
    public String obtenerMensajeEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException();
        }

        switch (estado) {
            case "ACTIVO":
                return "USUARIO ACTIVO";
                break;
            default:
                System.out.println("USUARIO INACTIVO");
                break;
        }
    }

    @Override
    public Integer obtenerDiasDelMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException();
        }
        switch (mes) {
            case 2:
                return "28 DIAS";
                break;
            case 4, 6, 9, 11:
                return "30 DIAS";
            break;
            default:
                return "31 DIAS";
                break;
        }
    }

    @Override
    public String obtenerCategoriaProducto(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException();
        }

        switch (codigo) {
            case "A":
                return "ALUMNO";
                break;
            case "P":
                return "PROFESOR";
                break;
            default:
                return "DESCONOCIDO";
                break;
        }
    }
    
}
