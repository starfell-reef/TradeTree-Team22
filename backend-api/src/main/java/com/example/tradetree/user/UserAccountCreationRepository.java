package com.example.tradetree.user;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountCreationRepository extends JpaRepository<UserAccountCreation, UUID> {

	@Query("""
			select count(creation.id)
			from UserAccountCreation creation
			where creation.createdAt >= :start and creation.createdAt < :end
			""")
	long countBetween(@Param("start") Instant start, @Param("end") Instant end);
}
