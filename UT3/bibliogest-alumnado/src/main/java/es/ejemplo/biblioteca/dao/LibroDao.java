package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Libro;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceso a datos de los libros del catalogo
 */
public class LibroDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO
     * @param url URL JDBC de la base de datos SQLite
     */
    public LibroDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    /**
     * Crea el DAO reutilizando una factoria de EntityManager ya existente
     * @param factory factoria compartida
     */
    public LibroDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Libro dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank()
                && dato.getIsbn() != null && !dato.getIsbn().isBlank()
                && dato.getGenero() != null && dato.getIdAutor() != null
                && dato.getEjemplares() != null && dato.getEjemplares() >= 0;
    }

    /**
     * Lista todos los libros del catalogo ordenados por titulo
     * @return libros existentes, o lista vacia si no hay ninguno
     */
    public List<Libro> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca un libro por su identificador.
     * @param id identificador del libro
     * @return libro encontrado u Optional.empty() si no existe
     */
    public Optional<Libro> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(LibroEntity.class, id))
                    .map(LibroMapper::toDomain);
        }
    }

    /**
     * Inserta un libro nuevo; el identificador se genera automaticamente.
     * @param dato libro con los campos obligatorios
     * @return identificador generado, o 0 si los datos no son validos o hay un error
     */
    public int insertar(Libro dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity entidad = LibroMapper.toEntity(dato);
                entidad.setIdLibro(null);
                em.persist(entidad);
                em.flush();
                dato.setIdLibro(entidad.getIdLibro());
                tx.commit();
                return dato.getIdLibro();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza los datos del libro existente.
     * @param dato libro con su identificador y los nuevos valores
     * @return 1 si se actualizo; 0 si no existe, los datos no son validos o hay un error
     */
    public int actualizar(Libro dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(LibroEntity.class, dato.getIdLibro()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(LibroMapper.toEntity(dato));
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
     * Elimina un libro; no se puede borrar si tiene registros que lo referencian
     * @param id identificador del libro
     * @return 1 si se elimino; 0 si no existe, tiene referencias o hay un error
     */
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity existente = em.find(LibroEntity.class, id);
                if (existente == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(existente);
                em.flush();
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
     * Lista los libros de un genero concreto
     * @param genero genero exacto a buscar
     * @return libros del genero, o lista vacia si no hay
     */
    public List<Libro> buscarPorGenero(String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where l.genero = :genero"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los libros escritos por un autor.
     * @param idAutor identificador del autor
     * @return libros del autor, o lista vacia si no hay
     */
    public List<Libro> buscarPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where l.idAutor = :id"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("id", idAutor)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca libros cuyo titulo contiene el texto indicado
     * @param texto fragmento que debe aparecer en el titulo
     * @return libros coincidentes, o lista vacia si no hay
     */
    public List<Libro> buscarPorTitulo(String texto) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l"
                                    + " where lower(l.titulo) like lower(concat('%', :texto, '%'))"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("texto", texto)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los libros con algun ejemplar libre
     * @return libros disponibles, o lista vacia si no hay
     */
    public List<Libro> listarDisponibles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where l.ejemplares >"
                                    + " (select count(p) from PrestamoEntity p where p.idLibro = l.idLibro"
                                    + " and p.fechaDevolucion is null)"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los libros publicados desde un anio, de un genero determinado
     * @param anio anio minimo de publicacion, incluido
     * @param genero genero exacto a buscar
     * @return libros que cumplen ambos filtros, o lista vacia si no hay
     */
    public List<Libro> librosDesdeAnio(int anio, String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l"
                                    + " where l.anioPublicacion >= :anio and l.genero = :genero"
                                    + " order by l.anioPublicacion, l.titulo",
                            LibroEntity.class)
                    .setParameter("anio", anio)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Obtiene los generos distintos registrados en el catalogo
     * @return nombres de genero sin repetir, o lista vacia si no hay
     */
    public List<String> generos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select distinct l.genero from LibroEntity l order by l.genero",
                            String.class)
                    .getResultList();
        }
    }

    /**
     * Lista los libros que nunca se han prestado
     * @return libros sin ningun prestamo historico, o lista vacia si no hay
     */
    public List<Libro> nuncaPrestados() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where not exists"
                                    + " (select 1 from PrestamoEntity p where p.idLibro = l.idLibro)"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los libros con mas prestamos historicos que la media del catalogo
     * @return libros por encima de la media, o lista vacia si no hay
     */
    public List<Libro> masPrestadosQueMedia() {
        try (var em = managerFactory.createEntityManager()) {
            long prestamos = em.createQuery("select count(p) from PrestamoEntity p", Long.class)
                    .getSingleResult();
            long libros = em.createQuery("select count(x) from LibroEntity x", Long.class)
                    .getSingleResult();
            // prestamos del libro > prestamos / libros  <=>  prestamos del libro * libros > prestamos
            return em.createQuery("select l from LibroEntity l where l.idLibro in"
                                    + " (select p.idLibro from PrestamoEntity p group by p.idLibro"
                                    + " having count(p) * :libros > :prestamos)"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("libros", libros)
                    .setParameter("prestamos", prestamos)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los libros con reservas pendientes y sin ejemplares disponibles
     * @return libros reservados y agotados, o lista vacia si no hay
     */
    public List<Libro> reservadosSinDisponibilidad() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l from LibroEntity l where exists"
                                    + " (select 1 from ReservaEntity r where r.idLibro = l.idLibro"
                                    + " and r.estado = 'PENDIENTE')"
                                    + " and l.ejemplares <= (select count(p) from PrestamoEntity p"
                                    + " where p.idLibro = l.idLibro and p.fechaDevolucion is null)"
                                    + " order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Obtiene los libros junto con el nombre y la nacionalidad de su autor
     * @return filas {idLibro, titulo, nombreAutor, nacionalidad}
     */
    public List<Object[]> librosConAutor() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.idLibro, l.titulo, a.nombre, a.nacionalidad"
                                    + " from LibroEntity l join AutorEntity a on a.idAutor = l.idAutor"
                                    + " order by l.titulo, l.idLibro",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Cuenta los libros de cada genero.
     * @return filas {genero, totalLibros} ordenadas por genero
     */
    public List<Object[]> contarPorGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.genero, count(l) from LibroEntity l"
                                    + " group by l.genero order by l.genero",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Obtiene los generos con mas prestamos historicos que el minimo indicado
     * @param minimo numero de prestamos que debe superarse
     * @return filas {genero, totalPrestamos} de mayor a menor
     */
    public List<Object[]> prestamosPorGeneroMinimo(long minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.genero, count(p)"
                                    + " from PrestamoEntity p join LibroEntity l on l.idLibro = p.idLibro"
                                    + " group by l.genero having count(p) > :minimo"
                                    + " order by count(p) desc, l.genero",
                            Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        }
    }

    /**
     * Obtiene por libro los ejemplares totales, los prestados actualmente y los disponibles
     * @return filas {idLibro, titulo, ejemplares, prestados, disponibles}
     */
    public List<Object[]> disponibilidadPorLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.idLibro, l.titulo, l.ejemplares,"
                                    + " count(p), l.ejemplares - count(p) from LibroEntity l"
                                    + " left join PrestamoEntity p"
                                    + " on p.idLibro = l.idLibro and p.fechaDevolucion is null"
                                    + " group by l.idLibro, l.titulo, l.ejemplares"
                                    + " order by l.titulo, l.idLibro",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Obtiene estadisticas por genero
     * @return filas {genero, titulos, ejemplares, historicos, activos}
     */
    public List<Object[]> estadisticasGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.genero, count(l), sum(l.ejempla665res),"
                                    + " (select count(p) from PrestamoEntity p"
                                    + " join LibroEntity x on x.idLibro = p.idLibro"
                                    + " where x.genero = l.genero),"
                                    + " (select count(q) from PrestamoEntity q"
                                    + " join LibroEntity y on y.idLibro = q.idLibro"
                                    + " where y.genero = l.genero and q.fechaDevolucion is null)"
                                    + " from LibroEntity l group by l.genero order by l.genero",
                            Object[].class)
                    .getResultList();
        }
    }

    /** Cierra la factoria de EntityManager solo si fue creada por este DAO */
    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}