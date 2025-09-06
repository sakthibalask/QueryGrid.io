package com.application.QueryGrid.io.dto.response.User;

import com.application.QueryGrid.io.Entity.UserAuth.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnUser {
    private String email;
    private String username;
    private String login_name;
    private String repositoryName;
    private Role role;
    private boolean isActive;

}
