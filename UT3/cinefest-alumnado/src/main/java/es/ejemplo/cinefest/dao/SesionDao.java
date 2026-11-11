
package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SesionEntity;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.SesionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a datos de sesiones. */
public class SesionDao {

    private static final Logger LOG = Logger.getLogger(SesionDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SesionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SesionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    private boolean validar(Sesion dato) {
        return dato != null
                && dato.getIdPelicula() != null
                && dato.getIdSala() != null
                && dato.getFecha() != null
                && dato.getHora() != null
                && dato.getPrecio() != null && dato.getPrecio() >= 0
                && dato.getEstado() != null
                && List.of("PROGRAMADA", "CANCELADA", "FINALIZADA")
                .contains(dato.getEstado());
    }

    public List<Sesion> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select x from SesionEntity x "
                                    + "order by x.fecha, x.hora, x.idSesion",
                            SesionEntity.class)
                    .getResultStream()
                    .map(SesionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Sesion> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SesionEntity.class, id))
                    .map(SesionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Sesion dato) {
        if (!validar(dato)) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SesionEntity entidad = SesionMapper.aEntidad(dato);
                entidad.setIdSesion(null);
                em.persist(entidad);
                em.flush();
                dato.setIdSesion(entidad.getIdSesion());
                tx.commit();
                return dato.getIdSesion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Sesion dato) {
        if (!validar(dato) || dato.getIdSesion() == null) return 0;
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(SesionEntity.class, dato.getIdSesion()) == null) {
                    tx.commit();
                    return 0;
                }
                em.merge(SesionMapper.aEntidad(dato));
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
                SesionEntity existente = em.find(SesionEntity.class, id);
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

    public List<Object[]> sesionesConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, a.nombre, d.nombre, "
                                    + "s.fecha, s.hora, s.precio, s.estado "
                                    + "from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "join SedeEntity d on d.idSede = a.idSede "
                                    + "order by s.fecha, s.hora, s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesConDetalles", ex);
            return List.of();
        }
    }

    public List<Object[]> sesionesPorFecha(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, s.hora, s.precio, s.estado "
                                    + "from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "where s.fecha = :fecha "
                                    + "order by s.hora, s.idSesion",
                            Object[].class)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesPorFecha", ex);
            return List.of();
        }
    }

    public List<Object[]> sesionesSinVentas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, s.fecha, s.hora "
                                    + "from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "where not exists (select 1 from EntradaEntity e "
                                    + "where e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA','USADA')) "
                                    + "order by s.fecha, s.hora, s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesSinVentas", ex);
            return List.of();
        }
    }

    public List<Object[]> disponibilidadSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, a.aforo, count(e), "
                                    + "a.aforo - count(e) "
                                    + "from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "left join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA','USADA') "
                                    + "group by s.idSesion, p.titulo, a.aforo "
                                    + "order by s.fecha, s.hora, s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("disponibilidadSesiones", ex);
            return List.of();
        }
    }

    public List<Object[]> sesionesAgotadas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, s.fecha, s.hora, a.aforo "
                                    + "from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "where (select count(e) from EntradaEntity e "
                                    + "where e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA','USADA')) >= a.aforo "
                                    + "and s.estado = 'PROGRAMADA' "
                                    + "order by s.fecha, s.hora, s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesAgotadas", ex);
            return List.of();
        }
    }

    public List<Object[]> rankingRecaudacion() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo, count(e), sum(s.precio) "
                                    + "from PeliculaEntity p "
                                    + "join SesionEntity s on s.idPelicula = p.idPelicula "
                                    + "join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "where e.estado in ('PAGADA','USADA') "
                                    + "group by p.idPelicula, p.titulo "
                                    + "order by sum(s.precio) desc, p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingRecaudacion", ex);
            return List.of();
        }
    }

    public List<Object[]> ingresosPorSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.idSede, d.nombre, count(e), sum(s.precio) "
                                    + "from SedeEntity d "
                                    + "join SalaEntity a on a.idSede = d.idSede "
                                    + "join SesionEntity s on s.idSala = a.idSala "
                                    + "join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "where e.estado in ('PAGADA','USADA') "
                                    + "group by d.idSede, d.nombre "
                                    + "order by sum(s.precio) desc, d.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ingresosPorSede", ex);
            return List.of();
        }
    }

    public List<Object[]> informeDiario() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.fecha, count(e), sum(s.precio) "
                                    + "from SesionEntity s "
                                    + "join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "where e.estado in ('PAGADA','USADA') "
                                    + "group by s.fecha order by s.fecha",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("informeDiario", ex);
            return List.of();
        }
    }

    public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                em.createNativeQuery(
                                "UPDATE sesiones SET estado = estado WHERE id_sesion = :id")
                        .setParameter("id", idSesion)
                        .executeUpdate();
                SesionEntity sesion = em.find(SesionEntity.class, idSesion);
                EspectadorEntity espectador =
                        em.find(EspectadorEntity.class, idEspectador);
                if (sesion == null || espectador == null
                        || !"PROGRAMADA".equals(sesion.getEstado())
                        || espectador.getActivo() == null
                        || espectador.getActivo() != 1
                        || fechaCompra == null || fechaCompra.isBlank()) {
                    tx.rollback();
                    return 0;
                }
                Long vendidas = em.createQuery(
                                "select count(e) from EntradaEntity e "
                                        + "where e.idSesion = :id "
                                        + "and e.estado in ('PAGADA','USADA')",
                                Long.class)
                        .setParameter("id", idSesion)
                        .getSingleResult();
                SalaEntity sala = em.find(SalaEntity.class, sesion.getIdSala());
                if (sala == null || vendidas >= sala.getAforo()) {
                    tx.rollback();
                    return 0;
                }
                Long repetida = em.createQuery(
                                "select count(e) from EntradaEntity e "
                                        + "where e.idSesion = :sesion "
                                        + "and e.idEspectador = :espectador",
                                Long.class)
                        .setParameter("sesion", idSesion)
                        .setParameter("espectador", idEspectador)
                        .getSingleResult();
                if (repetida > 0) {
                    tx.rollback();
                    return 0;
                }
                EntradaEntity entrada = new EntradaEntity();
                entrada.setIdSesion(idSesion);
                entrada.setIdEspectador(idEspectador);
                entrada.setFechaCompra(fechaCompra);
                entrada.setEstado("PAGADA");
                em.persist(entrada);
                em.flush();
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("venderEntrada", ex);
                return 0;
            }
        }
    }
}
