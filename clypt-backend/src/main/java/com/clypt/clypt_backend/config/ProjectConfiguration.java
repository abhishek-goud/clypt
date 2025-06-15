package com.clypt.clypt_backend.config;

import com.cloudinary.Cloudinary;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * ProjectConfiguration provides bean definitions for dependency injection,
 * including the configuration of the Cloudinary service with required credentials.
 */


@Configuration
public class ProjectConfiguration {

    // Cloudinary's configuration values injected from application properties.
    @Value("${cloudinary.cloudName}")
    private String cloudName;

    @Value("${cloudinary.apiKey}")
    private String apiKey;

    @Value("${cloudinary.apiSecret}")
    private String apiSecret;

    /**
     * Provides a configured Cloudinary bean for use throughout the application.
     *
     * @return a Cloudinary instance
     */

    @Bean
    public Cloudinary getCloudinary() {
        Map<Object, Object> config = new HashMap<>();
        config.put("cloud_name", this.cloudName);
        config.put("api_key", this.apiKey);
        config.put("api_secret", this.apiSecret);

        // Ensures secure HTTPS connections.
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
