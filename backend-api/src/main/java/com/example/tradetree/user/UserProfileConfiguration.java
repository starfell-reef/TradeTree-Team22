package com.example.tradetree.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class UserProfileConfiguration {

	@Bean
	PasswordEncoder passwordEncoder() {
		return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	}
}
