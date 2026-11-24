package aula.tareas.entity;

import jakarta.persistence.*;

@Entity(name="tarea_orm")
public class TareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String titulo;

    public TareaEntity() {
    }

    public TareaEntity(long id) {
        this.id = id;
    }

    public TareaEntity(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
