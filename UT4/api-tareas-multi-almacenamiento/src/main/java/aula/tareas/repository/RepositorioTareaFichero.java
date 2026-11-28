package aula.tareas.repository;

import aula.tareas.modelo.Tarea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** CRUD compartido por los formatos de fichero, con lectura del fichero en cada operación. */
abstract class RepositorioTareaFichero implements ITareaRepository {

    abstract List<Tarea> readAll();
    abstract void writeAll(List<Tarea> tareas);

    @Override
    public synchronized List<Tarea> findAll() {
        return readAll();
    }

    @Override
    public synchronized Optional<Tarea> findById(long id) {
        for (Tarea tarea : readAll()) {
            if (tarea.identificador() != null && tarea.identificador() == id) {
                return Optional.of(tarea);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Tarea create(Tarea tarea) {
        List<Tarea> tareas = new ArrayList<>(readAll());
        long maximo = 0;
        for (Tarea existente : tareas) {
            if (existente.identificador() != null && existente.identificador() > maximo) {
                maximo = existente.identificador();
            }
        }
        Tarea nueva = new Tarea(maximo + 1, tarea.nombre().strip());
        tareas.add(nueva);
        writeAll(tareas);
        return nueva;
    }

    @Override
    public synchronized Optional<Tarea> update(Tarea tarea) {
        List<Tarea> tareas = new ArrayList<>(readAll());
        for (int i = 0; i < tareas.size(); i++) {
            Long actual = tareas.get(i).identificador();
            if (actual != null && actual.equals(tarea.identificador())) {
                Tarea actualizada = new Tarea(actual, tarea.nombre().strip());
                tareas.set(i, actualizada);
                writeAll(tareas);
                return Optional.of(actualizada);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized boolean delete(long id) {
        List<Tarea> tareas = new ArrayList<>(readAll());
        for (int i = 0; i < tareas.size(); i++) {
            Long actual = tareas.get(i).identificador();
            if (actual != null && actual == id) {
                tareas.remove(i);
                writeAll(tareas);
                return true;
            }
        }
        return false;
    }
}