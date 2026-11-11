package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Reserva;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;
import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceso a datos de las reservas de libros de la biblioteca
 */
public class ReservaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ReservaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO
     * @param url URL JDBC de la base de datos SQLite
     */
    public ReservaDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    /**
     * Crea el DAO reutilizando una factoria de EntityManager ya existente
     * @param factory factoria compartida
     */
    public ReservaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Reserva dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null
                && dato.getFechaReserva() != null && dato.getEstado() != null
                && (dato.getEstado().equals("PENDIENTE") || dato.getEstado().equals("ATENDIDA")
                || dato.getEstado().equals("CANCELADA"));
    }

    /**
     * Lista todas las reservas ordenadas por fecha de reserva
     * @return reservas existentes, o lista vacia si no hay ninguna
     */
    public List<Reserva> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r order by r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca una reserva por su identificador
     * @param id identificador de la reserva
     * @return reserva encontrada u Optional.empty() si no existe
     */
    public Optional<Reserva> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ReservaEntity.class, id))
                    .map(ReservaMapper::toDomain);
        }
    }

    /**
     * Inserta una reserva nueva; el identificador se genera automaticamente
     * @param dato reserva con los campos obligatorios
     * @return identificador generado, o 0 si los datos no son validos o hay un error
     */
    public int insertar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity entidad = ReservaMapper.toEntity(dato);
                entidad.setIdReserva(null);
                em.persist(entidad);
                em.flush();
                dato.setIdReserva(entidad.getIdReserva());
                tx.commit();
                return dato.getIdReserva();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza los datos de la reserva existente
     * @param dato reserva con su identificador y los nuevos valores
     * @return 1 si se actualizo; 0 si no existe, los datos no son validos o hay un error
     */
    public int actualizar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(ReservaEntity.class, dato.getIdReserva()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(ReservaMapper.toEntity(dato));
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
     * Elimina una reserva; no se puede borrar si tiene registros que la referencian
     * @param id identificador de la reserva
     * @return 1 si se elimino; 0 si no existe, tiene referencias o hay un error
     */
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity existente = em.find(ReservaEntity.class, id);
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
     * Lista las reservas pendientes, de la mas antigua a la mas reciente
     * @return reservas con estado PENDIENTE, o lista vacia si no hay
     */
    public List<Reserva> listarPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where r.estado = 'PENDIENTE'"
                                    + " order by r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista las reservas realizadas por un socio
     * @param idSocio identificador del socio
     * @return reservas del socio, o lista vacia si no hay
     */
    public List<Reserva> buscarPorSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where r.idSocio = :valor"
                                    + " order by r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .setParameter("valor", idSocio)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista las reservas recibidas por un libro
     * @param idLibro identificador del libro
     * @return reservas del libro, o lista vacia si no hay
     */
    public List<Reserva> listarPorLibro(int idLibro) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r from ReservaEntity r where r.idLibro = :valor"
                                    + " order by r.fechaReserva, r.idReserva",
                            ReservaEntity.class)
                    .setParameter("valor", idLibro)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Cuenta las reservas que estan en estado pendiente
     * @return numero de reservas pendientes; 0 si no hay
     */
    public long contarReservasPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select count(r) from ReservaEntity r where r.estado = 'PENDIENTE'",
                            Long.class)
                    .getSingleResult();
        }
    }

    /**
     * Obtiene las reservas pendientes con el nombre del socio, el titulo del libro y su autor
     * @return filas {idReserva, socio, libro, autor, fechaReserva}
     */
    public List<Object[]> reservasConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select r.idReserva, s.nombre, l.titulo, a.nombre, r.fechaReserva"
                                    + " from ReservaEntity r join SocioEntity s on s.idSocio = r.idSocio"
                                    + " join LibroEntity l on l.idLibro = r.idLibro"
                                    + " join AutorEntity a on a.idAutor = l.idAutor"
                                    + " where r.estado = 'PENDIENTE' order by r.fechaReserva, r.idReserva",
                            Object[].class)
                    .getResultList();
        }
    }

    /**
     * Obtiene, para cada libro, la reserva pendiente mas antigua
     * Si varias comparten fecha se elige la de menor identificador
     * @return filas {idLibro, titulo, idReserva, socio, fechaReserva}
     */
    public List<Object[]> prioridadReservas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select l.idLibro, l.titulo, r.idReserva, s.nombre, r.fechaReserva"
                                    + " from ReservaEntity r join LibroEntity l on l.idLibro = r.idLibro"
                                    + " join SocioEntity s on s.idSocio = r.idSocio"
                                    + " where r.estado = 'PENDIENTE'"
                                    + " and r.idReserva = (select min(r2.idReserva) from ReservaEntity r2"
                                    + " where r2.idLibro = r.idLibro and r2.estado = 'PENDIENTE'"
                                    + " and r2.fechaReserva = (select min(r3.fechaReserva)"
                                    + " from ReservaEntity r3"
                                    + " where r3.idLibro = r.idLibro and r3.estado = 'PENDIENTE'))"
                                    + " order by l.titulo, l.idLibro",
                            Object[].class)
                    .getResultList();
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