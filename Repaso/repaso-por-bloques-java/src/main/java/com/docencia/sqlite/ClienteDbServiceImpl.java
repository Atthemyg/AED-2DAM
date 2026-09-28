package com.docencia.sqlite;

import java.util.ArrayList;
import java.util.List;

public class ClienteDbServiceImpl implements ClienteDbService {
    private final ClienteRepository repository;

    public ClienteDbServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Boolean create(Cliente cliente) {
        if (cliente == null) {
            return false;
        }
        if (repository.findByDni(cliente.getDni()) != null){
            return false;
        }
        return repository.save(cliente);
    }

    @Override
    public Cliente findByDni(String dni) {
        if (dni == null) {
            return null;
        }
        return repository.findByDni(dni);
    }

    @Override
    public List<Cliente> findAll() {
        return repository.findAll();
    }

    @Override
    public Boolean update(Cliente cliente) {
        if (cliente == null) {
            return false;
        }
        return repository.update(cliente);
    }

    @Override
    public Boolean deleteByDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }
        return repository.deleteByDni(dni);
    }

    @Override
    public List<Cliente> findByCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            return new ArrayList<>();
        }
        return repository.findByCiudad(ciudad);
    }

    
}
