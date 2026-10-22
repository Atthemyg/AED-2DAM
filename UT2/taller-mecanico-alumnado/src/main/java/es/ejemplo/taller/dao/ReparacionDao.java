package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Reparacion;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.math.BigDecimal;

public class ReparacionDao extends ConexionBD {

    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "EN_PROCESO", "FINALIZADA");

    public ReparacionDao(String url) {
        super(url);
    }

    // Las horas se leen como texto para no pasar por double
    Reparacion mapToClass(ResultSet rs) throws SQLException {
        return new Reparacion(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
                rs.getString(5), new BigDecimal(rs.getString(6)), rs.getString(7));
    }

    public List<Reparacion> listar() {
        List<Reparacion> lista = new ArrayList<>();
        String query = "SELECT id_reparacion, matricula, id_mecanico, descripcion, "
                + "fecha, horas_trabajo, estado FROM reparaciones ORDER BY id_reparacion";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las reparaciones: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Reparacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, "
                + "fecha, horas_trabajo, estado FROM reparaciones WHERE id_reparacion = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar la reparación: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Devuelve el id generado, o 0 si se rechaza o falla
    public int insertar(Reparacion dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "INSERT INTO reparaciones (matricula, id_mecanico, descripcion, fecha, "
                + "horas_trabajo, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setString(5, dato.getHorasTrabajo().toPlainString());
            ps.setString(6, dato.getEstado());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la reparación: " + e.getMessage());
        }
        return 0;
    }

    public int actualizar(Reparacion dato) {
        if (!esValido(dato) || dato.getIdReparacion() == null) {
            return 0;
        }
        String sql = "UPDATE reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, "
                + "fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setString(5, dato.getHorasTrabajo().toPlainString());
            ps.setString(6, dato.getEstado());
            ps.setInt(7, dato.getIdReparacion());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar la reparación: " + e.getMessage());
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM reparaciones WHERE id_reparacion = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar la reparación: " + e.getMessage());
            return 0;
        }
    }

    public List<Reparacion> buscarPorVehiculo(String valor) {
        List<Reparacion> lista = new ArrayList<>();
        if (valor == null || valor.isBlank()) {
            return lista;
        }
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, "
                + "fecha, horas_trabajo, estado FROM reparaciones WHERE matricula = ? ORDER BY id_reparacion";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las reparaciones del vehículo: " + e.getMessage());
        }
        return lista;
    }

    public List<Reparacion> buscarPorMecanico(Integer valor) {
        List<Reparacion> lista = new ArrayList<>();
        if (valor == null) {
            return lista;
        }
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, "
                + "fecha, horas_trabajo, estado FROM reparaciones WHERE id_mecanico = ? ORDER BY id_reparacion";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las reparaciones del mecánico: " + e.getMessage());
        }
        return lista;
    }

    public List<Reparacion> listarPendientes() {
        List<Reparacion> lista = new ArrayList<>();
        String sql = "SELECT id_reparacion, matricula, id_mecanico, descripcion, "
                + "fecha, horas_trabajo, estado FROM reparaciones WHERE estado = 'PENDIENTE' ORDER BY fecha, id_reparacion";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las reparaciones pendientes: " + e.getMessage());
        }
        return lista;
    }

    private boolean esValido(Reparacion r) {
        if (r == null) {
            return false;
        }
        if (r.getMatricula() == null || r.getMatricula().isBlank()) {
            return false;
        }
        if (r.getIdMecanico() == null) {
            return false;
        }
        if (r.getDescripcion() == null || r.getDescripcion().isBlank()) {
            return false;
        }
        if (!fechaValida(r.getFecha())) {
            return false;
        }
        if (r.getHorasTrabajo() == null || r.getHorasTrabajo().signum() < 0) {
            return false;
        }
        return r.getEstado() != null && ESTADOS.contains(r.getEstado());
    }

    // Formato AAAA-MM-DD y fecha real (no acepta 2026-02-30)
    private boolean fechaValida(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}