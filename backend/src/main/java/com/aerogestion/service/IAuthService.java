package com.aerogestion.service;

import com.aerogestion.dto.RegistroForm;
import com.aerogestion.model.Pasajero;
import com.aerogestion.model.Usuario;

public interface IAuthService {

    Pasajero registrar(RegistroForm form);

    Usuario obtenerPorEmail(String email);
}
