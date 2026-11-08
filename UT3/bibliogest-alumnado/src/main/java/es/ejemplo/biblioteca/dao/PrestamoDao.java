package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Prestamo;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceso a datos de los prestamos de la biblioteca
 */
public class PrestamoDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PrestamoDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO
     * @param url URL JDBC de la base de datos SQLite
     */
    public PrestamoDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    /**
     * Crea el DAO reutilizando una factoria de EntityManager ya existente
     * @param factory factoria compartida
     */
    public PrestamoDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Prestamo dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null
                && dato.getFechaPrestamo() != null && dato.getFechaLimite() != null
                && dato.getFechaLimite().compareTo(dato.getFechaPrestamo()) >= 0
                && (dato.getFechaDevolucion() == null
                || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo()) >= 0);
    }

    /**
     * Lista todos los prestamos ordenados por fecha de prestamo
     * @return prestamos existentes, o lista vacia si no hay ninguno
     */
    public List<Prestamo> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PrestamoEntity p order by p.fechaPrestamo, p.idPrestamo",
                            PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca un prestamo por su identificador
     * @param id identificador del prestamo
     * @return prestamo encontrado u Optional.empty() si no existe
     */
    public Optional<Prestamo> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PrestamoEntity.class, id))
                    .map(PrestamoMapper::toDomain);
        }
    }

    /**
     * Inserta un prestamo nuevo; el identificador se genera automaticamente
     * @param dato prestamo con los campos obligatorios
     * @return identificador generado, o 0 si los datos no son validos o hay un error
     */
    public int insertar(Prestamo dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PrestamoEntity entidad = PrestamoMapper.toEntity(dato);
                entidad.setIdPrestamo(null);
                em.persist(entidad);
                em.flush();
                dato.setIdPrestamo(entidad.getIdPrestamo());
                tx.commit();
                return dato.getIdPrestamo();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza los datos del prestamo existente
     * @param dato prestamo con su identificador y los nuevos valores
     * @return 1 si se actualizo; 0 si no existe, los datos no son validos o hay un error
     */
    public int actualizar(Prestamo dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(PrestamoEntity.class, dato.getIdPrestamo()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(PrestamoMapper.toEntity(dato));
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
     * Elimina un prestamo; no se puede borrar si tiene registros que lo referencian
     * @param id identificador del prestamo
     * @return 1 si se elimino; 0 si no existe, tiene referencias o hay un error
     */
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PrestamoEntity existente = em.find(PrestamoEntity.class, id);
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
     * Lista los prestamos de un socio, del mas reciente al mas antiguo
     * @param idSocio identificador del socio
     * @return prestamos del socio, o lista vacia si no hay
     */
    public List<Prestamo> listarPorSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PrestamoEntity p where p.idSocio = :valor"
                                    + " order by p.fechaPrestamo desc, p.idPrestamo desc",
                            PrestamoEntity.class)
                    .setParameter("valor", idSocio)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los prestamos activos
     * @return prestamos sin devolver, o lista vacia si no hay
     */
    public List<Prestamo> listarActivos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PrestamoEntity p where p.fechaDevolucion is null"
                                    + " order by p.fechaLimite, p.idPrestamo",
                            PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los prestamos vencidos
     * @param fecha fecha de referencia en formato YYYY-MM-DD
     * @return prestamos vencidos, o lista vacia si no hay
     */
    public List<Prestamo> listarVencidos(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PrestamoEntity p where p.fechaDevolucion is null"
                                    + " and p.fechaLimite < :valor order by p.fechaLimite, p.idPrestamo",
                            PrestamoEntity.class)
                    .setParameter("valor", fecha)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Cuenta todos los prestamos historicos de un libro, devueltos o no
     * @param idLibro identificador del libro
     * @return numero de prestamos del libro; 0 si no tiene
     */
    public long contarPrestamosPorLibro(int idLibro) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(p) from PrestamoEntity p where p.idLibro = :id", Long.class)
                    .setParameter("id", idLibro)
                    .getSingleResult();
        }
    }

    /**
     * Obtiene los prestamos con el nombre del socio, el titulo del libro y las fechas
     * @return filas {idPrestamo, socio, libro, fechaPrestamo, fechaLimite, fechaDevolucion}
     */
    public List<Object[]> prestamosConSocioYLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p.idPrestamo, s.nombre, l.titulo,"
                                    + " p.fechaPrestamo, p.fechaLimite, p.fechaDevolucion from PrestamoEntity p"
                                    + " join SocioEntity s on s.idSocio = p.idSocio"
                                    + " join LibroEntity l on l.idLibro = p.idLibro"
                                    + " order by p.fechaPrestamo, p.idPrestamo",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Obtiene el ranking de los libros mas prestados de un mes
     * @param anioMes mes a consultar en formato YYYY-MM
     * @param limite numero maximo de libros a devolver
     * @return filas {idLibro, titulo, autor, totalPrestamos} de mayor a menor
     */
    public List<Object[]> rankingMensual(String anioMes, int limite) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.idLibro, l.titulo, a.nombre, count(p)"
                                    + " from PrestamoEntity p join LibroEntity l on l.idLibro = p.idLibro"
                                    + " join AutorEntity a on a.idAutor = l.idAutor"
                                    + " where p.fechaPrestamo like :mes"
                                    + " group by l.idLibro, l.titulo, a.nombre"
                                    + " order by count(p) desc, l.titulo, l.idLibro",
                            Object[].class)
                    .setParameter("mes", anioMes + "%")
                    .setMaxResults(limite)
                    .getResultList();
        }
    }

    /**
     * Registra un prestamo si el socio esta activo y queda algun ejemplar libre
     * @param idSocio identificador del socio que solicita el prestamo
     * @param idLibro identificador del libro prestado
     * @param inicio fecha de prestamo en formato YYYY-MM-DD
     * @param limite fecha limite de devolucion en formato YYYY-MM-DD, no anterior al inicio
     * @return identificador del prestamo, o 0 si no se pudo registrar
     */
    public int registrarPrestamo(int idSocio, int idLibro, String inicio, String limite) {
        Prestamo nuevo = new Prestamo(null, idSocio, idLibro, inicio, limite, null);
        if (!validar(nuevo)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity socio = em.find(SocioEntity.class, idSocio);
                LibroEntity libro = em.find(LibroEntity.class, idLibro);
                if (socio == null || libro == null || !Boolean.TRUE.equals(socio.getActivo())) {
                    tx.commit();
                    return 0;
                }
                long activos = em.createQuery("select count(p) from PrestamoEntity p where p.idLibro = :id"
                                + " and p.fechaDevolucion is null", Long.class)
                        .setParameter("id", idLibro)
                        .getSingleResult();
                if (activos >= libro.getEjemplares()) {
                    tx.commit();
                    return 0;
                }
                PrestamoEntity entidad = PrestamoMapper.toEntity(nuevo);
                entidad.setIdPrestamo(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdPrestamo();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("registrarPrestamo", ex);
                return 0;
            }
        }
    }

    /**
     * Registra la devolucion de un prestamo activo
     * @param id identificador del prestamo
     * @param fecha fecha de devolucion en formato YYYY-MM-DD, no anterior a la del prestamo
     * @return 1 si se registro la devolucion; 0 si no existe, ya estaba devuelto, la fecha no es valida o hay un error
     */
    public int devolver(int id, String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PrestamoEntity prestamo = em.find(PrestamoEntity.class, id);
                if (prestamo == null || prestamo.getFechaDevolucion() != null || fecha == null
                        || fecha.compareTo(prestamo.getFechaPrestamo()) < 0) {
                    tx.commit();
                    return 0;
                }
                prestamo.setFechaDevolucion(fecha);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("devolver", ex);
                return 0;
            }
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