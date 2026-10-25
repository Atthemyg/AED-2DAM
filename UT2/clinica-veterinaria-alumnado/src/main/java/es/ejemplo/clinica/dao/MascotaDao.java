package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Mascota;
import java.sql.*;
import java.util.*;

public class MascotaDao extends ConexionBD implements IMascotaDao {

    public MascotaDao(String url) {
        super(url);
    }

    Mascota mapToClass(ResultSet rs) throws SQLException {
        return new Mascota(rs.getInt(1), rs.getString(2), rs.getString(3),
                rs.getString(4), rs.getString(5), rs.getInt(6));
    }

    @Override
    public List<Mascota> listar() {
        List<Mascota> lista = new ArrayList<>();
        String query = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
                + "FROM mascotas ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las mascotas", e);
        }
        return lista;
    }

    @Override
    public Optional<Mascota> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
                + "FROM mascotas WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la mascota", e);
        }
        return Optional.empty();
    }

    // Devuelve el id generado por SQLite
    @Override
    public int insertar(Mascota dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }
        String sql = "INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, id_cliente) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecie());
            ps.setString(3, dato.getRaza());
            ps.setString(4, dato.getFechaNacimiento());
            ps.setObject(5, dato.getIdCliente());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al insertar la mascota", e);
        }
        return 0;
    }

    @Override
    public int actualizar(Mascota dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }
        if (dato.getId() == null) {
            throw new IllegalArgumentException("Para actualizar hace falta el identificador");
        }
        String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, "
                + "fecha_nacimiento = ?, id_cliente = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecie());
            ps.setString(3, dato.getRaza());
            ps.setString(4, dato.getFechaNacimiento());
            ps.setObject(5, dato.getIdCliente());
            ps.setInt(6, dato.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        if (id == null) {
            throw new NullPointerException("El identificador no puede ser nulo");
        }
        String sql = "DELETE FROM mascotas WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la mascota", e);
        }
    }

    @Override
    public List<Mascota> buscarPorCliente(Integer valor) {
        List<Mascota> lista = new ArrayList<>();
        if (valor == null) {
            return lista;
        }
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
                + "FROM mascotas WHERE id_cliente = ? ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las mascotas del cliente", e);
        }
        return lista;
    }

    @Override
    public List<Mascota> listarPorEspecie(String valor) {
        List<Mascota> lista = new ArrayList<>();
        if (valor == null || valor.isBlank()) {
            return lista;
        }
        String sql = "SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente "
                + "FROM mascotas WHERE especie = ? ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar las mascotas de la especie", e);
        }
        return lista;
    }
}