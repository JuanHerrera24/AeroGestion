package com.aerogestion.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

@Configuration
public class WebConfig {

    /** Fechas en español en todas las vistas (ej. "martes 28 octubre"). */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.of("es", "PE"));
    }
}
