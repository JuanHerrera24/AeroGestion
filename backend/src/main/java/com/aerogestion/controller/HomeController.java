package com.aerogestion.controller;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aerogestion.dto.RegistroForm;
import com.aerogestion.exception.ReglaNegocioException;
import com.aerogestion.service.IAuthService;
import com.aerogestion.service.IBusquedaService;
import com.aerogestion.service.IVueloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IBusquedaService busquedaService;
    private final IVueloService vueloService;
    private final IAuthService authService;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("aeropuertos", vueloService.listarAeropuertos());
        model.addAttribute("proximos", busquedaService.proximosVuelos(6));
        model.addAttribute("hoy", LocalDate.now().plusDays(1));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("form", new RegistroForm());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("form") RegistroForm form, BindingResult result,
            Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "auth/registro";
        }
        try {
            authService.registrar(form);
        } catch (ReglaNegocioException e) {
            result.rejectValue("email", "duplicado", e.getMessage());
            return "auth/registro";
        }
        flash.addFlashAttribute("exito", "¡Cuenta creada! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    /** Después del login, cada rol va a su panel. */
    @GetMapping("/panel")
    public String panel(Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return esAdmin ? "redirect:/admin" : "redirect:/";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("titulo", "Acceso denegado");
        model.addAttribute("mensaje", "No tienes permisos para ver esta página.");
        return "error";
    }
}
