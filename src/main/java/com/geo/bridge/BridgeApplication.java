package com.geo.bridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
public class BridgeApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BridgeApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
