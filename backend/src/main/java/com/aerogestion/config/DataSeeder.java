package com.aerogestion.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aerogestion.dto.AvionForm;
import com.aerogestion.dto.RegistroForm;
import com.aerogestion.dto.VueloForm;
import com.aerogestion.model.Administrador;
import com.aerogestion.model.Aeropuerto;
import com.aerogestion.model.Avion;
import com.aerogestion.model.EstadoAvion;
import com.aerogestion.model.Vuelo;
import com.aerogestion.repository.AeropuertoRepository;
import com.aerogestion.repository.UsuarioRepository;
import com.aerogestion.service.IAuthService;
import com.aerogestion.service.IAvionService;
import com.aerogestion.service.IReservaService;
import com.aerogestion.service.IVueloService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Carga datos de demostración la primera vez que se ejecuta (base de datos vacía):
 * usuarios de prueba, aeropuertos, flota y vuelos para los próximos días.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    public static final String ADMIN_EMAIL = "admin@aerogestion.com";
    public static final String ADMIN_PASSWORD = "Admin2026!";
    public static final String PASAJERO_EMAIL = "pasajero@aerogestion.com";
    public static final String PASAJERO_PASSWORD = "Pasajero2026!";

    private static final int DIAS_DE_VUELOS = 10;

    private final UsuarioRepository usuarioRepository;
    private final AeropuertoRepository aeropuertoRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAuthService authService;
    private final IAvionService avionService;
    private final IVueloService vueloService;
    private final IReservaService reservaService;

    /** Ruta diaria: avión asignado, hora de salida, duración en minutos y tarifa base. */
    private record Ruta(String origen, String destino, int avion, LocalTime salida, int minutos, double tarifa) {
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        log.info("Base de datos vacía: cargando datos de demostración...");
        crearUsuarios();
        crearAeropuertos();
        List<Avion> flota = crearFlota();
        List<Vuelo> vuelos = crearVuelos(flota);
        reservaService.crear(vuelos.get(DIAS_DE_VUELOS * 2 + 2).getCodigoVuelo(), List.of("8A", "8B"), PASAJERO_EMAIL);
        log.info("Datos de demostración cargados: {} vuelos.", vuelos.size());
    }

    private void crearUsuarios() {
        Administrador admin = new Administrador();
        admin.setNombre("Ana");
        admin.setApellido("Torres");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setCodigoEmpleado("EMP-001");
        admin.setCargo("Jefa de Operaciones");
        usuarioRepository.save(admin);

        RegistroForm pasajero = new RegistroForm();
        pasajero.setNombre("Carlos");
        pasajero.setApellido("Ramírez");
        pasajero.setEmail(PASAJERO_EMAIL);
        pasajero.setPassword(PASAJERO_PASSWORD);
        pasajero.setDocumentoIdentidad("70123456");
        pasajero.setTelefono("987654321");
        pasajero.setFechaNacimiento(LocalDate.of(1998, 5, 14));
        pasajero.setNacionalidad("Peruana");
        authService.registrar(pasajero);
    }

    private void crearAeropuertos() {
        String[][] datos = {
            {"LIM", "Aeropuerto Internacional Jorge Chávez", "Lima", "Perú"},
            {"CUZ", "Aeropuerto Internacional Alejandro Velasco Astete", "Cusco", "Perú"},
            {"AQP", "Aeropuerto Internacional Rodríguez Ballón", "Arequipa", "Perú"},
            {"BOG", "Aeropuerto Internacional El Dorado", "Bogotá", "Colombia"},
            {"SCL", "Aeropuerto Internacional Arturo Merino Benítez", "Santiago", "Chile"},
            {"MEX", "Aeropuerto Internacional Benito Juárez", "Ciudad de México", "México"},
        };
        for (String[] d : datos) {
            Aeropuerto a = new Aeropuerto();
            a.setCodigoIata(d[0]);
            a.setNombre(d[1]);
            a.setCiudad(d[2]);
            a.setPais(d[3]);
            aeropuertoRepository.save(a);
        }
    }

    private List<Avion> crearFlota() {
        List<Avion> flota = new ArrayList<>();
        flota.add(avionService.crear(avion("OB-2101", "A320neo", "Airbus", 2021, 2, 4, 20)));
        flota.add(avionService.crear(avion("OB-1987", "737-800", "Boeing", 2018, 2, 3, 22)));
        flota.add(avionService.crear(avion("OB-2245", "A321neo", "Airbus", 2023, 3, 4, 24)));
        flota.add(avionService.crear(avion("OB-1790", "787-8 Dreamliner", "Boeing", 2019, 4, 6, 30)));
        Avion enMantenimiento = avionService.crear(avion("OB-1655", "A319", "Airbus", 2015, 0, 3, 18));
        avionService.cambiarEstado(enMantenimiento.getMatricula(), EstadoAvion.EN_MANTENIMIENTO);
        return flota;
    }

    private AvionForm avion(String matricula, String modelo, String fabricante, int anio,
            int primera, int ejecutiva, int economica) {
        AvionForm f = new AvionForm();
        f.setMatricula(matricula);
        f.setModelo(modelo);
        f.setFabricante(fabricante);
        f.setAnioFabricacion(anio);
        f.setFilasPrimera(primera);
        f.setFilasEjecutiva(ejecutiva);
        f.setFilasEconomica(economica);
        return f;
    }

    private List<Vuelo> crearVuelos(List<Avion> flota) {
        List<Ruta> rutas = List.of(
                new Ruta("LIM", "CUZ", 0, LocalTime.of(6, 0), 80, 89),
                new Ruta("CUZ", "LIM", 0, LocalTime.of(9, 0), 80, 89),
                new Ruta("LIM", "AQP", 0, LocalTime.of(13, 0), 90, 79),
                new Ruta("AQP", "LIM", 0, LocalTime.of(17, 0), 90, 79),
                new Ruta("LIM", "BOG", 1, LocalTime.of(8, 0), 210, 320),
                new Ruta("BOG", "LIM", 1, LocalTime.of(14, 0), 210, 320),
                new Ruta("LIM", "SCL", 2, LocalTime.of(7, 0), 225, 280),
                new Ruta("SCL", "LIM", 2, LocalTime.of(13, 0), 225, 280),
                new Ruta("LIM", "MEX", 3, LocalTime.of(1, 0), 360, 450),
                new Ruta("MEX", "LIM", 3, LocalTime.of(10, 0), 360, 450));

        List<Vuelo> vuelos = new ArrayList<>();
        for (int r = 0; r < rutas.size(); r++) {
            Ruta ruta = rutas.get(r);
            for (int dia = 1; dia <= DIAS_DE_VUELOS; dia++) {
                VueloForm f = new VueloForm();
                f.setCodigoVuelo(String.format("AG%d-%02d", 101 + r, dia));
                f.setOrigen(ruta.origen());
                f.setDestino(ruta.destino());
                f.setFechaHoraSalida(LocalDate.now().plusDays(dia).atTime(ruta.salida()));
                f.setFechaHoraLlegada(f.getFechaHoraSalida().plusMinutes(ruta.minutos()));
                f.setMatriculaAvion(flota.get(ruta.avion()).getMatricula());
                f.setTarifaBase(ruta.tarifa());
                vuelos.add(vueloService.programar(f));
            }
        }
        return vuelos;
    }
}
