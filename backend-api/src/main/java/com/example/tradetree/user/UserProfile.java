package com.example.tradetree.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
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

	@NotBlank
	@Size(max = 30)
	@Column(nullable = false, length = 30)
	private String username;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100, columnDefinition = "varchar(100) not null default 'Unknown'")
	private String name;

	@NotBlank
	@Email
	@Size(max = 254)
	@Column(nullable = false, length = 254)
	private String email;

	@Size(max = 30)
	@Column(name = "phone_number", length = 30)
	private String phoneNumber;

	@NotBlank
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "is_online", nullable = false, columnDefinition = "boolean not null default false")
	private boolean online;

	@NotBlank
	@Column(
			name = "account_status",
			nullable = false,
			length = 30,
			columnDefinition = "varchar(30) not null default 'ACTIVE'")
	private String accountStatus = "ACTIVE";

	@Column(name = "account_age", nullable = false, columnDefinition = "integer not null default 0")
	private Integer accountAge = 0;

	@Column(name = "is_admin", nullable = false, columnDefinition = "boolean not null default false")
	private boolean admin;

	@OneToOne(mappedBy = "userProfile")
	private UserAccountCreation accountCreation;

	@OneToMany(mappedBy = "userProfile")
	private List<UserSignInActivity> signInActivity = new ArrayList<>();

	protected UserProfile() {
	}

	public UserProfile(String username, String name, String email, String passwordHash) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public boolean isOnline() {
		return online;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public Integer getAccountAge() {
		return accountAge;
	}

	public boolean isAdmin() {
		return admin;
	}
}
