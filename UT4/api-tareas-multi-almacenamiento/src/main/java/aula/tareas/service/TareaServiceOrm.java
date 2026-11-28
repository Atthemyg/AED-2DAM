package aula.tareas.service;

import aula.tareas.api.TareaRequest;
import aula.tareas.mapper.TareaMapperServicio;
import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TareaServiceOrm implements ITareaService {
    private final ITareaRepository repository;

    public TareaServiceOrm(@Qualifier("tareaRepositoryOrm") ITareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tarea> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<Tarea> buscar(long id) {
        return repository.findById(id);
    }

    @Override
    public Tarea crear(TareaRequest request) {
        Tarea tarea = TareaMapperServicio.toDomain(request);
        return repository.create(tarea);
    }

    @Override
    public Optional<Tarea> actualizar(long id, TareaRequest request) {
        Tarea tarea = TareaMapperServicio.toDomain(id, request);
        return repository.update(tarea);
    }

    @Override
    public boolean eliminar(long id) {
        return repository.delete(id);
    }
}
