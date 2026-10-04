package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final IProductoRepository repository;

    public ProductoService(IProductoRepository repository) {
        this.repository = repository;
    }

    public Optional<Producto> maximoPrecio() {
        List<Producto> productos = repository.findAll();

        if (productos.isEmpty()) {
            return Optional.empty();
        }

        Producto maximo = productos.get(0); // Suponemos que el primero es el que tiene el precio más alto

        for (Producto producto : productos) {
            if (producto.precio() > maximo.precio()) {
                maximo = producto;
            }
        }
        return Optional.of(maximo);
    }

    public Optional<Producto> minimoPrecio() {
        List<Producto> productos = repository.findAll();

        if (productos.isEmpty()) {
            return Optional.empty();
        }

        Producto minimo = productos.get(0);

        for (Producto producto : productos) {
            if (producto.precio() < minimo.precio()) {
                minimo = producto;
            }
        }
        return Optional.of(minimo);
    }

    public Optional<Producto> maximoStock() {
        List<Producto> productos = repository.findAll();

        if (productos.isEmpty()) {
            return Optional.empty();
        }

        Producto maximo = productos.get(0);

        for (Producto producto : productos) {
            if (producto.stock() > maximo.stock()) {
                maximo = producto;
            }
        }
        return Optional.of(maximo);
    }

    public Optional<Producto> minimoStock() {
        List<Producto> productos = repository.findAll();

        if (productos.isEmpty()) {
            return Optional.empty();
        }

        Producto minimo = productos.get(0);

        for (Producto producto : productos) {
            if (producto.stock() < minimo.stock()) {
                minimo = producto;
            }
        }
        return Optional.of(minimo);
    }

    public int stockTotal() {
        List<Producto> productos = repository.findAll();

        int total = 0;

        for (Producto producto : productos) {
            total = total + producto.stock();
        }
        return total;
    }

    public double valorInventario() {
        List<Producto> productos = repository.findAll();

        double total = 0;

        for (Producto producto : productos) {
            total = total + producto.precio() * producto.stock();
        }
        return total;
    }

    public List<Producto> sinStock() {
        List<Producto> productos = repository.findAll();

        List<Producto> sinStock = new ArrayList<>();

        for (Producto producto : productos) {
            if (producto.stock() == 0) {
                sinStock.add(producto);
            }
        }
        return sinStock;
    }

    public List<Producto> buscar(String texto) {
        List<Producto> productos = repository.findAll();

        List<Producto> resultados = new ArrayList<>();

        for (Producto producto : productos) {
            if (producto.nombre().toLowerCase().contains(texto.toLowerCase())) {
                resultados.add(producto);
            }
        }
        return resultados;
    }
}
