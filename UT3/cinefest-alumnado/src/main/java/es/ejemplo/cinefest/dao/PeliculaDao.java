
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;
import es.ejemplo.cinefest.orm.mapper.PeliculaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de películas. */
public class PeliculaDao {

    private static final Logger LOG = Logger.getLogger(PeliculaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public PeliculaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public PeliculaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Pelicula dato) {
        return dato != null
                && dato.getTitulo() != null && !dato.getTitulo().isBlank()
                && dato.getGenero() != null && !dato.getGenero().isBlank()
                && dato.getDuracionMin() != null && dato.getDuracionMin() > 0
                && dato.getPais() != null
                && dato.getAnio() != null;
    }

    public List<Pelicula> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from PeliculaEntity x order by x.titulo, x.idPelicula",
                            PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Pelicula> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PeliculaEntity.class, id))
                    .map(PeliculaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Pelicula dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity entidad = PeliculaMapper.aEntidad(dato);
                entidad.setIdPelicula(null);
                em.persist(entidad);
                em.flush();
                dato.setIdPelicula(entidad.getIdPelicula());
                tx.commit();
                return dato.getIdPelicula();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Pelicula dato) {
        if (!validar(dato) || dato.getIdPelicula() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(PeliculaEntity.class, dato.getIdPelicula()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(PeliculaMapper.aEntidad(dato));
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
                PeliculaEntity existente = em.find(PeliculaEntity.class, id);
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

    public List<Pelicula> buscarPorGenero(String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p "
                                    + "where p.genero = :genero "
                                    + "order by p.titulo, p.idPelicula",
                            PeliculaEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorGenero", ex);
            return List.of();
        }
    }

    public List<Pelicula> peliculasDesdeAnio(int anio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p "
                                    + "where p.anio >= :anio "
                                    + "order by p.anio, p.titulo, p.idPelicula",
                            PeliculaEntity.class)
                    .setParameter("anio", anio)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("peliculasDesdeAnio", ex);
            return List.of();
        }
    }

    public List<Pelicula> peliculasSinSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p "
                                    + "where not exists (select 1 from SesionEntity s "
                                    + "where s.idPelicula = p.idPelicula) "
                                    + "order by p.titulo, p.idPelicula",
                            PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("peliculasSinSesiones", ex);
            return List.of();
        }
    }

    public List<Object[]> peliculasConMediaValoraciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo, avg(v.puntuacion), count(v) "
                                    + "from PeliculaEntity p "
                                    + "left join ValoracionEntity v on v.idPelicula = p.idPelicula "
                                    + "group by p.idPelicula, p.titulo "
                                    + "order by p.titulo, p.idPelicula",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("peliculasConMediaValoraciones", ex);
            return List.of();
        }
    }

    public List<Object[]> mejorValoradas(int minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo, avg(v.puntuacion), count(v) "
                                    + "from PeliculaEntity p "
                                    + "join ValoracionEntity v on v.idPelicula = p.idPelicula "
                                    + "group by p.idPelicula, p.titulo "
                                    + "having count(v) >= :minimo "
                                    + "order by avg(v.puntuacion) desc, count(v) desc, p.titulo",
                            Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("mejorValoradas", ex);
            return List.of();
        }
    }

    public List<Object[]> masVendidasQueMedia() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo, count(e) "
                                    + "from PeliculaEntity p "
                                    + "join SesionEntity s on s.idPelicula = p.idPelicula "
                                    + "join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA','USADA') "
                                    + "group by p.idPelicula, p.titulo "
                                    + "having count(e) > ("
                                    + "select count(x) * 1.0 / count(distinct y.idPelicula) "
                                    + "from EntradaEntity x "
                                    + "join SesionEntity y on y.idSesion = x.idSesion "
                                    + "where x.estado in ('PAGADA','USADA')) "
                                    + "order by count(e) desc, p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("masVendidasQueMedia", ex);
            return List.of();
        }
    }

    public List<Object[]> estadisticasGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.genero, count(distinct p), "
                                    + "count(distinct s), count(distinct e) "
                                    + "from PeliculaEntity p "
                                    + "left join SesionEntity s on s.idPelicula = p.idPelicula "
                                    + "left join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA','USADA') "
                                    + "group by p.genero order by p.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("estadisticasGenero", ex);
            return List.of();
        }
    }
}
