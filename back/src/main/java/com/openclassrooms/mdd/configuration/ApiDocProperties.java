package com.openclassrooms.mdd.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:04
 * Provides springdoc related configuration properties
 */
@ConfigurationProperties(prefix="springdoc")
@Getter
@Setter
public class ApiDocProperties {

    /**
     * springdoc.api-docs.* properties
     */
    @NestedConfigurationProperty
    private ApiDocs apiDocs = new ApiDocs();
    public String getApiDocsPath() {
        return apiDocs.getPath();
    }

    /**
     * springdoc.swagger-ui.* properties
     */
    @NestedConfigurationProperty
    private SwaggerUi swaggerUi = new SwaggerUi();

    public String getSwaggerPath() {
        return swaggerUi.getPath();
    }

    @Getter
    @Setter
    public static class ApiDocs {
        private String path;
    }

    @Getter
    @Setter
    public static class SwaggerUi {
        private String path;
    }
}
