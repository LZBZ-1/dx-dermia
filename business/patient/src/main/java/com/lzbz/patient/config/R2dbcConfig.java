package com.lzbz.patient.config;

import io.micrometer.common.lang.NonNullApi;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;

import java.io.File;
import java.net.MalformedURLException;

@NonNullApi
@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        try {
            return new PostgresqlConnectionFactory(
                    PostgresqlConnectionConfiguration.builder()
                            .host("34.44.97.230")
                            .port(5432)
                            .database("patient_service")
                            .username("postgres")
                            .password("IS3-2024-2")
                            .sslMode(SSLMode.REQUIRE)
                            .sslRootCert(new File("D:\\dx-dermia-archives\\server-ca.pem").toURI().toURL())
                            .build()
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}