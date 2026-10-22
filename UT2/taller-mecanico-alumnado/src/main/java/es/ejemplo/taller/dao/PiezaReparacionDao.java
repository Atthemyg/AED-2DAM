package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.PiezaReparacion;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class PiezaReparacionDao extends ConexionBD {

    public PiezaReparacionDao(String url) {
        super(url);
    }

    PiezaReparacion mapToClass(ResultSet rs) throws SQLException {
        return new PiezaReparacion(rs.getInt(1), rs.getInt(2), rs.getString(3),
                rs.getInt(4), BigDecimal.valueOf(rs.getDouble(5)));
    }

    public List<PiezaReparacion> listar() {
        List<PiezaReparacion> lista = new ArrayList<>();
        String query = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario "
                + "FROM piezas_reparacion ORDER BY id_pieza";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las piezas: " + e.getMessage());
        }
        return lista;
    }

    public Optional<PiezaReparacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario "
                + "FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar la pieza: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Devuelve el id generado, o 0 si se rechaza o falla
    public int insertar(PiezaReparacion dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "INSERT INTO piezas_reparacion (id_reparacion, nombre, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setDouble(4, dato.getPrecioUnitario().doubleValue());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la pieza: " + e.getMessage());
        }
        return 0;
    }

    public int actualizar(PiezaReparacion dato) {
        if (!esValido(dato) || dato.getIdPieza() == null) {
            return 0;
        }
        String sql = "UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, "
                + "precio_unitario = ? WHERE id_pieza = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setDouble(4, dato.getPrecioUnitario().doubleValue());
            ps.setInt(5, dato.getIdPieza());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar la pieza: " + e.getMessage());
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar la pieza: " + e.getMessage());
            return 0;
        }
    }

    public List<PiezaReparacion> listarPorReparacion(Integer id) {
        List<PiezaReparacion> lista = new ArrayList<>();
        if (id == null) {
            return lista;
        }
        String sql = "SELECT id_pieza, id_reparacion, nombre, cantidad, precio_unitario "
                + "FROM piezas_reparacion WHERE id_reparacion = ? ORDER BY id_pieza";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las piezas de la reparación: " + e.getMessage());
        }
        return lista;
    }

    public BigDecimal calcularCostePiezas(Integer id) {
        BigDecimal total = BigDecimal.ZERO;
        for (PiezaReparacion pieza : listarPorReparacion(id)) {
            BigDecimal importe = pieza.getPrecioUnitario().multiply(BigDecimal.valueOf(pieza.getCantidad()));
            total = total.add(importe);
        }
        return total;
    }

    private boolean esValido(PiezaReparacion p) {
        if (p == null) {
            return false;
        }
        if (p.getIdReparacion() == null) {
            return false;
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            return false;
        }
        if (p.getCantidad() == null || p.getPrecioUnitario() == null) {
            return false;
        }
        return true;
    }
}