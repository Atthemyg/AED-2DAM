
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;
import es.ejemplo.cinefest.orm.mapper.ValoracionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de valoraciones. */
public class ValoracionDao {

    private static final Logger LOG = Logger.getLogger(ValoracionDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ValoracionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public ValoracionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Valoracion dato) {
        return dato != null
                && dato.getIdPelicula() != null
                && dato.getIdEspectador() != null
                && dato.getPuntuacion() != null
                && dato.getPuntuacion() >= 1
                && dato.getPuntuacion() <= 5
                && dato.getFecha() != null;
    }

    public List<Valoracion> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from ValoracionEntity x "
                                    + "order by x.fecha, x.idValoracion",
                            ValoracionEntity.class)
                    .getResultStream()
                    .map(ValoracionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Valoracion> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ValoracionEntity.class, id))
                    .map(ValoracionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Valoracion dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ValoracionEntity entidad = ValoracionMapper.aEntidad(dato);
                entidad.setIdValoracion(null);
                em.persist(entidad);
                em.flush();
                dato.setIdValoracion(entidad.getIdValoracion());
                tx.commit();
                return dato.getIdValoracion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Valoracion dato) {
        if (!validar(dato) || dato.getIdValoracion() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(ValoracionEntity.class, dato.getIdValoracion()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(ValoracionMapper.aEntidad(dato));
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
                ValoracionEntity existente = em.find(ValoracionEntity.class, id);
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

    public List<Object[]> valoracionesConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select v.idValoracion, p.titulo, e.nombre, "
                                    + "v.puntuacion, v.comentario, v.fecha "
                                    + "from ValoracionEntity v "
                                    + "join PeliculaEntity p on p.idPelicula = v.idPelicula "
                                    + "join EspectadorEntity e on e.idEspectador = v.idEspectador "
                                    + "order by v.fecha, v.idValoracion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("valoracionesConDetalles", ex);
            return List.of();
        }
    }

    public List<Object[]> peliculasSinValoraciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo "
                                    + "from PeliculaEntity p "
                                    + "where not exists (select 1 from ValoracionEntity v "
                                    + "where v.idPelicula = p.idPelicula) "
                                    + "order by p.titulo, p.idPelicula",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("peliculasSinValoraciones", ex);
            return List.of();
        }
    }

    public List<Object[]> valoracionesPorPelicula(int idPelicula) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select v.idValoracion, e.nombre, v.puntuacion, "
                                    + "v.comentario, v.fecha "
                                    + "from ValoracionEntity v "
                                    + "join EspectadorEntity e on e.idEspectador = v.idEspectador "
                                    + "where v.idPelicula = :id "
                                    + "order by v.fecha, v.idValoracion",
                            Object[].class)
                    .setParameter("id", idPelicula)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("valoracionesPorPelicula", ex);
            return List.of();
        }
    }

    public List<Object[]> estadisticasPuntuaciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, count(v), avg(v.puntuacion), "
                                    + "min(v.puntuacion), max(v.puntuacion) "
                                    + "from PeliculaEntity p "
                                    + "left join ValoracionEntity v on v.idPelicula = p.idPelicula "
                                    + "group by p.idPelicula, p.titulo "
                                    + "order by p.titulo, p.idPelicula",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("estadisticasPuntuaciones", ex);
            return List.of();
        }
    }
}
