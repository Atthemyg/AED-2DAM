package aula.tareas.repository;

import aula.tareas.entity.TareaEntityXml;
import aula.tareas.mapper.TareaMapperXml;
import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("tareaRepositoryXml")
public class TareaRepositoryXml extends RepositorioTareaFichero {
    private final Path path;
    private final XmlMapper mapper = XmlMapper.builder()
            .defaultUseWrapper(false)
            .build();

    public TareaRepositoryXml() {
        this(Path.of("data", "tarea.xml"));
    }

    public TareaRepositoryXml(Path path) {
        this.path = path;
    }

    @JsonRootName("tareas")
    public static class DocumentoXml {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "tarea")
        private List<TareaEntityXml> tareas;

        public DocumentoXml() {}

        public DocumentoXml(List<TareaEntityXml> tareas) {
            this.tareas = tareas;
        }

        public List<TareaEntityXml> getTareas() {
            return tareas;
        }

        public void setTareas(List<TareaEntityXml> tareas) {
            this.tareas = tareas;
        }
    }

    @Override
    List<Tarea> readAll() {
        List<Tarea> result = new ArrayList<>();
        if (path.toFile().length() == 0) {
            return result;
        }
        DocumentoXml documento = mapper.readValue(path.toFile(), DocumentoXml.class);
        if (documento.getTareas() != null) {
            for (TareaEntityXml entity : documento.getTareas()) {
                result.add(TareaMapperXml.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    void writeAll(List<Tarea> tareas) {
        DocumentoXml documento = new DocumentoXml(new ArrayList<>());
        for (Tarea tarea : tareas) {
            documento.getTareas().add(TareaMapperXml.toEntity(tarea));
        }
        mapper.writeValue(path.toFile(), documento);
    }
}
