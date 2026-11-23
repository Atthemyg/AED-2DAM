package aula.tareas.modelo;

public class Tarea {

    private long identificador;
    private String title;

    public Tarea(long id) {
        this.identificador = id;
    }

    public Tarea(long id, String title) {
        this.identificador = id;
        this.title = title;
    }

    public long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(long identificador) {
        this.identificador = identificador;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
