package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.modelo.Tarea;
import java.util.List;
import java.util.Optional;

/** Casos de uso de tareas */
public interface ITareaService {

    /**
     * Lista todas las tareas guardadas
     * @return lista de tareas o vacia si no hay ninguna
     */
    List<Tarea> listar();

    /**
     * Busca una tarea por su identificador
     * @param id identificador de la tarea
     * @return la tarea, o un Optional vacio si el ID no existe
     */
    Optional<Tarea> buscar(long id);

    /**
     * Crea una tarea
     * @param request datos de la nueva tarea
     * @return la tarea guardada con su identificador
     */
    Tarea crear(TareaRequest request);

    /**
     * Actualiza el título de una tarea
     * @param id      identificador de la tarea a modificar
     * @param request nuevos datos de la tarea
     * @return la tarea actualizada, o un Optional vacio si el ID no existe
     */
    Optional<Tarea> actualizar(long id, TareaRequest request);

    /**
     * Elimina una tarea.
     * @param id identificador de la tarea a eliminar
     * @return true si se elimino o false si el ID no existe
     */
    boolean eliminar(long id);
}