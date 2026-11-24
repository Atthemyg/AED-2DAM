package ies.puesto.peliculas.controller;

import ies.puesto.peliculas.controller.mapper.PeliculaApiMapper;
import ies.puesto.peliculas.model.Pelicula;
import ies.puesto.peliculas.service.PeliculaService;
import ies.puesto.peliculas.service.modelo.DatosCreacionPelicula;
import ies.puesto.peliculas.service.modelo.DatosEdicionPelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PeliculaJdbcControllerTest {

    private static final String RUTA = "/api/v1/jdbc/peliculas";

    private PeliculaService service;
    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        service = mock(PeliculaService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PeliculaJdbcController(service, new PeliculaApiMapper()))
                .build();
    }

    private static Pelicula chihiro() {
        return new Pelicula("PEL001", "El viaje de Chihiro", 2001, 125);
    }

    private static Pelicula totoro() {
        return new Pelicula("PEL002", "Mi vecino Totoro", 1988, 86);
    }

    // ---------------------------------------------------------------- GET

    @Test
    void listarDevuelve200YLaLista() throws Exception {
        when(service.listar()).thenReturn(List.of(chihiro(), totoro()));

        mockMvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("PEL001"))
                .andExpect(jsonPath("$[0].titulo").value("El viaje de Chihiro"))
                .andExpect(jsonPath("$[0].anioEstreno").value(2001))
                .andExpect(jsonPath("$[0].duracionMinutos").value(125))
                .andExpect(jsonPath("$[1].codigo").value("PEL002"));
    }

    @Test
    void obtenerExistenteDevuelve200YLaPelicula() throws Exception {
        when(service.obtener("PEL001")).thenReturn(Optional.of(chihiro()));

        mockMvc.perform(get(RUTA + "/PEL001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PEL001"))
                .andExpect(jsonPath("$.titulo").value("El viaje de Chihiro"))
                .andExpect(jsonPath("$.anioEstreno").value(2001))
                .andExpect(jsonPath("$.duracionMinutos").value(125));
    }

    @Test
    void obtenerInexistenteDevuelve404ConProblemDetail() throws Exception {
        when(service.obtener("PEL099")).thenReturn(Optional.empty());

        mockMvc.perform(get(RUTA + "/PEL099"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe la película PEL099"));
    }

    @Test
    void obtenerConCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get(RUTA + "/PEL3"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // --------------------------------------------------------------- POST

    private static final String CUERPO_POST_VALIDO = """
            {"codigo":"PEL010","titulo":"Akira","anioEstreno":1988,"duracionMinutos":124}""";

    @Test
    void crearNuevaDevuelve201YLaPeliculaCreada() throws Exception {
        DatosCreacionPelicula datos = new DatosCreacionPelicula("PEL010", "Akira", 1988, 124);
        when(service.crear(datos)).thenReturn(Optional.of(new Pelicula("PEL010", "Akira", 1988, 124)));

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO_POST_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("PEL010"))
                .andExpect(jsonPath("$.titulo").value("Akira"))
                .andExpect(jsonPath("$.anioEstreno").value(1988))
                .andExpect(jsonPath("$.duracionMinutos").value(124));

        verify(service).crear(datos);
    }

    @Test
    void crearDuplicadaDevuelve409ConProblemDetail() throws Exception {
        when(service.crear(new DatosCreacionPelicula("PEL010", "Akira", 1988, 124)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(CUERPO_POST_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Ya existe la película PEL010"));
    }

    static Stream<String> cuerposInvalidosPost() {
        return Stream.of(
                "{\"titulo\":\"Akira\",\"anioEstreno\":1988,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL10\",\"titulo\":\"Akira\",\"anioEstreno\":1988,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"anioEstreno\":1988,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"   \",\"anioEstreno\":1988,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"" + "x".repeat(121) + "\",\"anioEstreno\":1988,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"anioEstreno\":1887,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"anioEstreno\":2101,\"duracionMinutos\":124}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"anioEstreno\":1988}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"anioEstreno\":1988,\"duracionMinutos\":0}",
                "{\"codigo\":\"PEL010\",\"titulo\":\"Akira\",\"anioEstreno\":1988,\"duracionMinutos\":1001}",
                "{\"codigo\":\"PEL010\",\"titulo\":"
        );
    }

    @ParameterizedTest
    @MethodSource("cuerposInvalidosPost")
    void crearConCuerpoInvalidoDevuelve400SinLlamarAlServicio(String cuerpo) throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------------------------------------------------------------- PUT

    private static final String CUERPO_PUT_VALIDO = """
            {"titulo":"Chihiro (remasterizada)","anioEstreno":2001,"duracionMinutos":126}""";

    @Test
    void actualizarExistenteDevuelve200YLaPeliculaActualizada() throws Exception {
        DatosEdicionPelicula datos = new DatosEdicionPelicula("Chihiro (remasterizada)", 2001, 126);
        when(service.actualizar("PEL001", datos))
                .thenReturn(Optional.of(new Pelicula("PEL001", "Chihiro (remasterizada)", 2001, 126)));

        mockMvc.perform(put(RUTA + "/PEL001").contentType(MediaType.APPLICATION_JSON).content(CUERPO_PUT_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PEL001"))
                .andExpect(jsonPath("$.titulo").value("Chihiro (remasterizada)"))
                .andExpect(jsonPath("$.anioEstreno").value(2001))
                .andExpect(jsonPath("$.duracionMinutos").value(126));

        verify(service).actualizar("PEL001", datos);
    }

    @Test
    void actualizarInexistenteDevuelve404ConProblemDetail() throws Exception {
        when(service.actualizar("PEL099", new DatosEdicionPelicula("Chihiro (remasterizada)", 2001, 126)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put(RUTA + "/PEL099").contentType(MediaType.APPLICATION_JSON).content(CUERPO_PUT_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe la película PEL099"));
    }

    @Test
    void actualizarConCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(put(RUTA + "/PEL3").contentType(MediaType.APPLICATION_JSON).content(CUERPO_PUT_VALIDO))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    static Stream<String> cuerposInvalidosPut() {
        return Stream.of(
                "{\"anioEstreno\":2001,\"duracionMinutos\":126}",
                "{\"titulo\":\"   \",\"anioEstreno\":2001,\"duracionMinutos\":126}",
                "{\"titulo\":\"" + "x".repeat(121) + "\",\"anioEstreno\":2001,\"duracionMinutos\":126}",
                "{\"titulo\":\"Chihiro\",\"duracionMinutos\":126}",
                "{\"titulo\":\"Chihiro\",\"anioEstreno\":1887,\"duracionMinutos\":126}",
                "{\"titulo\":\"Chihiro\",\"anioEstreno\":2101,\"duracionMinutos\":126}",
                "{\"titulo\":\"Chihiro\",\"anioEstreno\":2001}",
                "{\"titulo\":\"Chihiro\",\"anioEstreno\":2001,\"duracionMinutos\":0}",
                "{\"titulo\":\"Chihiro\",\"anioEstreno\":2001,\"duracionMinutos\":1001}",
                "{\"titulo\":"
        );
    }

    @ParameterizedTest
    @MethodSource("cuerposInvalidosPut")
    void actualizarConCuerpoInvalidoDevuelve400SinLlamarAlServicio(String cuerpo) throws Exception {
        mockMvc.perform(put(RUTA + "/PEL001").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ------------------------------------------------------------- DELETE

    @Test
    void eliminarExistenteDevuelve204SinCuerpo() throws Exception {
        when(service.eliminar("PEL001")).thenReturn(true);

        mockMvc.perform(delete(RUTA + "/PEL001"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void eliminarInexistenteDevuelve404ConProblemDetail() throws Exception {
        when(service.eliminar("PEL099")).thenReturn(false);

        mockMvc.perform(delete(RUTA + "/PEL099"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe la película PEL099"));
    }

    @Test
    void eliminarConCodigoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(delete(RUTA + "/PEL3"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
