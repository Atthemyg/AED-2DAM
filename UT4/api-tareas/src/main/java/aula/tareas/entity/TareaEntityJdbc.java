package aula.tareas.entity;

public class TareaEntityJdbc {

    private long id;
    private String titulo;

    public TareaEntityJdbc(long id) {
        this.id = id;
    }

    public TareaEntityJdbc(long id, String titulo) {
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
