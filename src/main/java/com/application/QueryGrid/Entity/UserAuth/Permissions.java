package com.application.QueryGrid.Entity.UserAuth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permissions {
    ADMIN_READ("admin:READ"),
    ADMIN_CREATE("admin:CREATE"),
    ADMIN_UPDATE("admin:UPDATE"),
    ADMIN_DELETE("admin:DELETE"),

    SUPERUSER_READ("superuser:READ"),
    SUPERUSER_CREATE("superuser:CREATE"),
    SUPERUSER_UPDATE("superuser:UPDATE"),
    SUPERUSER_DELETE("superuser:DELETE"),

    COORDINATOR_READ("coordinator:READ"),
    COORDINATOR_CREATE("coordinator:CREATE"),
    COORDINATOR_UPDATE("coordinator:UPDATE"),

    USER_READ("user:READ"),
    USER_CREATE("user:create")
    ;


    @Getter
    private final String permission;
}
