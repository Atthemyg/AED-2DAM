package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class MecanicoDao extends ConexionBD {

    public MecanicoDao(String url) {
        super(url);
    }

    Mecanico mapToClass(ResultSet rs) throws SQLException {
        return new Mecanico(rs.getInt(1), rs.getString(2),
                rs.getString(3), BigDecimal.valueOf(rs.getDouble(4)));
    }

    public List<Mecanico> listar() {
        List<Mecanico> lista = new ArrayList<>();
        String query = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos ORDER BY id_mecanico";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los mecánicos: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Mecanico> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos WHERE id_mecanico = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el mecánico: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Devuelve el id generado, o 0 si se rechaza o falla
    public int insertar(Mecanico dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "INSERT INTO mecanicos (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setDouble(3, dato.getTarifaHora().doubleValue());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el mecánico: " + e.getMessage());
        }
        return 0;
    }

    public int actualizar(Mecanico dato) {
        if (!esValido(dato) || dato.getIdMecanico() == null) {
            return 0;
        }
        String sql = "UPDATE mecanicos SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setDouble(3, dato.getTarifaHora().doubleValue());
            ps.setInt(4, dato.getIdMecanico());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el mecánico: " + e.getMessage());
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM mecanicos WHERE id_mecanico = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar el mecánico: " + e.getMessage());
            return 0;
        }
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        List<Mecanico> lista = new ArrayList<>();
        if (especialidad == null || especialidad.isBlank()) {
            return lista;
        }
        String sql = "SELECT id_mecanico, nombre, especialidad, tarifa_hora FROM mecanicos "
                + "WHERE especialidad = ? ORDER BY tarifa_hora";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar mecánicos por especialidad: " + e.getMessage());
        }
        return lista;
    }

    private boolean esValido(Mecanico m) {
        if (m == null) {
            return false;
        }
        if (m.getNombre() == null || m.getNombre().isBlank()) {
            return false;
        }
        if (m.getEspecialidad() == null || m.getEspecialidad().isBlank()) {
            return false;
        }
        if (m.getTarifaHora() == null) {
            return false;
        }
        return true;
    }
}