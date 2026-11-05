package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Vehiculo;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.entity.VehiculoEntity;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import es.ejemplo.taller.orm.mapper.VehiculoMapper;
import jakarta.persistence.EntityManagerFactory;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class VehiculoDao  {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public VehiculoDao(String url) {
        this.url = url;
        managerFactory = OrmFactory.crear(url);
    }

    public List<Vehiculo> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select v from VehiculoEntity v",
                            VehiculoEntity.class)
                    .getResultStream()
                    .map(VehiculoMapper::toDomain)
                    .toList();
        }
    }

    public Optional<Vehiculo> buscarPorId(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(VehiculoEntity.class, id))
                    .map(VehiculoMapper::toDomain);
        }
    }

    public int insertar(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                VehiculoEntity entidad = VehiculoMapper.toEntity(vehiculo);
                em.persist(entidad);
                vehiculo.setMatricula(entidad.getMatricula());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }

    public int actualizar(Vehiculo vehiculo) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                VehiculoEntity c = em.find(VehiculoEntity.class, vehiculo.getMatricula());
                if (c == null) {
                    tx.commit();
                    return 0; // false
                }
                VehiculoEntity vehiculoEntityActualizado = VehiculoMapper.toEntity(vehiculo);
                c.setMarca(vehiculoEntityActualizado.getMarca());
                c.setModelo(vehiculoEntityActualizado.getModelo());
                c.setAnio(vehiculoEntityActualizado.getAnio());
                tx.commit();           // Hibernate detecta el cambio
                return 1; // true
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public int eliminar(String id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                VehiculoEntity v = em.find(VehiculoEntity.class, id);
                if (v == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(v);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public List<Vehiculo> buscarPorCliente(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select v from VehiculoEntity v where v.idCliente = :id",
                            VehiculoEntity.class).setParameter("id", id).getResultStream()
                    .map(VehiculoMapper::toDomain).toList();
        }
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select v from VehiculoEntity v where v.marca = :marca",
                            VehiculoEntity.class).setParameter("marca", marca).getResultStream()
                    .map(VehiculoMapper::toDomain).toList();
        }
    }
}