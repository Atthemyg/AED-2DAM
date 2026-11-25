package aula.tareas.entity;

public class TareaEntityXml {
    private Long idTarea;
    private String nombreTarea;

    public TareaEntityXml() {}

    public TareaEntityXml(Long idTarea) {
        this.idTarea = idTarea;
    }

    public TareaEntityXml(Long idTarea, String nombreTarea) {
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
