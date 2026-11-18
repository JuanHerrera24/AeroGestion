package com.aerogestion.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.aerogestion.DatosPrueba;
import com.aerogestion.config.SecurityConfig;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IBusquedaService;
import com.aerogestion.service.IVueloService;

/** Consulta pública de vuelos: 200 con la colección, 404 si no existe, 405 y 500 con JSON. */
@WebMvcTest(VueloController.class)
@Import(SecurityConfig.class)
class VueloControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IBusquedaService busquedaService;

    @MockBean
    private IVueloService vueloService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/vuelos/proximos sin iniciar sesión → 200 OK con la lista en JSON")
    void listarProximos() throws Exception {
        when(busquedaService.proximosVuelos(anyInt())).thenReturn(List.of(DatosPrueba.vuelo()));

        mvc.perform(get("/api/vuelos/proximos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo").value("AG100"))
                .andExpect(jsonPath("$[0].origen").value("LIM"))
                .andExpect(jsonPath("$[0].destino").value("CUZ"))
                .andExpect(jsonPath("$[0].asientosDisponibles").value(4));
    }

    @Test
    @DisplayName("GET /api/vuelos/proximos sin vuelos → 200 OK con colección vacía, no 404")
    void listarSinResultados() throws Exception {
        when(busquedaService.proximosVuelos(anyInt())).thenReturn(List.of());

        mvc.perform(get("/api/vuelos/proximos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/vuelos/{codigo} inexistente → 404 Not Found")
    void consultarVueloInexistente() throws Exception {
        when(vueloService.obtener("XX999")).thenThrow(new RecursoNoEncontradoException("No existe el vuelo XX999"));

        mvc.perform(get("/api/vuelos/XX999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("No existe el vuelo XX999"));
    }

    @Test
    @DisplayName("GET /api/vuelos/buscar con fecha mal escrita → 400 Bad Request")
    void buscarConFechaInvalida() throws Exception {
        mvc.perform(get("/api/vuelos/buscar").param("origen", "LIM").param("destino", "CUZ").param("fecha", "mañana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/vuelos/{codigo} → 405 Method Not Allowed (los vuelos se cancelan, no se borran)")
    void eliminarVueloNoPermitido() throws Exception {
        mvc.perform(delete("/api/vuelos/AG100"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    @DisplayName("Error inesperado → 500 con JSON genérico, sin exponer detalles técnicos")
    void errorInesperado() throws Exception {
        when(vueloService.obtener("AG100")).thenThrow(new NullPointerException("detalle interno"));

        mvc.perform(get("/api/vuelos/AG100"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error inesperado. Intenta de nuevo más tarde."));
    }
}
