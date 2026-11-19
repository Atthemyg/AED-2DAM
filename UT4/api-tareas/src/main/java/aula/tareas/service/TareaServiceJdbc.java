package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceJdbc")
public class TareaServiceJdbc implements ITareaService {

    private final ITareaRepository repository;

    public TareaServiceJdbc(@Qualifier("repositoryJdbc") ITareaRepository tareaRepositoryJdbc) {
        this.repository = tareaRepositoryJdbc;
    }

    @Override
    public List<TareaRequest> listar() {
        return List.of();
    }

    @Override
    public TareaRequest buscar(long id) {
        return null;
    }

    @Override
    public TareaRequest crear(String titulo) {
        return null;
    }
}
