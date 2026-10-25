package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends ConexionBD implements IClienteDao {

    public ClienteDao(String url) {
        super(url);
    }

    Cliente mapToClass(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt(1), rs.getString(2),
                rs.getString(3), rs.getString(4));
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT id, nombre, email, telefono FROM clientes ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar los clientes", e);
        }
        return lista;
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, nombre, email, telefono FROM clientes WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el cliente", e);
        }
        return Optional.empty();
    }

    // Devuelve el id generado por SQLite
    @Override
    public int insertar(Cliente dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
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
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al insertar el cliente", e);
        }
        return 0;
    }

    @Override
    public int actualizar(Cliente dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (dato.getId() == null) {
            throw new IllegalArgumentException("Para actualizar hace falta el identificador");
        }
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el cliente", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el cliente", e);
        }
    }
}