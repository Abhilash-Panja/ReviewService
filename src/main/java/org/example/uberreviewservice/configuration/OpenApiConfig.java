package org.example.uberreviewservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "RideFlow Review and CRUD API",
        version = "0.0.1-SNAPSHOT",
        description = "Passenger and driver CRUD, a local booking state machine and passenger reviews. This workflow uses direct JPA access, not distributed driver dispatch. Consumers pin a historical shared model; verify that artifact and schema before running."
))
public class OpenApiConfig {
}
