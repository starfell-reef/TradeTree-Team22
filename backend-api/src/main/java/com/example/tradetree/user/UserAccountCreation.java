package com.example.tradetree.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_account_creation")
public class UserAccountCreation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	@NotNull
	private UserProfile userProfile;

	@NotNull
	private Instant createdAt;

	protected UserAccountCreation() {
	}

	public UserAccountCreation(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@PrePersist
	void setCreatedAt() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
