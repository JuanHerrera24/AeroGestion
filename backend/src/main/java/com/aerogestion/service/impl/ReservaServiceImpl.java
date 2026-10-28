package com.aerogestion.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.aerogestion.exception.RecursoNoEncontradoException;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.model.Asiento;
import com.aerogestion.model.EstadoReserva;
import com.aerogestion.model.Pasajero;
import com.aerogestion.model.Reserva;
import com.aerogestion.model.Vuelo;
import com.aerogestion.notification.Notificador;
import com.aerogestion.repository.ReservaRepository;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IReservaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService {

    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int MAX_ASIENTOS_POR_RESERVA = 9;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IVueloService vueloService;
    private final Notificador notificador;
    private final MongoTemplate mongoTemplate;

    @Override
    public Reserva crear(String codigoVuelo, List<String> numerosAsiento, String emailPasajero) {
        if (numerosAsiento == null || numerosAsiento.isEmpty()) {
            throw new ReglaNegocioException("Selecciona al menos un asiento");
        }
        List<String> numeros = new ArrayList<>(new LinkedHashSet<>(
                numerosAsiento.stream().map(n -> n.trim().toUpperCase()).toList()));
        if (numeros.size() > MAX_ASIENTOS_POR_RESERVA) {
            throw new ReglaNegocioException("Máximo " + MAX_ASIENTOS_POR_RESERVA + " asientos por reserva");
        }
        Pasajero pasajero = obtenerPasajero(emailPasajero);
        Vuelo vuelo = vueloService.obtener(codigoVuelo);
        if (!vuelo.estaAbiertoParaReservas()) {
            throw new ReglaNegocioException("El vuelo " + vuelo.getCodigoVuelo() + " no admite reservas");
        }
        List<Asiento> asientos = new ArrayList<>();
        for (String numero : numeros) {
            Asiento asiento = vuelo.getAvion().buscarAsiento(numero);
            if (asiento == null) {
                throw new ReglaNegocioException("El asiento " + numero + " no existe en este avión");
            }
            if (vuelo.getAsientosOcupados().contains(numero)) {
                throw new ReglaNegocioException("El asiento " + numero + " ya está ocupado");
            }
            asientos.add(asiento);
        }

        ocuparAsientosAtomicamente(vuelo, numeros);

        Reserva reserva = new Reserva();
        reserva.setCodigoReserva(generarCodigoUnico());
        reserva.setPasajero(pasajero);
        reserva.setVuelo(vuelo);
        reserva.setAsientos(asientos);
        reserva.confirmar();
        reserva = reservaRepository.save(reserva);
        notificador.enviarConfirmacion(reserva);
        return reserva;
    }

    /**
     * Ocupa los asientos en una sola operación de MongoDB: solo se aplica si ninguno está
     * ocupado. Así, si dos pasajeros eligen el mismo asiento a la vez, solo el primero lo obtiene.
     */
    private void ocuparAsientosAtomicamente(Vuelo vuelo, List<String> numeros) {
        Query query = new Query(Criteria.where("_id").is(vuelo.getId())
                .and("asientosOcupados").nin(numeros));
        Update update = new Update().addToSet("asientosOcupados").each(numeros.toArray());
        if (mongoTemplate.updateFirst(query, update, Vuelo.class).getModifiedCount() == 0) {
            throw new ReglaNegocioException("Alguno de los asientos elegidos acaba de ser reservado. Elige otro.");
        }
    }

    @Override
    public List<Reserva> listarPorPasajero(String emailPasajero) {
        return reservaRepository.findByPasajeroOrderByFechaReservaDesc(obtenerPasajero(emailPasajero));
    }

    @Override
    public Reserva obtenerDelPasajero(String codigoReserva, String emailPasajero) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + codigoReserva));
        if (!reserva.getPasajero().getEmail().equals(emailPasajero)) {
            throw new RecursoNoEncontradoException("No existe la reserva " + codigoReserva);
        }
        return reserva;
    }

    @Override
    public Reserva cancelar(String codigoReserva, String emailPasajero) {
        Reserva reserva = obtenerDelPasajero(codigoReserva, emailPasajero);
        if (!reserva.puedeCancelarse()) {
            throw new ReglaNegocioException("Solo puedes cancelar hasta 24 horas antes de la salida del vuelo");
        }
        reserva.cancelar();
        reservaRepository.save(reserva);
        Query query = new Query(Criteria.where("_id").is(reserva.getVuelo().getId()));
        Update update = new Update().pullAll("asientosOcupados", reserva.getNumerosAsiento().toArray());
        mongoTemplate.updateFirst(query, update, Vuelo.class);
        notificador.enviarCancelacion(reserva);
        return reserva;
    }

    @Override
    public long contarConfirmadas() {
        return reservaRepository.countByEstado(EstadoReserva.CONFIRMADA);
    }

    private Pasajero obtenerPasajero(String email) {
        return usuarioRepository.findByEmail(email)
                .filter(Pasajero.class::isInstance)
                .map(Pasajero.class::cast)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pasajero no encontrado"));
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("AG-");
            for (int i = 0; i < 6; i++) {
                sb.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
            }
            codigo = sb.toString();
        } while (reservaRepository.existsByCodigoReserva(codigo));
        return codigo;
    }
}
