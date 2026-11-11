
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.mapper.SalaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de salas. */
public class SalaDao {

    private static final Logger LOG = Logger.getLogger(SalaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SalaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SalaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sala dato) {
        return dato != null
                && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getAforo() != null && dato.getAforo() > 0
                && dato.getIdSede() != null;
    }

    public List<Sala> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from SalaEntity x order by x.nombre, x.idSala",
                            SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Sala> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SalaEntity.class, id))
                    .map(SalaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Sala dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity entidad = SalaMapper.aEntidad(dato);
                entidad.setIdSala(null);
                em.persist(entidad);
                em.flush();
                dato.setIdSala(entidad.getIdSala());
                tx.commit();
                return dato.getIdSala();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Sala dato) {
        if (!validar(dato) || dato.getIdSala() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(SalaEntity.class, dato.getIdSala()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(SalaMapper.aEntidad(dato));
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("actualizar", ex);
                return 0;
            }
        }
    }

    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity existente = em.find(SalaEntity.class, id);
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

    public List<Object[]> salasConSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSala, s.nombre, s.aforo, d.nombre, d.municipio "
                                    + "from SalaEntity s "
                                    + "join SedeEntity d on d.idSede = s.idSede "
                                    + "order by d.nombre, s.nombre, s.idSala",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("salasConSede", ex);
            return List.of();
        }
    }

    public List<Sala> salasSinSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SalaEntity s "
                                    + "where not exists (select 1 from SesionEntity x "
                                    + "where x.idSala = s.idSala) "
                                    + "order by s.nombre, s.idSala",
                            SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("salasSinSesiones", ex);
            return List.of();
        }
    }

    public List<Object[]> ocupacionPorSala() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSala, s.nombre, s.aforo, count(e) "
                                    + "from SalaEntity s "
                                    + "left join SesionEntity x on x.idSala = s.idSala "
                                    + "left join EntradaEntity e on e.idSesion = x.idSesion "
                                    + "and e.estado in ('PAGADA','USADA') "
                                    + "group by s.idSala, s.nombre, s.aforo "
                                    + "order by s.nombre, s.idSala",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ocupacionPorSala", ex);
            return List.of();
        }
    }
}
