package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.Group.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Groups, String> {
    Set<Groups> findAllByGroupNameIn(Set<String> names);


    @Query("""
            SELECT g FROM Groups g
            JOIN g.users u
            WHERE u.email = :userEmail
            """)
    Optional<Groups> findByuserEmail(String userEmail);
}
