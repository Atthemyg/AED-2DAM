package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends AbstractDao<Vehiculo> {

    public VehiculoDao(String url) { super(url); }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(
                rs.getString("matricula"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("anio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos ORDER BY matricula";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar vehiculos", e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {
        if (matricula == null) return Optional.empty();
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return consultarUno(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar vehiculo por matricula", e);
            return Optional.empty();
        }
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        return buscarPorId(matricula);
    }

    public boolean eliminar(String matricula) {
        if (matricula == null) return false;
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricula);
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("eliminar vehiculo", e);
            return false;
        }
    }

    public boolean insertar(Vehiculo v) {
        String sql = "INSERT INTO Vehiculos (matricula,marca,modelo,anio) VALUES (?,?,?,?)";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getMatricula());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("insertar vehiculo", e);
            return false;
        }
    }

    public boolean actualizar(Vehiculo v) {
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getMarca());
            ps.setString(2, v.getModelo());
            ps.setInt(3, v.getAnio());
            ps.setString(4, v.getMatricula());
            return ejecutar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("actualizar vehiculo", e);
            return false;
        }
    }

    //Revisar lo del LIKE
    public List<Vehiculo> buscarPorMarca(String texto) {
        if (texto == null) return List.of();
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos "
                   + "WHERE marca LIKE ? ORDER BY matricula";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar vehiculos por marca", e);
            return List.of();
        }
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos "
                   + "WHERE anio BETWEEN ? AND ? ORDER BY matricula";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, desde);
            ps.setInt(2, hasta);
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar vehiculos por anio", e);
            return List.of();
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Vehiculos";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultarEntero(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("contar vehiculos", e);
            return 0;
        }
    }

    public Map<String, Integer> contarPorMarca() {
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return consultarAgrupado(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("contar vehiculos por marca", e);
            return Map.of();
        }
    }
}
