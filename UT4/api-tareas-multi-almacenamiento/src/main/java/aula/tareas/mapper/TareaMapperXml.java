package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityXml;
import aula.tareas.modelo.Tarea;

public final class TareaMapperXml {
    private TareaMapperXml() {}

    public static TareaEntityXml toEntity(Tarea tarea) {
        return new TareaEntityXml(tarea.identificador(), tarea.nombre());
    }

    public static Tarea toDomain(TareaEntityXml entity) {
        return new Tarea(entity.getIdTarea(), entity.getNombreTarea());
    }
}
