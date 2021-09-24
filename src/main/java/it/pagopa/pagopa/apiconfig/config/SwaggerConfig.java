package it.pagopa.pagopa.apiconfig.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDescription, @Value("${application-version}") String appVersion) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("ApiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .description("The API key to access this function app.")
                                .name("X-Functions-Key")
                                .in(SecurityScheme.In.HEADER)))
                .info(new Info()
                        .title("PagoPA API configuration")
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("https://www.pagopa.gov.it/"));

    }
}
