
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.EntradaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de entrada. */
public class EntradaDao {
    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public EntradaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public EntradaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Entrada dato) {
        return dato != null
                && dato.getIdSesion() != null
                && dato.getIdEspectador() != null
                && dato.getFechaCompra() != null
                && dato.getEstado() != null
                && List.of("PAGADA", "USADA", "CANCELADA").contains(dato.getEstado());
    }

    public List<Entrada> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from EntradaEntity x order by x.fechaCompra, x.idEntrada",
                            EntradaEntity.class)
                    .getResultStream()
                    .map(EntradaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Entrada> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(EntradaEntity.class, id))
                    .map(EntradaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Entrada dato) {
        if (!validar(dato)) return 0;

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity entidad = EntradaMapper.aEntidad(dato);
                entidad.setIdEntrada(null);
                em.persist(entidad);
                em.flush();
                dato.setIdEntrada(entidad.getIdEntrada());
                tx.commit();
                return dato.getIdEntrada();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Entrada dato) {
        if (!validar(dato) || dato.getIdEntrada() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(EntradaEntity.class, dato.getIdEntrada()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(EntradaMapper.aEntidad(dato));
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
                EntradaEntity existente = em.find(EntradaEntity.class, id);
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

    public List<Object[]> entradasConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x.idEntrada, p.titulo, s.fecha, s.hora, e.nombre, "
                                    + "x.fechaCompra, x.estado "
                                    + "from EntradaEntity x "
                                    + "join SesionEntity s on s.idSesion = x.idSesion "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "join EspectadorEntity e on e.idEspectador = x.idEspectador "
                                    + "order by x.idEntrada",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("entradasConDetalles", ex);
            return List.of();
        }
    }

    public List<Object[]> entradasPorEspectador(int idEspectador) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x.idEntrada, p.titulo, s.fecha, s.hora, "
                                    + "x.fechaCompra, x.estado "
                                    + "from EntradaEntity x "
                                    + "join SesionEntity s on s.idSesion = x.idSesion "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "where x.idEspectador = :id "
                                    + "order by s.fecha, s.hora, x.idEntrada",
                            Object[].class)
                    .setParameter("id", idEspectador)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("entradasPorEspectador", ex);
            return List.of();
        }
    }

    public List<Object[]> ventasPorDia() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x.fechaCompra, count(x), sum(s.precio) "
                                    + "from EntradaEntity x "
                                    + "join SesionEntity s on s.idSesion = x.idSesion "
                                    + "where x.estado in ('PAGADA','USADA') "
                                    + "group by x.fechaCompra "
                                    + "order by x.fechaCompra",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ventasPorDia", ex);
            return List.of();
        }
    }

    public int cancelarEntrada(int idEntrada) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity entrada = em.find(EntradaEntity.class, idEntrada);
                if (entrada == null || "CANCELADA".equals(entrada.getEstado())) {
                    tx.rollback();
                    return 0;
                }
                entrada.setEstado("CANCELADA");
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("cancelarEntrada", ex);
                return 0;
            }
        }
    }
}
