package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de tareas */
public interface ITareaRepository {

    /**
     * Devuelve todas las tareas guardadas
     * @return lista de tareas o vacia si no hay ninguna
     */
    List<Tarea> findAll();

    /**
     * Busca una tarea por su identificador
     * @param id identificador de la tarea
     * @return la tarea, o un Optional vacio si el ID no existe
     */
    Optional<Tarea> findById(long id);

    /**
     * Guarda una tarea nueva
     * @param tarea tarea a guardar
     * @return la tarea guardada con su identificador
     */
    Tarea create(Tarea tarea);

    /**
     * Modifica una tarea
     * @param tarea tarea con el identificador y los nuevos datos
     * @return la tarea actualizada, o un Optional vacio si el ID no existe
     */
    Optional<Tarea> update(Tarea tarea);

    /**
     * Elimina una tarea
     * @param id identificador de la tarea a eliminar
     * @return true si se elimino o false si el ID no existe
     */
    boolean delete(long id);
}