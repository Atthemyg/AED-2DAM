package aula.tareas.mapper;

import aula.tareas.entity.TareaEntityJson;
import aula.tareas.modelo.Tarea;

public final class TareaMapperJson {
    private TareaMapperJson() {}

    public static TareaEntityJson toEntity(Tarea tarea) {
        return new TareaEntityJson(tarea.identificador(), tarea.nombre());
    }

    public static Tarea toDomain(TareaEntityJson entity) {
        return new Tarea(entity.getIdTarea(), entity.getNombreTarea());
    }

    //SE PUEDE HACER ASI TAMBIEN
    /**@Component("tareaMapperJson")
    public class TareaMapperJson {
    private TareaMapperJson() {}

    public TareaEntityJson toEntity(Tarea tarea) {
    // TODO: Convertir al tipo de destino.
    throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public Tarea toDomain(TareaEntityJson entity) {
    // TODO: Convertir al tipo de destino.
    throw new UnsupportedOperationException("Pendiente de implementar");
    }*/
}
