package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Veterinario;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class VeterinarioDao extends ConexionBD implements IVeterinarioDao {

    public VeterinarioDao(String url) {
        super(url);
    }

    // La tarifa se lee como texto para no pasar por double
    Veterinario mapToClass(ResultSet rs) throws SQLException {
        return new Veterinario(rs.getInt(1), rs.getString(2),
                rs.getString(3), new BigDecimal(rs.getString(4)));
    }

    @Override
    public List<Veterinario> listar() {
        List<Veterinario> lista = new ArrayList<>();
        String query = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar los veterinarios", e);
        }
        return lista;
    }

    @Override
    public Optional<Veterinario> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el veterinario", e);
        }
        return Optional.empty();
    }

    // Devuelve el id generado por SQLite
    @Override
    public int insertar(Veterinario dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El veterinario no puede ser nulo");
        }
        String sql = "INSERT INTO veterinarios (nombre, especialidad, tarifa_hora) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al insertar el veterinario", e);
        }
        return 0;
    }

    @Override
    public int actualizar(Veterinario dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El veterinario no puede ser nulo");
        }
        if (dato.getId() == null) {
            throw new IllegalArgumentException("Para actualizar hace falta el identificador");
        }
        String sql = "UPDATE veterinarios SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4, dato.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el veterinario", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }
        String sql = "DELETE FROM veterinarios WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el veterinario", e);
        }
    }

    @Override
    public List<Veterinario> buscarPorEspecialidad(String valor) {
        List<Veterinario> lista = new ArrayList<>();
        if (valor == null || valor.isBlank()) {
            return lista;
        }
        String sql = "SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios "
                + "WHERE especialidad = ? ORDER BY tarifa_hora, id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar los veterinarios de la especialidad", e);
        }
        return lista;
    }
}