package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.UserAuth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);


    @Query("""
            SELECT u FROM User u
            WHERE u.login_name = :loginName
            """)
    Optional<User> findByLoginName(String loginName);
}
