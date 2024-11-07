package com.openclassrooms.mdd.configuration;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:05
 */
@Configuration
@PropertySource("file:./.env")
@EnableConfigurationProperties({ ServerProperties.class, ApiDocProperties.class})
public class ApplicationConfiguration {

}
