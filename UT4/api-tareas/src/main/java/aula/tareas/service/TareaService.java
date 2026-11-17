package aula.tareas.service;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import aula.tareas.repository.TareaRepository;
import org.springframework.stereotype.Service;

@Service
public class TareaService {
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, Tarea> tareas = new ConcurrentSkipListMap<>();
    private final ITareaRepository repository;

    // Pasado
    /**public TareaService() {
        this.repository = new TareaRepository();
    }**/

    // Spring
    public TareaService(ITareaRepository repository) {
        this.repository = repository;
    }


    public List<Tarea> listar() {
        return repository.findAll();
    }

    public Tarea buscar(long id) {
        return repository.findById(id);
    }

    public Tarea crear(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return null;
        }
        return repository.create(titulo);
    }
}
