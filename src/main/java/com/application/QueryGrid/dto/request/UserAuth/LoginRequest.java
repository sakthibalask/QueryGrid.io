package com.application.QueryGrid.dto.request.UserAuth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String repositoryName;
    private String loginName;
//    private String email;
    private String password;
}
