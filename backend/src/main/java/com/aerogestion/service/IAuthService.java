package com.aerogestion.service;

import com.aerogestion.dto.RegistroForm;
import com.aerogestion.model.Pasajero;

public interface IAuthService {

    Pasajero registrar(RegistroForm form);
}
