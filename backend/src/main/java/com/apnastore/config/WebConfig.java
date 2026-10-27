package com.apnastore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Enable Cross-Origin requests for the REST APIs
        // Allow the Authorization header for JWT Bearer tokens
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward the root URL ("/") directly to the storefront index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve frontend folder location dynamically based on working directory
        File frontendDir = resolveFrontendDir();
        String frontendUri = frontendDir.toURI().toString();
        if (!frontendUri.endsWith("/")) {
            frontendUri += "/";
        }

        // Serve all static storefront assets (index.html, styles.css, app.js, /assets)
        registry.addResourceHandler("/**")
                .addResourceLocations(frontendUri, "classpath:/static/", "classpath:/public/");
    }

    private File resolveFrontendDir() {
        // Check standard relative locations
        File[] candidates = new File[] {
            new File("../frontend"),     // When launched from backend/ (via mvnw spring-boot:run)
            new File("frontend"),        // When launched from workspace root
            new File("../../frontend")
        };
        for (File candidate : candidates) {
            File testIndex = new File(candidate, "index.html");
            if (testIndex.exists()) {
                return candidate.getAbsoluteFile();
            }
        }
        // Fallback default
        return new File("../frontend").getAbsoluteFile();
    }
}
