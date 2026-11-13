package com.example.tradetree.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapAdminConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(BootstrapAdminConfiguration.class);

	@Bean
	ApplicationRunner bootstrapAdmin(
			UserProfileService userProfileService,
			@Value("${app.admin.bootstrap.username:}") String username,
			@Value("${app.admin.bootstrap.password:}") String password) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) {
				if (username.isBlank() && password.isBlank()) {
					return;
				}
				if (username.isBlank() || password.isBlank()) {
					throw new IllegalStateException(
							"Set both DEMO_ADMIN_USERNAME and DEMO_ADMIN_PASSWORD to bootstrap the demo admin.");
				}

				if (userProfileService.createBootstrapAdmin(username, password)) {
					logger.info("Created configured demo admin account '{}'.", username.trim());
				} else {
					logger.info("Configured demo admin account '{}' already exists; leaving it unchanged.", username.trim());
				}
			}
		};
	}
}
