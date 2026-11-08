package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Socio;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceso a datos de los socios de la biblioteca
 */
public class SocioDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SocioDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    /**
     * Crea el DAO
     * @param url URL JDBC de la base de datos SQLite
     */
    public SocioDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    /**
     * Crea el DAO reutilizando una factoria de EntityManager ya existente
     * @param factory factoria compartida
     */
    public SocioDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Socio dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getEmail() != null && !dato.getEmail().isBlank()
                && dato.getFechaAlta() != null && dato.getActivo() != null;
    }

    /**
     * Lista todos los socios ordenados por nombre
     * @return socios existentes, o lista vacia si no hay ninguno
     */
    public List<Socio> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s order by s.nombre, s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca un socio por su identificador
     * @param id identificador del socio
     * @return socio encontrado u Optional.empty() si no existe
     */
    public Optional<Socio> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SocioEntity.class, id))
                    .map(SocioMapper::toDomain);
        }
    }

    /**
     * Inserta un socio nuevo; el identificador se genera automaticamente
     * @param dato socio con nombre, email, fecha de alta y estado
     * @return identificador generado, o 0 si los datos no son validos o hay un error
     */
    public int insertar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity entidad = SocioMapper.toEntity(dato);
                entidad.setIdSocio(null);
                em.persist(entidad);
                em.flush();
                dato.setIdSocio(entidad.getIdSocio());
                tx.commit();
                return dato.getIdSocio();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza los datos de un socio existente
     * @param dato socio con su identificador y los nuevos valores
     * @return 1 si se actualizo; 0 si no existe, los datos no son validos o hay un error
     */
    public int actualizar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(SocioEntity.class, dato.getIdSocio()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(SocioMapper.toEntity(dato));
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
     * Elimina un socio; no se puede borrar si tiene prestamos o reservas asociados
     * @param id identificador del socio
     * @return 1 si se elimino; 0 si no existe, tiene referencias o hay un error
     */
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity socio = em.find(SocioEntity.class, id);
                if (socio == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(socio);
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
     * Lista los socios activos, de la fecha de alta mas reciente a la mas antigua
     * @return socios con activo = true, o lista vacia si no hay
     */
    public List<Socio> listarActivos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s where s.activo = true"
                                    + " order by s.fechaAlta desc, s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Busca un socio por su correo electronico
     * @param email direccion de correo que se desea localizar
     * @return socio encontrado u Optional.empty() si no existe
     */
    public Optional<Socio> buscarPorEmail(String email) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s where s.email = :email",
                            SocioEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(SocioMapper::toDomain);
        }
    }

    /**
     * Lista los socios con algun prestamo vencido
     * @param fecha fecha de referencia en formato YYYY-MM-DD
     * @return socios con prestamos vencidos, o lista vacia si no hay
     */
    public List<Socio> listarConPrestamosVencidos(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s where exists"
                                    + " (select 1 from PrestamoEntity p where p.idSocio = s.idSocio"
                                    + " and p.fechaDevolucion is null and p.fechaLimite < :fecha)"
                                    + " order by s.idSocio",
                            SocioEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Lista los socios que nunca han realizado un prestamo
     * @return socios sin prestamos, o lista vacia si no hay
     */
    public List<Socio> sociosSinPrestamos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SocioEntity s where not exists"
                                    + " (select 1 from PrestamoEntity p where p.idSocio = s.idSocio)"
                                    + " order by s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        }
    }

    /**
     * Obtiene el ranking de socios con mas prestamos
     * @param limite numero maximo de socios a devolver
     * @return filas {idSocio, nombre, totalPrestamos} de mayor a menor
     */
    public List<Object[]> rankingSocios(int limite) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s.idSocio, s.nombre, count(p) from SocioEntity s"
                                    + " join PrestamoEntity p on p.idSocio = s.idSocio"
                                    + " group by s.idSocio, s.nombre"
                                    + " order by count(p) desc, s.idSocio",
                            Object[].class)
                    .setMaxResults(limite)
                    .getResultList();
        }
    }

    /**
     * Obtiene el historial completo de prestamos de un socio con su estado de devolucion y retraso
     * @param idSocio identificador del socio
     * @return filas {idPrestamo, titulo, fechaPrestamo, fechaLimite, fechaDevolucion, estado}
     */
    public List<Object[]> historialSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p.idPrestamo, l.titulo, p.fechaPrestamo, p.fechaLimite,"
                                    + " p.fechaDevolucion,"
                                    + " case when p.fechaDevolucion is null then"
                                    + " (case when p.fechaLimite < :hoy then 'ACTIVO CON RETRASO' else 'ACTIVO' end)"
                                    + " when p.fechaDevolucion > p.fechaLimite then 'DEVUELTO CON RETRASO'"
                                    + " else 'DEVUELTO' end"
                                    + " from PrestamoEntity p join LibroEntity l on l.idLibro = p.idLibro"
                                    + " where p.idSocio = :id order by p.fechaPrestamo, p.idPrestamo",
                            Object[].class)
                    .setParameter("hoy", LocalDate.now().toString())
                    .setParameter("id", idSocio)
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