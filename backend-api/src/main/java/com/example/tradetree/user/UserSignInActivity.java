package com.example.tradetree.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sign_in_activity")
public class UserSignInActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	private UserProfile userProfile;

	@NotNull
	private Instant signedInAt;

	protected UserSignInActivity() {
	}

	public UserSignInActivity(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	@PrePersist
	void setSignedInAt() {
		if (signedInAt == null) {
			signedInAt = Instant.now();
		}
	}
}
