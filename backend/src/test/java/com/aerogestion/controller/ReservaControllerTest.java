package com.aerogestion.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.aerogestion.DatosPrueba;
import com.aerogestion.config.SecurityConfig;
import com.aerogestion.exception.ConflictoException;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IReservaService;

/**
 * Capa de presentación de reservas: comprueba que cada resultado del caso de uso se traduce
 * al código HTTP correcto (201, 200, 400, 401, 403, 404, 409). El servicio se simula, así que
 * no hace falta MongoDB.
 */
@WebMvcTest(ReservaController.class)
@Import(SecurityConfig.class)
class ReservaControllerTest {

    private static final String JSON_RESERVA = """
            {"codigoVuelo": "AG100", "asientos": ["2A"]}
            """;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IReservaService reservaService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("POST /api/reservas con datos válidos → 201 Created + JSON de la reserva")
    void crearReservaValida() throws Exception {
        when(reservaService.crear("AG100", List.of("2A"), DatosPrueba.EMAIL_PASAJERO))
                .thenReturn(DatosPrueba.reserva());

        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(JSON_RESERVA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("AG-ABC234"))
                .andExpect(jsonPath("$.vuelo").value("AG100"))
                .andExpect(jsonPath("$.asientos[0].numero").value("2A"))
                .andExpect(jsonPath("$.asientos[0].clase").value("ECONOMICA"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("POST /api/reservas sin asientos → 400 Bad Request con el campo inválido")
    void crearReservaSinAsientos() throws Exception {
        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoVuelo\": \"AG100\", \"asientos\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.asientos").value("Selecciona al menos un asiento"));

        verify(reservaService, never()).crear(anyString(), anyList(), anyString());
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("POST /api/reservas con JSON mal formado → 400 Bad Request")
    void crearReservaJsonMalFormado() throws Exception {
        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content("{codigoVuelo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Petición inválida"));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("POST /api/reservas con un asiento inexistente → 400 Bad Request")
    void crearReservaAsientoInexistente() throws Exception {
        when(reservaService.crear(any(), any(), any()))
                .thenThrow(new ReglaNegocioException("El asiento 99Z no existe en este avión"));

        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(JSON_RESERVA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El asiento 99Z no existe en este avión"));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("POST /api/reservas con un asiento ya ocupado → 409 Conflict")
    void crearReservaAsientoOcupado() throws Exception {
        when(reservaService.crear(any(), any(), any()))
                .thenThrow(new ConflictoException("El asiento 2A ya está ocupado"));

        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(JSON_RESERVA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflicto"))
                .andExpect(jsonPath("$.mensaje").value("El asiento 2A ya está ocupado"));
    }

    @Test
    @DisplayName("POST /api/reservas sin iniciar sesión → 401 Unauthorized")
    void crearReservaSinAutenticacion() throws Exception {
        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(JSON_RESERVA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "admin@aerogestion.com", roles = "ADMIN")
    @DisplayName("POST /api/reservas con rol ADMIN → 403 Forbidden")
    void crearReservaConRolAdmin() throws Exception {
        mvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(JSON_RESERVA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("GET /api/reservas → 200 OK con la lista (puede estar vacía)")
    void listarReservas() throws Exception {
        when(reservaService.listarPorPasajero(DatosPrueba.EMAIL_PASAJERO))
                .thenReturn(List.of(DatosPrueba.reserva()));

        mvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo").value("AG-ABC234"));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("GET /api/reservas/{codigo} inexistente → 404 Not Found")
    void consultarReservaInexistente() throws Exception {
        when(reservaService.obtenerDelPasajero(eq("AG-NOEXISTE"), anyString()))
                .thenThrow(new RecursoNoEncontradoException("No existe la reserva AG-NOEXISTE"));

        mvc.perform(get("/api/reservas/AG-NOEXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("No existe la reserva AG-NOEXISTE"));
    }

    @Test
    @WithMockUser(username = DatosPrueba.EMAIL_PASAJERO, roles = "PASAJERO")
    @DisplayName("PATCH /api/reservas/{codigo}/cancelar a menos de 24 h → 409 Conflict")
    void cancelarFueraDePlazo() throws Exception {
        when(reservaService.cancelar(eq("AG-ABC234"), anyString()))
                .thenThrow(new ConflictoException("Solo puedes cancelar hasta 24 horas antes de la salida del vuelo"));

        mvc.perform(patch("/api/reservas/AG-ABC234/cancelar"))
                .andExpect(status().isConflict());
    }
}
