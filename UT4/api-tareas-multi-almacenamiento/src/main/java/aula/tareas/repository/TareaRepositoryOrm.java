package aula.tareas.repository;

import aula.tareas.entity.TareaEntityOrm;
import aula.tareas.mapper.TareaMapperOrm;
import aula.tareas.modelo.Tarea;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("tareaRepositoryOrm")
public class TareaRepositoryOrm implements ITareaRepository {

    private final ITareaRepositoryJpa jpa;

    public TareaRepositoryOrm(ITareaRepositoryJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Tarea> findAll() {
        List<Tarea> tareas = new ArrayList<>();
        for (TareaEntityOrm entity : jpa.findAll()) {
            tareas.add(TareaMapperOrm.toDomain(entity));
        }
        return tareas;
    }

    @Override
    public Optional<Tarea> findById(long id) {
        Optional<TareaEntityOrm> entity = jpa.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TareaMapperOrm.toDomain(entity.get()));
    }

    @Override
    public Tarea create(Tarea tarea) {
        TareaEntityOrm guardada = jpa.save(TareaMapperOrm.toEntity(tarea));
        return TareaMapperOrm.toDomain(guardada);
    }

    @Override
    public Optional<Tarea> update(Tarea tarea) {
        if (tarea.identificador() == null || !jpa.existsById(tarea.identificador())) {
            return Optional.empty();
        }
        TareaEntityOrm guardada = jpa.save(TareaMapperOrm.toEntity(tarea));
        return Optional.of(TareaMapperOrm.toDomain(guardada));
    }

    @Override
    public boolean delete(long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }
}