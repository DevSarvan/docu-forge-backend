package com.mycompany.pdfchat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // allow all paths
                .allowedOrigins("http://localhost:4200")  // allow requests from frontend server
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // allow specified methods
                .allowedHeaders("*")  // allow all headers
                .allowCredentials(true);  // if you want to support cookies/auth headers
    }
}
