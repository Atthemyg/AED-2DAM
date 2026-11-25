package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityJdbc;
import aula.tareas.modelo.Tarea;

public final class TareaMapperJdbc {
    private TareaMapperJdbc() {}

    public static TareaEntityJdbc toEntity(Tarea tarea) {
        return new TareaEntityJdbc(tarea.identificador(), tarea.nombre());
    }

    public static Tarea toDomain(TareaEntityJdbc entity) {
        return new Tarea(entity.idTarea(), entity.nombreTarea());
    }
}
