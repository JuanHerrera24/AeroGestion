package com.aerogestion.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.aerogestion.DatosPrueba;
import com.aerogestion.model.Administrador;
import com.aerogestion.model.Avion;
import com.aerogestion.model.Pasajero;
import com.aerogestion.model.Usuario;

/**
 * Persistencia: Pasajero y Administrador se guardan en la misma colección "usuarios" y los
 * asientos van embebidos en el avión. El campo discriminador "tipo" indica la subclase de cada
 * documento para que, al leerlo, Spring Data cree el objeto correcto.
 */
class DiscriminadorTipoTest {

    private MappingMongoConverter converter;

    @BeforeEach
    void setUp() {
        MongoCustomConversions conversions = new MongoCustomConversions(List.of());
        MongoMappingContext context = new MongoMappingContext();
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        context.setInitialEntitySet(new HashSet<>(MongoConfig.SUBTIPOS));
        context.afterPropertiesSet();
        converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(MongoConfig.CAMPO_TIPO, context));
        converter.afterPropertiesSet();
    }

    private Document escribir(Object entidad) {
        Document documento = new Document();
        converter.write(entidad, documento);
        return documento;
    }

    @Test
    @DisplayName("Un Pasajero se guarda con tipo = PASAJERO (y sin _class)")
    void pasajeroLlevaTipo() {
        Document doc = escribir(DatosPrueba.pasajero());

        assertThat(doc.getString("tipo")).isEqualTo("PASAJERO");
        assertThat(doc).doesNotContainKey("_class");
    }

    @Test
    @DisplayName("Al leer la colección usuarios, el tipo decide si se crea Pasajero o Administrador")
    void leerSubtipoSegunTipo() {
        Administrador admin = new Administrador();
        admin.setEmail("admin@aerogestion.com");

        Usuario leidoPasajero = converter.read(Usuario.class, escribir(DatosPrueba.pasajero()));
        Usuario leidoAdmin = converter.read(Usuario.class, escribir(admin));

        assertThat(leidoPasajero).isInstanceOf(Pasajero.class);
        assertThat(leidoAdmin).isInstanceOf(Administrador.class);
        assertThat(leidoAdmin.puedeGestionarVuelos()).isTrue();
    }

    @Test
    @DisplayName("Un avión leído de MongoDB (sin haberlo guardado antes) recupera cada clase de asiento por su tipo")
    void leerAsientosDesdeDocumento() {
        Document doc = new Document("matricula", "OB-9999")
                .append("asientos", List.of(
                        new Document("numero", "1A").append("fila", 1).append("letra", "A").append("tipo", "PRIMERA_CLASE"),
                        new Document("numero", "5A").append("fila", 5).append("letra", "A").append("tipo", "EJECUTIVO"),
                        new Document("numero", "9A").append("fila", 9).append("letra", "A").append("tipo", "ECONOMICO")));

        Avion avion = converter.read(Avion.class, doc);

        assertThat(avion.getAsientos()).extracting(a -> a.getClase())
                .containsExactly("PRIMERA", "EJECUTIVA", "ECONOMICA");
    }

    @Test
    @DisplayName("Los asientos embebidos en el avión guardan su clase en tipo")
    void asientosEmbebidosLlevanTipo() {
        Avion avion = DatosPrueba.avion();

        @SuppressWarnings("unchecked")
        List<Document> asientos = (List<Document>) escribir(avion).get("asientos");

        assertThat(asientos).extracting(a -> a.getString("tipo"))
                .containsExactly("EJECUTIVO", "EJECUTIVO", "ECONOMICO", "ECONOMICO");
    }
}
