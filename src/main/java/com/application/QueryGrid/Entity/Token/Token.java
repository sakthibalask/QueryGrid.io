package com.application.QueryGrid.Entity.Token;

import com.application.QueryGrid.Entity.UserAuth.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Table(name = "qg_user_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;
    private boolean revoked;

    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "user_email")
    @JsonIgnore
    private User userInfo;

}
