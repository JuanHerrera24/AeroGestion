package com.aerogestion.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import com.aerogestion.DatosPrueba;
import com.aerogestion.exception.ConflictoException;
import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.EstadoReserva;
import com.aerogestion.model.EstadoVuelo;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;
import com.aerogestion.notification.Notificador;
import com.aerogestion.repository.ReservaRepository;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IVueloService;
import com.mongodb.client.result.UpdateResult;

/**
 * Capa de negocio: las reglas de reserva se validan en el Service, no en el Controller.
 * Los repositorios y MongoTemplate se simulan con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private IVueloService vueloService;
    @Mock
    private Notificador notificador;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ReservaServiceImpl service;

    private Vuelo vuelo;

    @BeforeEach
    void setUp() {
        vuelo = DatosPrueba.vuelo();
    }

    private void pasajeroYVueloExisten() {
        when(usuarioRepository.findByEmail(DatosPrueba.EMAIL_PASAJERO)).thenReturn(Optional.of(DatosPrueba.pasajero()));
        when(vueloService.obtener("AG100")).thenReturn(vuelo);
    }

    private void mongoOcupaAsientos(long modificados) {
        when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class), eq(Vuelo.class)))
                .thenReturn(UpdateResult.acknowledged(1, modificados, null));
    }

    @Test
    @DisplayName("Reserva válida: calcula el total por clase, guarda la reserva y envía el correo")
    void crearReservaValida() {
        pasajeroYVueloExisten();
        mongoOcupaAsientos(1);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = service.crear("AG100", List.of("1a", "2A"), DatosPrueba.EMAIL_PASAJERO);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(reserva.getCodigoReserva()).matches("AG-[A-Z2-9]{6}");
        assertThat(reserva.getNumerosAsiento()).containsExactly("1A", "2A");
        // 1A ejecutivo (200 × 1.8 = 360) + 2A económico (200 × 1.0 = 200)
        assertThat(reserva.getTotal()).isEqualTo(560.0);
        verify(notificador).enviarConfirmacion(reserva);
    }

    @Test
    @DisplayName("Sin asientos → 400 (ReglaNegocioException) y no se consulta nada más")
    void sinAsientos() {
        assertThatThrownBy(() -> service.crear("AG100", List.of(), DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("Selecciona al menos un asiento");
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Más de 9 asientos → 400 (ReglaNegocioException)")
    void demasiadosAsientos() {
        List<String> diez = List.of("1A", "1B", "1C", "1D", "1E", "1F", "2A", "2B", "2C", "2D");
        assertThatThrownBy(() -> service.crear("AG100", diez, DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Asiento que no existe en el avión → 400 (ReglaNegocioException)")
    void asientoInexistente() {
        pasajeroYVueloExisten();
        assertThatThrownBy(() -> service.crear("AG100", List.of("30F"), DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("30F");
    }

    @Test
    @DisplayName("Asiento ya ocupado → 409 (ConflictoException) y no se guarda la reserva")
    void asientoOcupado() {
        vuelo.getAsientosOcupados().add("2A");
        pasajeroYVueloExisten();

        assertThatThrownBy(() -> service.crear("AG100", List.of("2A"), DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("El asiento 2A ya está ocupado");
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Vuelo cancelado → 409 (ConflictoException): no admite reservas")
    void vueloCancelado() {
        vuelo.setEstado(EstadoVuelo.CANCELADO);
        pasajeroYVueloExisten();

        assertThatThrownBy(() -> service.crear("AG100", List.of("2A"), DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("no admite reservas");
    }

    @Test
    @DisplayName("Otro pasajero tomó el asiento un instante antes → 409 (ConflictoException)")
    void asientoTomadoAlMismoTiempo() {
        pasajeroYVueloExisten();
        mongoOcupaAsientos(0);

        assertThatThrownBy(() -> service.crear("AG100", List.of("2A"), DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ConflictoException.class);
        verify(reservaRepository, never()).save(any());
        verify(notificador, never()).enviarConfirmacion(any());
    }

    @Test
    @DisplayName("Reserva de otro pasajero → 404: no se revela que existe")
    void reservaDeOtroPasajero() {
        when(reservaRepository.findByCodigoReserva("AG-ABC234")).thenReturn(Optional.of(DatosPrueba.reserva()));

        assertThatThrownBy(() -> service.obtenerDelPasajero("ag-abc234", "otro@correo.com"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Cancelar a menos de 24 h de la salida → 409 (ConflictoException)")
    void cancelarFueraDePlazo() {
        Reserva reserva = DatosPrueba.reserva();
        reserva.getVuelo().setFechaHoraSalida(java.time.LocalDateTime.now().plusHours(5));
        when(reservaRepository.findByCodigoReserva("AG-ABC234")).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> service.cancelar("AG-ABC234", DatosPrueba.EMAIL_PASAJERO))
                .isInstanceOf(ConflictoException.class);
        verify(notificador, never()).enviarCancelacion(any());
    }

    @Test
    @DisplayName("Cancelar a tiempo → libera los asientos del vuelo y envía el correo")
    void cancelarATiempo() {
        Reserva reserva = DatosPrueba.reserva();
        when(reservaRepository.findByCodigoReserva("AG-ABC234")).thenReturn(Optional.of(reserva));

        Reserva cancelada = service.cancelar("AG-ABC234", DatosPrueba.EMAIL_PASAJERO);

        assertThat(cancelada.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(mongoTemplate).updateFirst(any(Query.class), any(UpdateDefinition.class), eq(Vuelo.class));
        verify(notificador).enviarCancelacion(reserva);
    }
}
