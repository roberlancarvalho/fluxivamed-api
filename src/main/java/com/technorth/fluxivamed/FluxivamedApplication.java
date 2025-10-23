package com.technorth.fluxivamed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EntityScan(basePackages = {"com.technorth.fluxivamed.domain", "com.technorth.fluxivamed.core"})
@EnableJpaRepositories(basePackages = {"com.technorth.fluxivamed.repository", "com.technorth.fluxivamed.core"})
public class FluxivamedApplication {

    public static void main(String[] args) {
        SpringApplication.run(FluxivamedApplication.class, args);
    }

}