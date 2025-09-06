package com.application.QueryGrid.io.dto.request.UserAuth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {
    private String email;
    private String username;
    private String login_name;
    private String repositoryName;
    private String password;
}
