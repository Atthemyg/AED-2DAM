package aula.tareas.service;

import aula.tareas.api.TareaRequest;

import java.util.List;

public interface ITareaService {

    /**
     * Soy la documentacion del metodo
     * @return
     */
    public List<TareaRequest> listar();

    /**
     * Tambien soy la documentacion del metodo
     * @param id
     * @return
     */
    public TareaRequest buscar(long id);

    /**
     * Otra documentacion del metodo
     * @param titulo
     * @return
     */
    public TareaRequest crear(String titulo);

}
