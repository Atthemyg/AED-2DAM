package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IRepository {

    /**
     * Funcion que obtiene todos los elementos
     * @return List de productos
     */
    List<Producto> findAll();

    /**
     * Funcion que obtiene el elemento
     * @param id Identificador del producto
     * @return Optional del producto
     */
    Optional<Producto> findById(long id);

    /**
     * Crea el producto
     * @param producto
     */
    void create(Producto producto);

    /**
     * Actualiza un producto
     * @param producto
     * @return
     */
    boolean update(Producto producto);


    boolean delete(long id);
}
