package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityCsv;
import aula.tareas.modelo.Tarea;

public final class TareaMapperCsv {
    private TareaMapperCsv() {}

    public static TareaEntityCsv toEntity(Tarea tarea) {
        return new TareaEntityCsv(tarea.identificador(), tarea.nombre());
    }

    public static Tarea toDomain(TareaEntityCsv entity) {
        return new Tarea(entity.getIdTarea(), entity.getNombreTarea());
    }
}
