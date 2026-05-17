package com.aibert.dosw.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI gamificationOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("AIBERT Gamification Service API")
                .version("1.0.0")
                .description(
                    """
                    Academic gamification microservice for the AIBERT platform.

                    This API exposes three capabilities aligned with integration requirements:
                    - **AIB-36 Points System**: awards XP, updates total points and productivity streaks.
                    - **AIB-37 Achievement System**: unlocks badges and maintains the achievement gallery.
                    - **AIB-38 Subject Progress**: calculates per-subject progress, levels and visual indicators.

                    All endpoints require JWT Bearer authentication and are designed for consumption by other
                    AIBERT microservices (tasks, academics, identity).
                    """)
                .contact(new Contact().name("Grootyology Team").email("support@aibert.edu")))
        .addTagsItem(new Tag().name("AIB-36 Points").description("Points and XP management"))
        .addTagsItem(new Tag().name("AIB-37 Achievements").description("Achievements and badges"))
        .addTagsItem(
            new Tag().name("AIB-38 Subject Progress").description("Per-subject academic progress"))
        .addSecurityItem(new SecurityRequirement().addList("Bearer"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Bearer",
                    new SecurityScheme()
                        .name("Bearer")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token issued by the AIBERT identity service")));
  }
}
