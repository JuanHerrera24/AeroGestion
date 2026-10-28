package com.aerogestion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/** Activa @CreatedDate / @LastModifiedDate en EntidadBase. */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
