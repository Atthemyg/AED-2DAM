package es.ejemplo.biblioteca.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

import es.ejemplo.biblioteca.infra.ConexionBD;
import es.ejemplo.biblioteca.modelo.*;
/** Implementación pendiente: el alumnado debe completar los métodos JDBC. */
public final class SocioDao extends ConexionBD implements ISocioDao {

 public SocioDao(String url) { super(url); }

 Socio mapToClass(ResultSet rs) throws SQLException {
  return new Socio(rs.getInt(1), rs.getString(2),
          rs.getString(3), rs.getString(4));
 }

 @Override
 public List<Socio> listar() {
  List<Socio> lista = new ArrayList<>();
  String sql = "SELECT id, nombre, email, fecha_alta FROM socios ORDER BY id ASC";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    lista.add(mapToClass(rs));
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los socios", e);
  }
  return lista;
 }

 @Override
 public Optional<Socio> buscarPorId(Integer id) {
  if (id == null) {
   return Optional.empty();
  }
  String sql = "SELECT id, nombre, email, fecha_alta FROM socios WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, id);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     return Optional.of(mapToClass(rs));
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al buscar el socio", e);
  }
  return Optional.empty();
 }

 @Override
 public int insertar(Socio entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El socio no puede ser nulo");
  }
  String sql = "INSERT INTO socios (nombre, email, fecha_alta) VALUES (?, ?, ?)";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, entidad.getNombre());
   ps.setString(2, entidad.getEmail());
   ps.setString(3, entidad.getFechaAlta());
   if (ps.executeUpdate() > 0) {
    try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
         ResultSet rs = id.executeQuery()) {
     rs.next();
     return rs.getInt(1);
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al insertar el socio", e);
  }
  return 0;
 }

 @Override
 public int actualizar(Socio entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El socio no puede ser nulo");
  }
  if (entidad.getId() == null) {
   throw new IllegalArgumentException("Para actualizar hace falta el identificador");
  }
  String sql = "UPDATE socios SET nombre = ?, email = ?, fecha_alta = ? WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, entidad.getNombre());
   ps.setString(2, entidad.getEmail());
   ps.setString(3, entidad.getFechaAlta());
   ps.setInt(4, entidad.getId());
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al actualizar el socio", e);
  }
 }

 @Override
 public int eliminar(Integer id) {
  if (id == null) {
   throw new IllegalArgumentException("El identificador no puede ser nulo");
  }
  String sql = "DELETE FROM socios WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, id);
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al eliminar el socio", e);
  }
 }

 @Override
 public Optional<Socio> buscarPorEmail(String email) {
  if (email == null) {
   return Optional.empty();
  }
  String sql = "SELECT id, nombre, email, fecha_alta FROM socios WHERE email = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, email);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     return Optional.of(mapToClass(rs));
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al buscar el socio", e);
  }
  return Optional.empty();
 }

 @Override
 public List<Socio> listarAltasDesde(String fecha) {
  List<Socio> lista = new ArrayList<>();
  if (fecha == null || fecha.isBlank()) {
   return lista;
  }
  String sql = "SELECT id, nombre, email, fecha_alta FROM socios WHERE fecha_alta >= ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, fecha);
   try (ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
     lista.add(mapToClass(rs));
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los socios dados de alta", e);
  }
  return lista;
 }
 }
