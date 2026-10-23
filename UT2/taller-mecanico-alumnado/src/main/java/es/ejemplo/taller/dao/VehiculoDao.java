package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends ConexionBD {

    public VehiculoDao(String url) {
        super(url);
    }

    Vehiculo mapToClass(ResultSet rs) throws SQLException {
        return new Vehiculo(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getInt(5));
    }

    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos ORDER BY matricula";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los vehículos: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Vehiculo> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente FROM vehiculos WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el vehículo: " + e.getMessage());
        }
        return Optional.empty();
    }

    public int insertar(Vehiculo dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "INSERT INTO vehiculos (matricula, marca, modelo, anio, id_cliente) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar el vehículo: " + e.getMessage());
            return 0;
        }
    }

    public int actualizar(Vehiculo dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? "
                + "WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getMarca());
            ps.setString(2, dato.getModelo());
            ps.setInt(3, dato.getAnio());
            ps.setInt(4, dato.getIdCliente());
            ps.setString(5, dato.getMatricula());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el vehículo: " + e.getMessage());
            return 0;
        }
    }

    public int eliminar(String id) {
        if (id == null || id.isBlank()) {
            return 0;
        }
        String sql = "DELETE FROM vehiculos WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar el vehículo: " + e.getMessage());
            return 0;
        }
    }

    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        List<Vehiculo> lista = new ArrayList<>();
        if (idCliente == null) {
            return lista;
        }
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente "
                + "FROM vehiculos WHERE id_cliente = ? ORDER BY matricula";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los vehículos del cliente: " + e.getMessage());
        }
        return lista;
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        List<Vehiculo> lista = new ArrayList<>();
        if (marca == null || marca.isBlank()) {
            return lista;
        }
        String sql = "SELECT matricula, marca, modelo, anio, id_cliente "
                + "FROM vehiculos WHERE marca = ? ORDER BY matricula";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, marca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los vehículos por marca: " + e.getMessage());
        }
        return lista;
    }

    private boolean esValido(Vehiculo v) {
        if (v == null) {
            return false;
        }
        if (v.getMatricula() == null || v.getMatricula().isBlank()) {
            return false;
        }
        if (v.getMarca() == null || v.getMarca().isBlank()) {
            return false;
        }
        if (v.getModelo() == null || v.getModelo().isBlank()) {
            return false;
        }
        if (v.getAnio() == null || v.getIdCliente() == null) {
            return false;
        }
        return true;
    }
}