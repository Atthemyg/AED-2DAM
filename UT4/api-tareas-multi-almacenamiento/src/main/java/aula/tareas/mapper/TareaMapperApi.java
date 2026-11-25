package aula.tareas.mapper;

import aula.tareas.api.TareaResponse;
import aula.tareas.modelo.Tarea;

public final class TareaMapperApi {

    private TareaMapperApi() {}

    public static TareaResponse toResponse(Tarea tarea) {
        return new TareaResponse(tarea.identificador(), tarea.nombre());
    }
}
