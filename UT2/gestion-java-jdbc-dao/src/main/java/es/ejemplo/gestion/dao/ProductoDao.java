package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends AbstractDao {
    //private  ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }

    @Override
    Producto mapToClass(ResultSet rs) {
        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }

    // Lo primero de todo crear lista por si falla devolver lista vacia
    public List<Producto> listar() {
        //List<Producto> lista = new ArrayList<>();
        String query = "select id,nombre,precio from productos order by id";
        return consultar(query);

        /*try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoProducto(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }

        return lista;*/
    }

    public Optional<Producto> buscarPorId(int id) {
        String sql = "SELECT id, nombre, precio FROM Productos WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoProducto(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar el producto", e);
        }
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al actualizar el producto", e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE id = ?";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al eliminar el producto", e);
        }
    }

    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM Productos WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeoProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar productos por nombre", e);
        }
        return lista;
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, precio FROM Productos WHERE precio BETWEEN ? AND ? ORDER BY precio, id";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, minimo);
            ps.setDouble(2, maximo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeoProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al buscar productos por precio", e);
        }
        return lista;
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Productos";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al contar los productos", e);
        }
    }

    public double precioMedio() {
        // AVG devuelve NULL con la tabla vacía; getDouble lo convierte en 0
        String sql = "SELECT AVG(precio) FROM Productos";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new AplicationException("Error al calcular el precio medio", e);
        }
    }

    private Producto mapeoProducto(ResultSet rs) {

        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
