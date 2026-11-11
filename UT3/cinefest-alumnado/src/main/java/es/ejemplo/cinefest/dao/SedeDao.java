
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SedeEntity;
import es.ejemplo.cinefest.orm.mapper.SedeMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de sedes. */
public class SedeDao{

    private static final Logger LOG = Logger.getLogger(SedeDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SedeDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SedeDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sede dato) {
        return dato != null
                && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getMunicipio() != null && !dato.getMunicipio().isBlank();
    }

    public List<Sede> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from SedeEntity x order by x.nombre, x.idSede",
                            SedeEntity.class)
                    .getResultStream()
                    .map(SedeMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Sede> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SedeEntity.class, id))
                    .map(SedeMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Sede dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity entidad = SedeMapper.aEntidad(dato);
                entidad.setIdSede(null);
                em.persist(entidad);
                em.flush();
                dato.setIdSede(entidad.getIdSede());
                tx.commit();
                return dato.getIdSede();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Sede dato) {
        if (!validar(dato) || dato.getIdSede() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(SedeEntity.class, dato.getIdSede()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(SedeMapper.aEntidad(dato));
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
                SedeEntity existente = em.find(SedeEntity.class, id);
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

    public List<Object[]> salasPorSede(int idSede) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSala, s.nombre, s.aforo "
                                    + "from SalaEntity s "
                                    + "where s.idSede = :id "
                                    + "order by s.nombre, s.idSala",
                            Object[].class)
                    .setParameter("id", idSede)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("salasPorSede", ex);
            return List.of();
        }
    }

    public List<Object[]> aforoTotalPorSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.idSede, d.nombre, coalesce(sum(s.aforo), 0) "
                                    + "from SedeEntity d "
                                    + "left join SalaEntity s on s.idSede = d.idSede "
                                    + "group by d.idSede, d.nombre "
                                    + "order by d.nombre, d.idSede",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("aforoTotalPorSede", ex);
            return List.of();
        }
    }
}
