package com.example.tradetree.user;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<UserProfile> findByUsername(String username);

}
