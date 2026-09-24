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
            case 2:
                return "MARTES";
            case 3:
                return "MIERCOLES";
            case 4:
                return "JUEVES";
            case 5:
                return "VIERNES";
            case 6:
                return "SABADO";
            case 7:
                return "DOMINGO";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Double calcularDescuentoPorTipo(String tipoCliente, Double importe) {
        if (tipoCliente == null || tipoCliente.isBlank() || importe == null || importe < 0) {
            throw new IllegalArgumentException();
        }

        Double descuento = 0.0;

        switch (tipoCliente) {
            case "VIP":
                descuento = importe * 0.20;
                break;
            case "NORMAL":
                descuento = 0.0;
                break;
            default:
                throw new IllegalArgumentException();
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
                return "Elemento activo";
            default:
                return "Elemento inactivo";
            }
    }

    @Override
    public Integer obtenerDiasDelMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException();
        }

        switch (mes) {
            case 2:
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
            }
    }

    @Override
    public String obtenerCategoriaProducto(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException();
        }

        switch (codigo) {
            case "T001":
                return "TECNOLOGIA";
            default:
                return "DESCONOCIDO";
        }
    }
}
