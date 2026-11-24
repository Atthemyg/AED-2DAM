package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceOrm")
public class TareaServiceOrm implements ITareaService {

    private final ITareaRepository repository;

    public TareaServiceOrm(@Qualifier("repositoryOrm") ITareaRepository tareaRepositoryJdbc) {
        this.repository = tareaRepositoryJdbc;
    }

    @Override
    public List<TareaRequest> listar() {
        return repository.findAll();
    }

    @Override
    public TareaRequest buscar(long id) {
        return repository.findById(id);
    }

    @Override
    public TareaRequest crear(String titulo) {
        return repository.create(titulo);
    }
}
