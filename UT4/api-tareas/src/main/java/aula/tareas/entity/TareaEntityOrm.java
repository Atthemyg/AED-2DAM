package aula.tareas.entity;

//@Entity(name="tarea_orm")
public class TareaEntityOrm {

    private long id;
    private String titulo;

    public TareaEntityOrm(long id) {
        this.id = id;
    }

    public TareaEntityOrm(long id, String titulo) {
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
