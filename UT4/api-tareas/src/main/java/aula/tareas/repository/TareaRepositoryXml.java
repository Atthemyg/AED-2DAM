package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryXml")
public class TareaRepositoryXml implements ITareaRepository {
    private final AtomicLong secuencia = new AtomicLong();
    private List<Tarea> lista = new ArrayList<>();

    @Override
    public Tarea create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new Tarea(id, titulo.strip());
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
    public List<Tarea> findAll() {
        return List.copyOf(lista);
    }

    @Override
    public Tarea findById(long identificador) {
        for (Tarea tarea : lista) {
            if (tarea.id() == identificador) {
                return tarea;
            }
        }
        return null;
    }

    @Override
    public Tarea update(Tarea tarea) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tarea.id()) {
                Tarea actualizada = new Tarea(tarea.id(), tarea.titulo().strip());
                lista.set(i, actualizada);
                return actualizada;
            }
        }
        return null;
    }
}