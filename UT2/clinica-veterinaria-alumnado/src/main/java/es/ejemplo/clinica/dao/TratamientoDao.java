package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.infra.ConexionBD;
import es.ejemplo.clinica.modelo.Tratamiento;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class TratamientoDao extends ConexionBD implements ITratamientoDao {

    public TratamientoDao(String url) {
        super(url);
    }

    // El precio se lee como texto para no pasar por double
    Tratamiento mapToClass(ResultSet rs) throws SQLException {
        return new Tratamiento(rs.getInt(1), rs.getInt(2), rs.getString(3),
                rs.getInt(4), new BigDecimal(rs.getString(5)));
    }

    @Override
    public List<Tratamiento> listar() {
        List<Tratamiento> lista = new ArrayList<>();
        String query = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario "
                + "FROM tratamientos ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar los tratamientos", e);
        }
        return lista;
    }

    @Override
    public Optional<Tratamiento> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario "
                + "FROM tratamientos WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el tratamiento", e);
        }
        return Optional.empty();
    }

    // Devuelve el id generado por SQLite
    @Override
    public int insertar(Tratamiento dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El tratamiento no puede ser nulo");
        }
        String sql = "INSERT INTO tratamientos (id_consulta, descripcion, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, dato.getIdConsulta());
            ps.setString(2, dato.getDescripcion());
            ps.setObject(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            if (ps.executeUpdate() > 0) {
                try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
                     ResultSet rs = id.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al insertar el tratamiento", e);
        }
        return 0;
    }

    @Override
    public int actualizar(Tratamiento dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El tratamiento no puede ser nulo");
        }
        if (dato.getId() == null) {
            throw new IllegalArgumentException("Para actualizar hace falta el identificador");
        }
        String sql = "UPDATE tratamientos SET id_consulta = ?, descripcion = ?, cantidad = ?, "
                + "precio_unitario = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, dato.getIdConsulta());
            ps.setString(2, dato.getDescripcion());
            ps.setObject(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            ps.setInt(5, dato.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el tratamiento", e);
        }
    }

    @Override
    public int eliminar(Integer id) {
        Objects.requireNonNull(id, "El identificador no puede ser nulo");
        String sql = "DELETE FROM tratamientos WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el tratamiento", e);
        }
    }

    @Override
    public List<Tratamiento> listarPorConsulta(Integer id) {
        List<Tratamiento> lista = new ArrayList<>();
        if (id == null) {
            return lista;
        }
        String sql = "SELECT id, id_consulta, descripcion, cantidad, precio_unitario "
                + "FROM tratamientos WHERE id_consulta = ? ORDER BY id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapToClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar los tratamientos de la consulta", e);
        }
        return lista;
    }

    @Override
    public BigDecimal calcularCosteTratamientos(Integer id) {
        BigDecimal total = BigDecimal.ZERO;
        for (Tratamiento tratamiento : listarPorConsulta(id)) {
            BigDecimal importe = tratamiento.getPrecioUnitario().multiply(BigDecimal.valueOf(tratamiento.getCantidad()));
            total = total.add(importe);
        }
        return total;
    }
}