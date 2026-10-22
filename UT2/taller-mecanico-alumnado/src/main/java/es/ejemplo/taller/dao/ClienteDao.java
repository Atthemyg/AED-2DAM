package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends ConexionBD {

    public ClienteDao(String url) {
        super(url);
    }

    Cliente mapToClass(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt(1), rs.getString(2),
                rs.getString(3), rs.getString(4));
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT id_cliente, nombre, email, telefono FROM clientes ORDER BY id_cliente";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los clientes: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_cliente, nombre, email, telefono FROM clientes WHERE id_cliente = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el cliente: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Devuelve el id generado, o 0 si se rechaza o falla
    public int insertar(Cliente dato) {
        if (!esValido(dato)) {
            return 0;
        }
        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el cliente: " + e.getMessage());
        }
        return 0;
    }

    public int actualizar(Cliente dato) {
        if (!esValido(dato) || dato.getIdCliente() == null) {
            return 0;
        }
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getIdCliente());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el cliente: " + e.getMessage());
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar el cliente: " + e.getMessage());
            return 0;
        }
    }

    private boolean esValido(Cliente c) {
        if (c == null) {
            return false;
        }
        if (c.getNombre() == null || c.getNombre().isBlank()) {
            return false;
        }
        if (c.getEmail() == null || c.getEmail().isBlank()) {
            return false;
        }
        return true;
    }
}