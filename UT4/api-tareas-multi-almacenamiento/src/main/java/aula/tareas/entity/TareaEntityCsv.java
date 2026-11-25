package aula.tareas.entity;

import jakarta.persistence.*;

public class TareaEntityCsv {

    private Long idTarea;
    private String nombreTarea;

    public TareaEntityCsv() {}

    public TareaEntityCsv(Long idTarea) {
        this.idTarea = idTarea;
    }

    public TareaEntityCsv(Long idTarea, String nombreTarea) {
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
