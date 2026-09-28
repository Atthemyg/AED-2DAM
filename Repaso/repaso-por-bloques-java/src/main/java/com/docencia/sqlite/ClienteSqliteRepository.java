package com.docencia.sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteSqliteRepository implements ClienteRepository {
    private final String url;

    public ClienteSqliteRepository(String databasePath) {
        this.url = "jdbc:sqlite:" + databasePath;
        crearTablaSiNoExiste();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS cliente (dni TEXT PRIMARY KEY, nombre TEXT NOT NULL, email TEXT NOT NULL, ciudad TEXT NOT NULL)";
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new RuntimeException("No se pudo crear la tabla cliente", exception);
        }
    }

    @Override
    public Boolean save(Cliente cliente) {
        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(
                     "INSERT INTO cliente (dni, nombre, email, ciudad) VALUES (?, ?, ?, ?)")) {

            sentencia.setString(1, cliente.getDni());
            sentencia.setString(2, cliente.getNombre());
            sentencia.setString(3, cliente.getEmail());
            sentencia.setString(4, cliente.getCiudad());

            return sentencia.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error creando cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Cliente findByDni(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni=?";

        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {

            sentencia.setString(1, dni);
            ResultSet rs = sentencia.executeQuery();

            if (!rs.next()) return null;

            return new Cliente(
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("ciudad")
            );

        } catch (Exception e) {
            System.err.println("Error buscando cliente: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Cliente> findAll() {
        String sql = "SELECT * FROM cliente ORDER BY nombre";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {

            ResultSet rs = sentencia.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("ciudad")
                ));
            }

            return clientes;

        } catch (Exception e) {
            System.err.println("Error buscando clientes: " + e.getMessage());
            return clientes;
        }
    }

    @Override
    public Boolean update(Cliente cliente) {
        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(
                     "UPDATE cliente SET nombre=?, email=?, ciudad=? WHERE dni=?")) {

            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getEmail());
            sentencia.setString(3, cliente.getCiudad());
            sentencia.setString(4, cliente.getDni());

            return sentencia.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error actualizando cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean deleteByDni(String dni) {
        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(
                     "DELETE FROM cliente WHERE dni=?")) {

            sentencia.setString(1, dni);
            return sentencia.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error eliminando cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Cliente> findByCiudad(String ciudad) {
        String sql = "SELECT * FROM cliente WHERE ciudad=?";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement sentencia = connection.prepareStatement(sql)) {

            sentencia.setString(1, ciudad);
            ResultSet rs = sentencia.executeQuery();

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("ciudad")
                ));
            }

            return clientes;

        } catch (Exception e) {
            System.err.println("Error buscando clientes por ciudad: " + e.getMessage());
            return clientes;
        }
    }

    
}
