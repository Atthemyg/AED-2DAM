package es.codelearnacademy.filelab.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFileRepository<T, ID> implements IRepository<T, ID> {

    @Override
    public List<T> findAll() {
        try {
            return readAll(); // Lee las entidades y las devuelve en una lista
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            List<T> entidades = readAll();

            for (T entity : entidades) {
                if (getId(entity).equals(id)) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean create(T entity) {
        try {
            List<T> entidades = readAll();

            for (T entidad : entidades) {
                if (getId(entidad).equals(getId(entity))) {
                    return false;
                }
            }
            entidades.add(entity);
            writeAll(entidades);

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean update(T entity) {
        try {
            List<T> entidades = readAll();

            for (int i = 0; i < entidades.size(); i++) {
                if (getId(entidades.get(i)).equals(getId(entity))) {
                    entidades.set(i, entity);
                    writeAll(entidades);
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean delete(ID id) {
        try {
            List<T> entidades = readAll();

            for (int i = 0; i < entidades.size(); i++) {
                if (getId(entidades.get(i)).equals(id)) {
                    entidades.remove(i);
                    writeAll(entidades);
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            return false;
        }
    }

    protected abstract ID getId(T entity);

    protected abstract List<T> readAll() throws IOException;

    protected abstract void writeAll(List<T> entities) throws IOException;
}
