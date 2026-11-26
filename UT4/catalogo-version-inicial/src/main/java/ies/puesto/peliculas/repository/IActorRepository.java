package ies.puesto.peliculas.repository;

import ies.puesto.peliculas.model.Actor;
import ies.puesto.peliculas.model.DatosActor;

import java.util.List;

public interface IActorRepository {

    List<Actor> buscarTodos();
    Actor obtener(String idActor);
    void eliminar(String idActor);
    Actor editar(String idActor, DatosActor datos);
    Actor crear(String idActor, DatosActor datos);
}
