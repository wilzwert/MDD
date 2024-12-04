package com.openclassrooms.mdd.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Provides springdoc related configuration properties
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:04
 */
@ConfigurationProperties(prefix="springdoc")
@Getter
@Setter
public class ApiDocProperties {

    @NestedConfigurationProperty
    private ApiDocs apiDocs = new ApiDocs();
    public String getApiDocsPath() {
        return apiDocs.getPath();
    }

    @NestedConfigurationProperty
    private SwaggerUi swaggerUi = new SwaggerUi();

    public String getSwaggerPath() {
        return swaggerUi.getPath();
    }

    /**
     *  Api doc related properties
     * found in springdoc.api-docs.*
     * As of now, only 'path' is handled
     */
    @Getter
    @Setter
    public static class ApiDocs {
        private String path;
    }

    /**
     * Swagger ui related properties
     * found in springdoc.swagger-ui.* properties
     * As of now, only 'path' is handled
     */
    @Getter
    @Setter
    public static class SwaggerUi {
        private String path;
    }
}
