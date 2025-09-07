package com.application.QueryGrid.Entity.UserAuth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    ADMINISTRATOR(
            Set.of(
                    Permissions.ADMIN_READ,
                    Permissions.ADMIN_CREATE,
                    Permissions.ADMIN_UPDATE,
                    Permissions.ADMIN_DELETE
            )
    ),
    SUPERUSER(
            Set.of(
                    Permissions.SUPERUSER_READ,
                    Permissions.SUPERUSER_CREATE,
                    Permissions.SUPERUSER_UPDATE,
                    Permissions.SUPERUSER_DELETE
            )
    ),
    COORDINATOR(
            Set.of(
                    Permissions.COORDINATOR_READ,
                    Permissions.COORDINATOR_CREATE,
                    Permissions.COORDINATOR_UPDATE
            )
    ),
    USER(
            Set.of(
                    Permissions.USER_READ,
                    Permissions.USER_CREATE
            )
    );

    @Getter
    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.name())).collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return authorities;
    }
}
