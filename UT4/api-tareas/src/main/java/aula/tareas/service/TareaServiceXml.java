package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceXml")
public class TareaServiceXml implements ITareaService {

    private final ITareaRepository repository;

    public TareaServiceXml(@Qualifier("repositoryXml") ITareaRepository repository) {
        this.repository = repository;
    }

    public List<TareaRequest> listar() {
        return repository.findAll();
    }

    public TareaRequest buscar(long id) {
        return repository.findById(id);
    }

    public TareaRequest crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return repository.create(titulo);
    }

    public TareaRequest actualizar(long id, String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return repository.update(new TareaRequest(id, titulo));
    }

    public boolean borrar(long id) {
        return repository.delete(id);
    }
}