package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Autor;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceso a datos de los autores de la biblioteca
 */
public class AutorDao implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO
     * @param url URL JDBC de la base de datos SQLite
     */
    public AutorDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    /**
     * Crea el DAO reutilizando una factoria de EntityManager ya existente
     * @param factory factoria compartida
     */
    public AutorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    /**
     * Registra un mensaje de advertencia en el log cuando un metodo no se puede completar
     * @param metodo Nombre del metodo que ha producido el error.
     * @param ex Excepcion que contiene la informacion del error.
     */
    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Autor dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    /**
     * Lista todos los autores ordenados por nombre
     * @return autores existentes, o lista vacia si no hay ninguno
     */
    public List<Autor> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a order by a.nombre, a.idAutor",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca un autor por su identificador
     * @param id identificador del autor
     * @return autor encontrado u Optional.empty() si no existe
     */
    public Optional<Autor> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(AutorEntity.class, id))
                    .map(AutorMapper::toDomain);
        }
    }

    /**
     * Inserta un autor nuevo; el identificador se genera automaticamente
     * @param dato autor con el nombre
     * @return identificador generado, o 0 si los datos no son validos o hay un error
     */
    public int insertar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity entidad = AutorMapper.toEntity(dato);
                entidad.setIdAutor(null);
                em.persist(entidad);
                em.flush();
                dato.setIdAutor(entidad.getIdAutor());
                tx.commit();
                return dato.getIdAutor();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza los datos de un autor existente
     * @param dato autor con su identificador y los nuevos valores
     * @return 1 si se actualizo; 0 si no existe, los datos no son validos o hay un error
     */
    public int actualizar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(AutorEntity.class, dato.getIdAutor()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(AutorMapper.toEntity(dato));
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("actualizar", ex);
                return 0;
            }
        }
    }

    /**
     * Elimina un autor; no se puede borrar si tiene libros asociados
     * @param id identificador del autor
     * @return 1 si se elimino; 0 si no existe, tiene libros o hay un error
     */
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity autor = em.find(AutorEntity.class, id);
                if (autor == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(autor);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    /**
     * Lista los autores de una nacionalidad concreta
     * @param nacionalidad nacionalidad exacta a buscar
     * @return autores de esa nacionalidad, o lista vacia si no hay
     */
    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a where a.nacionalidad = :nacionalidad order by a.nombre, a.idAutor",
                            AutorEntity.class)
                    .setParameter("nacionalidad", nacionalidad)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Cuenta los libros del catalogo escritos por un autor
     * @param idAutor identificador del autor
     * @return numero de libros del autor; 0 si no tiene
     */
    public long contarLibrosPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(l) from LibroEntity l where l.idAutor = :id", Long.class)
                    .setParameter("id", idAutor)
                    .getSingleResult();
        }
    }

    /**
     * Obtiene todos los autores con su numero de libros, incluidos los que no tienen ninguno
     * @return filas {idAutor, nombre, totalLibros}
     */
    public List<Object[]> autoresConRecuento() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a.idAutor, a.nombre, count(l) from AutorEntity a left join LibroEntity l"
                                    + " on l.idAutor = a.idAutor group by a.idAutor, a.nombre order by a.nombre, a.idAutor",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Lista los autores cuyos libros han sido prestados por al menos tres socios distintos
     * @return autores que cumplen la condicion, o lista vacia si no hay
     */
    public List<Autor> autoresLeidosPorTresSocios() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a where a.idAutor in (select l.idAutor from LibroEntity l"
                                    + " join PrestamoEntity p on p.idLibro = l.idLibro group by l.idAutor"
                                    + " having count(distinct p.idSocio) >= 3) order by a.idAutor",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Cierra la factoria de EntityManager solo si fue creada por este DAO.
     */
    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}