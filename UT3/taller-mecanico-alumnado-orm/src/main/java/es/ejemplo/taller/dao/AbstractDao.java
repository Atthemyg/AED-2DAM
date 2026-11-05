package es.ejemplo.taller.dao;

import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class AbstractDao<T, E, ID> {
    private final EntityManagerFactory managerFactory;

    public AbstractDao(String url) {
        managerFactory = OrmFactory.crear(url);
    }

    protected List<T> findAll(String sql) {
        /*try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(sql, E.class).getResultStream().map(ClienteMapper::toDomain).toList();
    }*/
        return null;
    }
}
