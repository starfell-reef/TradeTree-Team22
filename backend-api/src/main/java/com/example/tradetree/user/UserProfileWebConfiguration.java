package com.example.tradetree.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserProfileWebConfiguration implements WebMvcConfigurer {

	private final String[] allowedOrigins;

	public UserProfileWebConfiguration(
			@Value("${app.cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500}")
					String[] allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigins)
				.allowedMethods("GET", "POST", "OPTIONS")
				.allowedHeaders("Content-Type")
				.maxAge(3600);
	}
}
