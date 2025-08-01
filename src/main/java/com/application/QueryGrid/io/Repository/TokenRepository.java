package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.Token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("""
        SELECT t FROM Token t WHERE t.userInfo.email = :useremail AND (t.expired = false OR t.revoked = false)
    """)
    List<Token> findAllValidTokensByUser(String useremail);

    @Query("""
        SELECT t FROM Token t 
        WHERE t.userInfo.email = :useremail  
        AND t.expired = false 
        AND t.revoked = false
    """)
    Optional<Token> findByUser(String useremail);
}
