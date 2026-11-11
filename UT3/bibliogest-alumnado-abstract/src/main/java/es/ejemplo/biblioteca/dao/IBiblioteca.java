package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Autor;

public interface IBiblioteca<T,ID> {
    /**
     * Realiza la insercion de T en la base de datos
     * @param t T clase asociado
     * @return int 0/1 si realiza correctamente la insercion
     */
    public int insertar(T t);
}
