package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class VehiculoDao extends ConexionBD {

    public VehiculoDao(String url) {
        super(url);
    }

    // Lo primero de todo crear lista por si falla devolver lista vacia
    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String query = "select matricula,marca,modelo,anio from vehiculos order by matricula";

        try (Connection connection = this.obtenerConexion()) {

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoVehiculo(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoVehiculo(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar el vehículo", e);
        }
    }

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMatricula());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el vehículo", e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMarca());
            ps.setString(2, vehiculo.getModelo());
            ps.setInt(3, vehiculo.getAnio());
            ps.setString(4, vehiculo.getMatricula());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el vehículo", e);
        }
    }

    public boolean eliminar(String matricula) {
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al eliminar el vehículo", e);
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        List<Vehiculo> lista = new ArrayList<>();
        // LIKE de SQLite no distingue mayúsculas/minúsculas en ASCII
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca LIKE ? ORDER BY matricula";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeoVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar vehículos por marca", e);
        }
        return lista;
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE anio BETWEEN ? AND ? ORDER BY anio, matricula";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeoVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar vehículos por año", e);
        }
        return lista;
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Vehiculos";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al contar los vehículos", e);
        }
    }

    public Map<String, Integer> contarPorMarca() {
        // LinkedHashMap conserva el orden de la consulta: más vehículos primero
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY COUNT(*) DESC, marca";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al contar vehículos por marca", e);
        }
        return resultado;
    }

    private Vehiculo mapeoVehiculo(ResultSet rs) {

        try {
            return new Vehiculo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}