
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.mapper.EspectadorMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de espectadores. */
public class EspectadorDao {

    private static final Logger LOG = Logger.getLogger(EspectadorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public EspectadorDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public EspectadorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Espectador dato) {
        return dato != null
                && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getEmail() != null && !dato.getEmail().isBlank()
                && dato.getFechaAlta() != null
                && dato.getActivo() != null
                && (dato.getActivo() == 0 || dato.getActivo() == 1);
    }

    public List<Espectador> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from EspectadorEntity x order by x.nombre, x.idEspectador",
                            EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Espectador> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(EspectadorEntity.class, id))
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Espectador dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EspectadorEntity entidad = EspectadorMapper.aEntidad(dato);
                entidad.setIdEspectador(null);
                em.persist(entidad);
                em.flush();
                dato.setIdEspectador(entidad.getIdEspectador());
                tx.commit();
                return dato.getIdEspectador();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Espectador dato) {
        if (!validar(dato) || dato.getIdEspectador() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(EspectadorEntity.class, dato.getIdEspectador()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(EspectadorMapper.aEntidad(dato));
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
                EspectadorEntity existente = em.find(EspectadorEntity.class, id);
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

    public Optional<Espectador> buscarPorEmail(String email) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e from EspectadorEntity e where e.email = :email",
                            EspectadorEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorEmail", ex);
            return Optional.empty();
        }
    }

    public List<Espectador> espectadoresSinEntradas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e from EspectadorEntity e "
                                    + "where not exists (select 1 from EntradaEntity x "
                                    + "where x.idEspectador = e.idEspectador) "
                                    + "order by e.nombre, e.idEspectador",
                            EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("espectadoresSinEntradas", ex);
            return List.of();
        }
    }

    public List<Object[]> rankingEspectadores() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e.idEspectador, e.nombre, count(x) "
                                    + "from EspectadorEntity e "
                                    + "join EntradaEntity x on x.idEspectador = e.idEspectador "
                                    + "where x.estado in ('PAGADA','USADA') "
                                    + "group by e.idEspectador, e.nombre "
                                    + "order by count(x) desc, e.idEspectador",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingEspectadores", ex);
            return List.of();
        }
    }

    public List<Object[]> espectadoresVariasSedes(int minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e.idEspectador, e.nombre, count(distinct d.idSede) "
                                    + "from EspectadorEntity e "
                                    + "join EntradaEntity x on x.idEspectador = e.idEspectador "
                                    + "join SesionEntity s on s.idSesion = x.idSesion "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "join SedeEntity d on d.idSede = a.idSede "
                                    + "where x.estado in ('PAGADA','USADA') "
                                    + "group by e.idEspectador, e.nombre "
                                    + "having count(distinct d.idSede) >= :minimo "
                                    + "order by count(distinct d.idSede) desc, e.idEspectador",
                            Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("espectadoresVariasSedes", ex);
            return List.of();
        }
    }
}
