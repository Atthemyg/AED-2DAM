package es.ejemplo.biblioteca.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import es.ejemplo.biblioteca.infra.ConexionBD;
import es.ejemplo.biblioteca.modelo.*;

public class LibroDao extends ConexionBD implements ILibroDao {

public LibroDao(String url) { super(url); }

 Libro mapToClass(ResultSet rs) throws SQLException {
  return new Libro(rs.getInt(1), rs.getString(2),
          rs.getString(3), rs.getInt(4));
 }

 @Override
 public List<Libro> listar() {
  List<Libro> lista = new ArrayList<>();
  String sql = "SELECT id, titulo, autor, anio_publicacion FROM libros ORDER BY id ASC";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
   while (rs.next()) {
    lista.add(mapToClass(rs));
   }
  } catch (SQLException e) {
   throw new DaoException("Error al listar los libros", e);
  }
  return lista;
 }
 
 @Override 
 public Optional<Libro> buscarPorId(Integer id) {
  if (id == null) {
   return Optional.empty();
  }
  String sql = "SELECT id, titulo, autor, anio_publicacion FROM libros WHERE id = ?";
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

 @Override
 public int insertar(Libro entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El libro no puede ser nulo");
  }
  String sql = "INSERT INTO libros (titulo, autor, anio_publicacion) VALUES (?, ?, ?)";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, entidad.getTitulo());
   ps.setString(2, entidad.getAutor());
   ps.setInt(3, entidad.getAnioPublicacion());
   if (ps.executeUpdate() > 0) {
    try (PreparedStatement id = con.prepareStatement("SELECT last_insert_rowid()");
         ResultSet rs = id.executeQuery()) {
     rs.next();
     return rs.getInt(1);
    }
   }
  } catch (SQLException e) {
   throw new DaoException("Error al insertar el libro", e);
  }
  return 0;
 }

 @Override
 public int actualizar(Libro entidad) {
  if (entidad == null) {
   throw new IllegalArgumentException("El libro no puede ser nulo");
  }
  if (entidad.getId() == null) {
   throw new IllegalArgumentException("Para actualizar hace falta el identificador");
  }
  String sql = "UPDATE libros SET titulo = ?, autor = ?, anio_publicacion = ? WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setString(1, entidad.getTitulo());
   ps.setString(2, entidad.getAutor());
   ps.setInt(3, entidad.getAnioPublicacion());
   ps.setInt(4, entidad.getId());
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al actualizar el libro", e);
  }
 }
 
 @Override
 public int eliminar(Integer id) {
  if (id == null) {
   throw new IllegalArgumentException("El identificador no puede ser nulo");
  }
  String sql = "DELETE FROM libros WHERE id = ?";
  try (Connection con = this.obtenerConexion();
       PreparedStatement ps = con.prepareStatement(sql)) {
   ps.setInt(1, id);
   return ps.executeUpdate();
  } catch (SQLException e) {
   throw new DaoException("Error al eliminar el libro", e);
 }
 }
}
