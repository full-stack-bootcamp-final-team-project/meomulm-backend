package com.meomulm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOriginPatterns(
                                "http://localhost:*",
                                "http://10.0.2.2:*",

                                // Render 프론트
                                "https://*.onrender.com",

                                // 나중에 붙일 실제 도메인
                                "https://meomulm.com",
                                "https://www.meomulm.com"
                        )
                        .allowCredentials(true)
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
