package com.application.QueryGrid.dto.request.UserAuth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPatchRequest {
    private String email;
    private String username;
    private String loginName;
    private String repositoryName;
    private boolean isActive;
    private String role;
    private String password;
}
