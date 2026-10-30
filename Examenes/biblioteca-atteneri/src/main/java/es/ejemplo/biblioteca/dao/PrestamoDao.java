package es.ejemplo.biblioteca.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import es.ejemplo.biblioteca.infra.ConexionBD;
import es.ejemplo.biblioteca.modelo.*;
/** Implementación pendiente: el alumnado debe completar los métodos JDBC. */
public final class PrestamoDao extends ConexionBD implements IPrestamoDao {

 public PrestamoDao(String url) { super(url); }

 Prestamo mapToClass(ResultSet rs) throws SQLException {
  return new Prestamo(rs.getInt(1), rs.getInt(2),
          rs.getInt(3), rs.getString(4), rs.getString(5));
 }

 PrestamosPorSocio mapToClassInter(ResultSet rs) throws SQLException {
  return new PrestamosPorSocio(rs.getInt(1), rs.getString(2),
          rs.getLong(3));
 }

 @Override
 public List<Prestamo> listar() {
  List<Prestamo> lista = new ArrayList<>();
  String sql = "SELECT id, id_libro, id_socio, fecha_prestamo, fecha_devolucion FROM prestamos ORDER BY id ASC";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    lista.add(mapToClass(rs));
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los prestamos", e);
  }
  return lista;
 }
 
 @Override
 public Optional<Prestamo> buscarPorId(Integer id) {
  if (id == null) {
   return Optional.empty();
  }
  String sql = "SELECT id, id_libro, id_socio, fecha_prestamo, fecha_devolucion FROM prestamos WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, id);
   try (ResultSet rs = ps.executeQuery()) {
    if (rs.next()) {
     return Optional.of(mapToClass(rs));
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al buscar el prestamo", e);
  }
  return Optional.empty();
 }
 
 @Override
 public int insertar(Prestamo entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El prestamo no puede ser nulo");
  }
  String sql = "INSERT INTO prestamos (id_libro, id_socio, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, entidad.getIdLibro());
   ps.setInt(2, entidad.getIdSocio());
   ps.setString(3, entidad.getFechaPrestamo());
   ps.setString(4, entidad.getFechaDevolucion());
   if (ps.executeUpdate() > 0) {
    try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
         ResultSet rs = id.executeQuery()) {
     rs.next();
     return rs.getInt(1);
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al insertar el prestamo", e);
  }
  return 0;
 }

 @Override
 public int actualizar(Prestamo entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El prestamo no puede ser nulo");
  }
  if (entidad.getId() == null) {
   throw new IllegalArgumentException("Para actualizar hace falta el identificador");
  }
  String sql = "UPDATE prestamos SET id_libro = ?, id_socio = ?, fecha_prestamo = ?, fecha_devolucion = ? WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, entidad.getIdLibro());
   ps.setInt(2, entidad.getIdSocio());
   ps.setString(3, entidad.getFechaPrestamo());
   ps.setString(4, entidad.getFechaDevolucion());
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al actualizar el prestamo", e);
  }
 }

 @Override
 public int eliminar(Integer id) {
  if (id == null) {
   throw new IllegalArgumentException("El identificador no puede ser nulo");
  }
  String sql = "DELETE FROM prestamos WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, id);
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al eliminar el prestamo", e);
  }
 }

 @Override
 public List<PrestamosPorSocio> contarPrestamosPorSocio() {
  List<PrestamosPorSocio> lista = new ArrayList<>();
  String sql = "SELECT id_socio, nombre, total_prestamos FROM socios";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    lista.add(mapToClassInter(rs));
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los prestamos", e);
  }
  return lista;
 }

 @Override
 public List<Prestamo> listarPrestamosPorAutor(String autor) {
  List<Prestamo> lista = new ArrayList<>();
  if (autor == null || autor.isBlank()) {
   return lista;
  }
  String sql = "SELECT p.id, p.id_libro, p.id_socio, p.fecha_prestamo, p.fecha_devolucion FROM prestamo p " +
          "INNER JOIN libros l ON p.id_libro = l.id";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    lista.add(mapToClass(rs));
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los prestamos", e);
  }
  return lista;
 }
}
