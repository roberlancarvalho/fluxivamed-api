package com.technorth.fluxivamed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class FluxivamedApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxivamedApplication.class, args);
	}

}
