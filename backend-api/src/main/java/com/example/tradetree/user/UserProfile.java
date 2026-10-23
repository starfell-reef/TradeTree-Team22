package com.example.tradetree.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(
		name = "user_profiles",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_user_profiles_username", columnNames = "username"),
			@UniqueConstraint(name = "uk_user_profiles_email", columnNames = "email")
		})
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 30)
	private String username;

	@Column(nullable = false, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	protected UserProfile() {
	}

	public UserProfile(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
}
