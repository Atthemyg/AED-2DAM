package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryXml")
public class TareaRepositoryXml implements ITareaRepository {
    private final AtomicLong secuencia = new AtomicLong();
    private List<TareaRequest> lista = new ArrayList<>();

    @Override
    public TareaRequest create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new TareaRequest(id, titulo.strip());
        lista.add(tarea);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == identificador) {
                lista.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<TareaRequest> findAll() {
        return List.copyOf(lista);
    }

    @Override
    public TareaRequest findById(long identificador) {
        for (TareaRequest tareaRequest : lista) {
            if (tareaRequest.id() == identificador) {
                return tareaRequest;
            }
        }
        return null;
    }

    @Override
    public TareaRequest update(TareaRequest tareaRequest) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tareaRequest.id()) {
                TareaRequest actualizada = new TareaRequest(tareaRequest.id(), tareaRequest.titulo().strip());
                lista.set(i, actualizada);
                return actualizada;
            }
        }
        return null;
    }
}