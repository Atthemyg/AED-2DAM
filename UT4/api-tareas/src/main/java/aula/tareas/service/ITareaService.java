package aula.tareas.service;

import aula.tareas.modelo.Tarea;

import java.util.List;

public interface ITareaService {

    /**
     * Soy la documentacion del metodo
     * @return
     */
    public List<Tarea> listar();

    /**
     * Tambien soy la documentacion del metodo
     * @param id
     * @return
     */
    public Tarea buscar(long id);

    /**
     * Otra documentacion del metodo
     * @param titulo
     * @return
     */
    public Tarea crear(String titulo);

}
