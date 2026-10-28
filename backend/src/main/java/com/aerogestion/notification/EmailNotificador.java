package com.aerogestion.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.aerogestion.model.Reserva;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificador implements Notificador {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Override
    public void enviarConfirmacion(Reserva reserva) {
        enviar(reserva, "Reserva confirmada " + reserva.getCodigoReserva(),
                "Tu reserva para el vuelo " + reserva.getVuelo().getCodigoVuelo()
                        + " fue confirmada. Total: " + reserva.getTotal());
    }

    @Override
    public void enviarCancelacion(Reserva reserva) {
        enviar(reserva, "Reserva cancelada " + reserva.getCodigoReserva(),
                "Tu reserva para el vuelo " + reserva.getVuelo().getCodigoVuelo()
                        + " fue cancelada.");
    }

    /** Si no hay cuenta SMTP configurada, el correo se registra en el log en lugar de enviarse. */
    private void enviar(Reserva reserva, String asunto, String cuerpo) {
        String destino = reserva.getPasajero().getEmail();
        if (remitente == null || remitente.isBlank()) {
            log.info("[Correo simulado] Para: {} | {} | {}", destino, asunto, cuerpo);
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destino);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
        } catch (MailException e) {
            log.warn("No se pudo enviar el correo a {}: {}", destino, e.getMessage());
        }
    }
}
