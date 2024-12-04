package com.openclassrooms.mdd.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides server related configuration properties
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:05
 *
 */

@ConfigurationProperties(prefix = "server")
@Getter
@Setter
public class ServerProperties {
    private String hostname;

    private String port;

    private String protocol;
}