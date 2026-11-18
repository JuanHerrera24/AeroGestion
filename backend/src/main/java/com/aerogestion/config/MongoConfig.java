package com.aerogestion.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.aerogestion.model.Administrador;
import com.aerogestion.model.AsientoEconomico;
import com.aerogestion.model.AsientoEjecutivo;
import com.aerogestion.model.AsientoPrimeraClase;
import com.aerogestion.model.Pasajero;

/**
 * Configuración de la persistencia en MongoDB.
 *
 * <p>Activa la auditoría ({@code @CreatedDate} / {@code @LastModifiedDate} en {@code EntidadBase})
 * y define el <b>campo discriminador {@code tipo}</b>: cada documento guarda qué subclase representa
 * ({@code "PASAJERO"}, {@code "ADMINISTRADOR"}, {@code "ECONOMICO"}, …, ver {@code @TypeAlias}).
 * Así la colección {@code usuarios} guarda pasajeros y administradores juntos, y al leerla
 * Spring Data sabe qué objeto crear para cada documento.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    public static final String CAMPO_TIPO = "tipo";

    /**
     * Subclases que se guardan dentro de otros documentos o en la colección de su superclase.
     * Deben registrarse al arrancar para que el alias del campo {@code tipo} (ej. "ECONOMICO")
     * se pueda traducir a su clase al leer, aunque la aplicación aún no haya guardado ninguna.
     */
    public static final List<Class<?>> SUBTIPOS = List.of(
            Pasajero.class, Administrador.class,
            AsientoEconomico.class, AsientoEjecutivo.class, AsientoPrimeraClase.class);

    /** Clases que conoce el mapeo desde el arranque: los {@code @Document} del paquete más los subtipos. */
    @Bean
    public MongoManagedTypes mongoManagedTypes(ApplicationContext context) throws ClassNotFoundException {
        Set<Class<?>> tipos = new LinkedHashSet<>(new EntityScanner(context).scan(Document.class));
        tipos.addAll(SUBTIPOS);
        return MongoManagedTypes.fromIterable(tipos);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory,
            MongoMappingContext context, MongoCustomConversions conversions) {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        converter.setCustomConversions(conversions);
        converter.setCodecRegistryProvider(factory);
        converter.setTypeMapper(new DefaultMongoTypeMapper(CAMPO_TIPO, context));
        return converter;
    }
}
