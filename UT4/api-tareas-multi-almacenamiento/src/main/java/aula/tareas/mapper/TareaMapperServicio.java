package aula.tareas.mapper;

import aula.tareas.api.TareaRequest;
import aula.tareas.modelo.Tarea;

public final class TareaMapperServicio {
    private TareaMapperServicio() {}

    // Identificador queda nulo para que lo asigne el almacenamiento
    public static Tarea toDomain(TareaRequest request) {
        return new Tarea(null, request.titulo());
    }

    // Identificador de la URL manda sobre el del cuerpo
    public static Tarea toDomain(long id, TareaRequest request) {
        return new Tarea(id, request.titulo());
    }
}
