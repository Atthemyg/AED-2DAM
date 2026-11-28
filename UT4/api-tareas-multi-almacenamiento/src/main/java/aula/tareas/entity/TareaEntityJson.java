package aula.tareas.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TareaEntityJson {

    @JsonProperty("id")
    private Long idTarea;

    @JsonProperty("titulo")
    private String nombreTarea;

    public TareaEntityJson() {}

    public TareaEntityJson(Long idTarea, String nombreTarea) {
        this.idTarea = idTarea;
        this.nombreTarea = nombreTarea;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }
}