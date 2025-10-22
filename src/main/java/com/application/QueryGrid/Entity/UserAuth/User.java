package com.application.QueryGrid.Entity.UserAuth;

import com.application.QueryGrid.Entity.Token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qg_users_info")
public class User implements UserDetails {
    @Id
    @Column(length = 30, nullable = false, unique = true)
    private String email;
    @Column(length = 30, nullable = false, unique = true)
    private String username;
    @Column(length = 30, nullable = false, unique = true)
    private String login_name;
    private String repositoryName;
    private String password;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private Role role;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }
    @Override
    public String getUsername() {
        return email;
    }
    public String getActualUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @OneToMany(mappedBy = "userInfo")
    private List<Token> tokens;

    private boolean isLicensed;
}
