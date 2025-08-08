package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.UserAuth.UserLicense.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface LicenseRepository extends JpaRepository<License, String> {

    @Query("""
      SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
        FROM License l
        WHERE l.assignedUser.email = :email
    """)
    boolean existsByAssignedUserEmail(String email);

    @Query("""
            SELECT l FROM License l
            WHERE l.assignedUser.email = :email
            AND l.isExpired = false
            """)
    Optional<License> getLicenseByEmail(String email);

    @Modifying
    @Query("""
            DELETE from License l
            WHERE l.assignedUser.email = :email
            """)
    void deleteLicenseByEmail(String email);
}
