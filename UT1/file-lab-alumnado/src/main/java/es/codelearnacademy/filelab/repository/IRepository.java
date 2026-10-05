package es.codelearnacademy.filelab.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {

    /**
     * Encuentra todas las entidades
     * @return Lista de todas las entidades
     */
    List<T> findAll();

    /**
     * Encuentra una entidad por su id
     * @param id id de la entidad
     * @return La entidad con id concreto
     */
    Optional<T> findById(ID id);

    /**
     * Crea una entidad
     * @param entity entidad a crear
     * @return Entidad creada
     */
    boolean create(T entity);

    /**
     * Actualiza una entidad
     * @param entity entidad a actualizar
     * @return Entidad actualizada
     */
    boolean update(T entity);

    /**
     * Elimina una entidad por su id
     * @param id id de la entidad
     * @return Entidad eliminada
     */
    boolean delete(ID id);
}
