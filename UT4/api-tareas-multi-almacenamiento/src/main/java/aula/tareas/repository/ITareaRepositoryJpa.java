package aula.tareas.repository;

import aula.tareas.entity.TareaEntityOrm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA sobre la tabla tareas_orm
 * Hereda findAll, findById, existsById, save y deleteById
 * findById devuelve un Optional vacio si el ID no existe
 * save con ID nulo inserta y devuelve la entidad con el ID generado
 */
public interface ITareaRepositoryJpa extends JpaRepository<TareaEntityOrm, Long> {
}