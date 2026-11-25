package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityOrm;
import aula.tareas.modelo.Tarea;

public final class TareaMapperOrm {
    private TareaMapperOrm() {}

    public static TareaEntityOrm toEntity(Tarea tarea) {
        return new TareaEntityOrm(tarea.identificador(), tarea.nombre());
    }

    public static Tarea toDomain(TareaEntityOrm entity) {
        return new Tarea(entity.getIdTarea(), entity.getNombreTarea());
    }
}
