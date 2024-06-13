package org.aionys.main.commons.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;


@Configuration
@OpenAPIDefinition(info = @Info(title = "Notes service", version = "v1"))
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
class OpenAPIConfig {

    private static void removeNotFoundFromGetAll(OpenAPI openApi) {
        openApi.getPaths().values().forEach(pathItem -> {
                    if (pathItem.getGet() != null
                            && pathItem.getGet().getSummary() != null
                            && pathItem.getGet().getSummary().startsWith("Get all")) {
                        pathItem.getGet().getResponses().remove("404");
                    }
                }
        );
    }

    private static void removeRedundantCodesFromPost(OpenAPI openApi) {
        removeRedundantCodes(openApi, PathItem::getPost, "400");
    }

    private static void removeRedundantCodesForGet(OpenAPI openApi) {
        removeRedundantCodes(openApi, PathItem::getGet, "404", "409");
    }

    private static void removeRedundantForDelete(OpenAPI openApi) {
        removeRedundantCodes(openApi, PathItem::getDelete, "400", "409");
    }

    private static void removeRedundantCodes(OpenAPI openApi,
                                             Function<PathItem, Operation> methodExtractor,
                                             String... codesToRemove) {
        openApi.getPaths().values().stream().filter(
                pathItem -> methodExtractor.apply(pathItem) != null
        ).forEach(pathItem -> {
            for (var code : codesToRemove) {
                methodExtractor.apply(pathItem).getResponses().remove(code);
            }
        });
    }

    private static void addUnauthorizedAndForbidden(OpenAPI openApi) {
        openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation ->
                        operation.responses(operation.getResponses().addApiResponse(
                                        "401",
                                        new io.swagger.v3.oas.models.responses.ApiResponse()
                                                .description("Unauthorized")
                                ).addApiResponse(
                                        "403",
                                        new io.swagger.v3.oas.models.responses.ApiResponse()
                                                .description("Forbidden")
                                )

                        )
                ));
    }

    @Bean
    public OpenAPI customize() {
        final var securitySchemeName = "bearerAuth";
        return new OpenAPI().components(
                new Components().addSecuritySchemes(
                        securitySchemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                )
        ).security(List.of(new SecurityRequirement().addList(securitySchemeName)));
    }

    @Bean
    public OpenApiCustomizer customizer() {
        return openApi -> {
            removeRedundantCodesForGet(openApi);
            removeRedundantCodesFromPost(openApi);
            removeRedundantForDelete(openApi);
            addUnauthorizedAndForbidden(openApi);
            removeNotFoundFromGetAll(openApi);
        };
    }
}
