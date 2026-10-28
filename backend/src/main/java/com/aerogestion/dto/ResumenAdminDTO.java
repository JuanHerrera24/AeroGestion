package com.aerogestion.dto;

import java.util.List;

/** Indicadores del panel del administrador. */
public record ResumenAdminDTO(
        int totalAviones,
        int avionesActivos,
        int vuelosProximos7Dias,
        long reservasConfirmadas,
        double ocupacionMedia,
        List<VueloDTO> proximasSalidas) {
}
