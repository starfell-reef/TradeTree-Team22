package com.example.tradetree.user;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSignInActivityRepository extends JpaRepository<UserSignInActivity, java.util.UUID> {

	@Query("""
			select count(distinct activity.userProfile.id)
			from UserSignInActivity activity
			where activity.signedInAt >= :start and activity.signedInAt < :end
			""")
	long countDistinctUsersBetween(@Param("start") Instant start, @Param("end") Instant end);
}
