package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.Token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
        SELECT t FROM Token t WHERE t.userInfo.email = :useremail AND (t.expired = false OR t.revoked = false)
    """)
    List<Token> findAllValidTokensByUser(String useremail);

    Optional<Token> findByToken(String token);

    @Query("""
        SELECT t FROM Token t
        WHERE t.userInfo.email = :user_email
        AND t.expired = false
        AND t.revoked = false
    """)
    Optional<Token> findByUser(String user_email);

    @Modifying
    @Query("""
            DELETE FROM Token t WHERE t.userInfo.email = :userEmail
            """)
    void deleteUserTokens(String userEmail);



}
