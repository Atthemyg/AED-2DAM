package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Consulta;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ConsultaDao extends ConexionBD implements IConsultaDao {

    public ConsultaDao(String url) {
        super(url);
    }

    // Las horas se leen como texto para no pasar por double
    Consulta mapToClass(ResultSet rs) throws SQLException {
        return new Consulta(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),
                new BigDecimal(rs.getString(5)), rs.getString(6));
    }

    @Override
    public List<Consulta> listar() {
        List<Consulta> lista = new ArrayList<>();
        String query = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
                + "FROM consultas ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las consultas", e);
        }
        return lista;
    }

    @Override
    public Optional<Consulta> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
                + "FROM consultas WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la consulta", e);
        }
        return Optional.empty();
    }

    // Devuelve el id generado por SQLite
    @Override
    public int insertar(Consulta dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La consulta no puede ser nula");
        }
        String sql = "INSERT INTO consultas (id_mascota, id_veterinario, fecha, horas, estado) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, dato.getIdMascota());
            ps.setObject(2, dato.getIdVeterinario());
            ps.setString(3, dato.getFecha());
            ps.setBigDecimal(4, dato.getHoras());
            ps.setString(5, dato.getEstado());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al insertar la consulta", e);
        }
        return 0;
    }

    @Override
    public int actualizar(Consulta dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La consulta no puede ser nula");
        }
        if (dato.getId() == null) {
            throw new IllegalArgumentException("Para actualizar hace falta el identificador");
        }
        String sql = "UPDATE consultas SET id_mascota = ?, id_veterinario = ?, fecha = ?, "
                + "horas = ?, estado = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, dato.getIdMascota());
            ps.setObject(2, dato.getIdVeterinario());
            ps.setString(3, dato.getFecha());
            ps.setBigDecimal(4, dato.getHoras());
            ps.setString(5, dato.getEstado());
            ps.setInt(6, dato.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la consulta", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }
        String sql = "DELETE FROM consultas WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la consulta", e);
        }
    }

    @Override
    public List<Consulta> buscarPorMascota(Integer valor) {
        List<Consulta> lista = new ArrayList<>();
        if (valor == null) {
            return lista;
        }
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
                + "FROM consultas WHERE id_mascota = ? ORDER BY fecha, id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las consultas de la mascota", e);
        }
        return lista;
    }

    @Override
    public List<Consulta> buscarPorVeterinario(Integer valor) {
        List<Consulta> lista = new ArrayList<>();
        if (valor == null) {
            return lista;
        }
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
                + "FROM consultas WHERE id_veterinario = ? ORDER BY fecha, id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las consultas del veterinario", e);
        }
        return lista;
    }

    @Override
    public List<Consulta> listarProgramadas() {
        List<Consulta> lista = new ArrayList<>();
        String sql = "SELECT id, id_mascota, id_veterinario, fecha, horas, estado "
                + "FROM consultas WHERE estado = 'PROGRAMADA' ORDER BY fecha, id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las consultas programadas", e);
        }
        return lista;
    }
}