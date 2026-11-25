package aula.tareas.entity;

import jakarta.persistence.*;

@Entity(name="tareaEntityOrm")
public class TareaEntityOrm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarea;

    @Column
    private String nombreTarea;

    public TareaEntityOrm() {
    }

    public TareaEntityOrm(Long idTarea) {
        this.idTarea = idTarea;
    }

    public TareaEntityOrm(Long idTarea, String nombreTarea) {
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
